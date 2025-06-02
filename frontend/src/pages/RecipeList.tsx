import { useState, useEffect, useRef } from 'react';
import {
  Container,
  VStack,
  Spinner,
  Alert,
  Text,
  Box,
  Flex,
  Checkbox,
  Button,
  Dialog,
  Portal,
  CloseButton,
} from '@chakra-ui/react';
import { Recipe } from '../types/Recipe';
import SearchForm, { SearchParams } from '../components/recipe/list/SearchForm';
import RecipeCard from '../components/recipe/list/RecipeCard';
import PaginationControls from '../components/recipe/list/PaginationControls';
import { usePaginationContext } from '@/hooks/PaginationContext';
import { toaster } from '@/components/ui/toaster';
import { FAVORITES_TAG_ID, FAVORITES_TAG_NAME } from '@/config/tags';
interface Props {
  refreshSignal: number;
  onRefresh: () => void;
}

interface PaginatedResponse {
  content: Recipe[];
  page: {
    size: number;
    number: number;
    totalElements: number;
    totalPages: number;
  };
}

export default function RecipeList({ refreshSignal, onRefresh }: Props) {
  const [recipes, setRecipes] = useState<Recipe[]>([]);
  const [selectedIds, setSelectedIds] = useState<string[]>([]);
  const [loading, setLoading] = useState<boolean>(true);
  const [searchLoading, setSearchLoading] = useState<boolean>(false);
  const [error, setError] = useState<string | null>(null);
  const [isInitialLoad, setIsInitialLoad] = useState<boolean>(true);
  const {
    pagination,
    setPagination,
    scrollY,
    setScrollY,
    searchParams,
    setSearchParams,
    showOnlyFavorites,
    setShowOnlyFavorites,
  } = usePaginationContext();
  const closeRef = useRef<HTMLButtonElement>(null);
  const recipesFoundRef = useRef<HTMLDivElement>(null);

  useEffect(() => {
    if (searchParams.pageNumber !== pagination.number) {
      setSearchParams({ ...searchParams, pageNumber: pagination.number });
    } else {
      fetchRecipes();
    }
  }, [searchParams]);

  useEffect(() => {
    setPagination({ ...pagination, number: 0 });
    setSearchParams({ ...searchParams, pageNumber: 0 });
    fetchRecipes();
  }, [showOnlyFavorites]);

  useEffect(() => {
    fetchRecipes();
  }, [pagination.number, searchParams]);

  useEffect(() => {
    fetchRecipes();
    setIsInitialLoad(false);
  }, [refreshSignal]);

  const handleRecipeSelect = (id: string) => {
    setSelectedIds((prev) => [...prev, id]);
  };

  const handleRecipeUnselect = (id: string) => {
    setSelectedIds((prev) => prev.filter((r) => r !== id));
  };

  const handleToggleFavorite = async (recipeId: string, isFav: boolean) => {
    const current = recipes.find((r) => r.id === recipeId);
    if (!current) return;

    const favTag = {
      id: FAVORITES_TAG_ID,
      name: FAVORITES_TAG_NAME,
      existingTag: true,
    };

    const requestInit: RequestInit = isFav
      ? {
          method: 'DELETE',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify([favTag]),
        }
      : {
          method: 'PATCH',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify([favTag]),
        };

    try {
      const res = await fetch(`http://localhost:8080/api/recipes/${recipeId}/tags`, requestInit);

      if (res.ok) {
        const updated: Recipe = await res.json();
        setRecipes((prev) => prev.map((r) => (r.id === updated.id ? updated : r)));

        if (showOnlyFavorites) {
          setRecipes((prev) => prev.filter((r) => r.tags?.some((t) => t.id === FAVORITES_TAG_ID)));
        }

        toaster.create({
          title: 'Success',
          description: isFav ? 'Removed from favourites' : 'Added to favourites',
          type: 'success',
        });
      } else {
        const message =
          (await res.json().catch(() => undefined))?.message ||
          (await res.text().catch(() => '')) ||
          'Failed to update favourites';

        toaster.create({
          title: 'Error',
          description: message,
          type: 'error',
        });
      }
    } catch (err) {
      toaster.create({
        title: 'Error',
        description: err instanceof Error ? err.message : 'An unexpected error occurred',
        type: 'error',
      });
    }
  };

  const buildSearchUrl = () => {
    const url = new URL('http://localhost:8080/api/recipes/search/advanced');

    if (searchParams.name) url.searchParams.set('name', searchParams.name);

    searchParams.ingredients.forEach((ing) => url.searchParams.append('ingredients', ing));

    searchParams.tagIds.forEach((tagId) => url.searchParams.append('tagsID', tagId));

    if (showOnlyFavorites) {
      url.searchParams.append('tagsID', FAVORITES_TAG_ID);
    }

    url.searchParams.set('sort_field', searchParams.sortField);
    url.searchParams.set('direction', searchParams.direction);
    url.searchParams.set('page_number', searchParams.pageNumber.toString());
    url.searchParams.set('size', searchParams.size.toString());

    return url.toString();
  };

  const fetchRecipes = async () => {
    try {
      setSearchLoading(true);

      const url = buildSearchUrl();

      const response = await fetch(url, {
        method: 'GET',
        headers: {
          'Content-Type': 'application/json',
        },
      });

      if (!response.ok) {
        throw new Error('Failed to fetch recipes');
      }

      const data: PaginatedResponse = await response.json();

      if (isInitialLoad) {
        window.scrollTo(0, scrollY);
        setIsInitialLoad(false);
      }

      setRecipes(data.content);
      setPagination(data.page);
      setError(null);
    } catch (err) {
      setError(err instanceof Error ? err.message : 'An unknown error occurred');
    } finally {
      setLoading(false);
      setSearchLoading(false);
    }
  };

  const handlePageChange = (newPage: number) => {
    setPagination({
      ...pagination,
      number: newPage,
    });
    setSearchParams({
      ...searchParams,
      pageNumber: newPage,
    });

    if (recipesFoundRef.current) {
      recipesFoundRef.current.scrollIntoView({ behavior: 'instant', block: 'start' });
      setScrollY(recipesFoundRef.current.offsetTop);
    }
  };

  const handleOnRecipeDelete = (id: string) => {
    setRecipes((prev) => prev.filter((r) => r.id !== id));
    setSelectedIds((prev) => prev.filter((selectedId) => selectedId !== id));
  };

  const handleDeleteSelected = async () => {
    if (selectedIds.length === 0) return;

    try {
      const deleteRequests = selectedIds.map((id) =>
        fetch(`http://localhost:8080/api/recipes/${id}`, {
          method: 'DELETE',
          headers: {
            'Content-Type': 'application/json',
          },
        }),
      );

      const responses = await Promise.all(deleteRequests);

      responses.forEach((res, index) => {
        if (!res.ok) {
          toaster.create({
            title: 'Delete error',
            description: `There was a problem with deleting a recipe ${selectedIds[index]}`,
            type: 'error',
          });
        } else {
          toaster.create({
            title: 'Delete success',
            description: `Recipe ${selectedIds[index]} deleted`,
            type: 'success',
          });
          handleOnRecipeDelete(selectedIds[index]);
        }
      });
    } catch (error) {
      console.error('Error deleting recipes:', error);
      toaster.create({
        title: 'Delete error',
        description: `There was a problem with deleting a recipes`,
        type: 'error',
      });
    } finally {
      if (closeRef && closeRef.current) closeRef.current.click();
    }
  };

  const handleSearchParamsChange = (params: SearchParams) => {
    setIsInitialLoad(false);
    setSearchParams({ ...params, pageNumber: 0 });
    setPagination({ ...pagination, number: 0 });
  };

  const handleSearch = () => {
    setIsInitialLoad(false);
    setSearchParams({ ...searchParams, pageNumber: 0 });
    setPagination({ ...pagination, number: 0 });
  };

  if (loading && recipes.length === 0) {
    return (
      <Container centerContent py={10}>
        <Spinner size='xl' />
      </Container>
    );
  }

  if (error) {
    return (
      <Container maxW='container.lg' py={5}>
        <Alert.Root status='error'>
          <Alert.Indicator />
          <Alert.Content>
            <Alert.Title>{error}</Alert.Title>
          </Alert.Content>
        </Alert.Root>
      </Container>
    );
  }

  const renderEmptyState = () => (
    <Box textAlign='center' py={10}>
      <Alert.Root status='info'>
        <Alert.Indicator />
        <Alert.Content>
          <Alert.Title>No recipes found</Alert.Title>
          <Alert.Description>
            {searchParams.ingredients.length > 0 || searchParams.tagIds.length > 0 || searchParams.name
              ? 'No recipes match your search criteria. Try adjusting your filters.'
              : 'Add your first recipe to get started!'}
          </Alert.Description>
        </Alert.Content>
      </Alert.Root>
    </Box>
  );

  return (
    <Container maxW='container.xl' py={5}>
      <VStack align='stretch'>
        <SearchForm
          searchParams={searchParams}
          onSearchParamsChange={handleSearchParamsChange}
          onSearch={handleSearch}
          loading={searchLoading}
        />

        <Flex justifyContent='flex-end' alignItems='center' gap={3} mb={3}>
          <Checkbox.Root
            checked={showOnlyFavorites}
            onCheckedChange={(e) => setShowOnlyFavorites(!!e.checked)}
            size='md'
            variant='subtle'
            className='cursor-pointer'>
            <Checkbox.HiddenInput />
            <Checkbox.Control />
            <Checkbox.Label>Show only favorites</Checkbox.Label>
          </Checkbox.Root>

          <Dialog.Root>
            <Dialog.Trigger asChild>
              <Button colorPalette='red' variant='surface' disabled={!(selectedIds.length > 0)} size='sm'>
                Delete selected
              </Button>
            </Dialog.Trigger>
            <Portal>
              <Dialog.Backdrop />
              <Dialog.Positioner>
                <Dialog.Content>
                  <Dialog.Header>
                    <Dialog.Title>Delete</Dialog.Title>
                  </Dialog.Header>
                  <Dialog.Body>
                    <p>
                      Are you sure to delete {selectedIds.length} recipe{selectedIds.length === 1 ? '' : 's'}?
                    </p>
                    <p>
                      This action is <b>irreversable.</b>
                    </p>
                  </Dialog.Body>
                  <Dialog.Footer>
                    <Dialog.ActionTrigger asChild>
                      <Button variant='outline'>Cancel</Button>
                    </Dialog.ActionTrigger>
                    <Button onClick={() => handleDeleteSelected()}>Delete</Button>
                  </Dialog.Footer>
                  <Dialog.CloseTrigger asChild>
                    <CloseButton size='sm' ref={closeRef} />
                  </Dialog.CloseTrigger>
                </Dialog.Content>
              </Dialog.Positioner>
            </Portal>
          </Dialog.Root>
        </Flex>

        {!loading && (
          <Flex justifyContent='space-between' alignItems='center' mb={4} ref={recipesFoundRef}>
            <Text fontWeight='medium'>
              {pagination.totalElements} recipes found
              {searchParams.ingredients.length > 0 || searchParams.tagIds.length > 0 || searchParams.name
                ? ' matching your search'
                : ''}
            </Text>
            {recipes.length > 0 && (
              <Text fontSize='sm'>
                Showing {pagination.number * pagination.size + 1}-
                {Math.min((pagination.number + 1) * pagination.size, pagination.totalElements)} of{' '}
                {pagination.totalElements}
              </Text>
            )}
          </Flex>
        )}

        {!loading && recipes.length === 0 ? (
          renderEmptyState()
        ) : (
          <VStack align='stretch'>
            {recipes.map((recipe) => {
              const isFav = recipe.tags?.some((t) => t.id === FAVORITES_TAG_ID);

              return (
                <RecipeCard
                  key={recipe.id}
                  recipe={recipe}
                  onDelete={() => {
                    if (recipe.id) handleOnRecipeDelete(recipe.id);
                  }}
                  onSelect={() => {
                    if (recipe.id) handleRecipeSelect(recipe.id);
                  }}
                  onUnselect={() => {
                    if (recipe.id) handleRecipeUnselect(recipe.id);
                  }}
                  isFavorite={isFav}
                  onToggleFavorite={() => recipe.id && handleToggleFavorite(recipe.id, isFav)}
                  onPhotoUploadSuccess={onRefresh}
                />
              );
            })}
          </VStack>
        )}

        <PaginationControls pagination={pagination} handlePageChange={handlePageChange} />
      </VStack>
    </Container>
  );
}
