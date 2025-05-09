import { useState, useEffect } from 'react';
import { Container, VStack, Spinner, Alert, Text, Box, Flex, Checkbox } from '@chakra-ui/react';
import { Recipe } from '../types/Recipe';
import FilterControls from '../components/recipe/list/FilterControls';
import RecipeCard from '../components/recipe/list/RecipeCard';
import PaginationControls from '../components/recipe/list/PaginationControls';

interface PaginatedResponse {
  content: Recipe[];
  page: {
    size: number;
    number: number;
    totalElements: number;
    totalPages: number;
  };
}

interface FilterParams {
  page: number;
  size: number;
  sort: string;
  direction: 'asc' | 'desc';
  ingredients: string[];
}

export default function RecipeList() {
  const FAVORITES_KEY = 'favoriteRecipeIds';
  const [recipes, setRecipes] = useState<Recipe[]>([]);
  const [selectedIds, setSelectedIds] = useState<string[]>([]);
  const [loading, setLoading] = useState<boolean>(true);
  const [error, setError] = useState<string | null>(null);
  const [pagination, setPagination] = useState({
    size: 10,
    number: 0,
    totalElements: 0,
    totalPages: 0,
  });

  const [favoriteIds, setFavoriteIds] = useState<string[]>(() => {
    const stored = localStorage.getItem(FAVORITES_KEY);
    return stored ? (JSON.parse(stored) as string[]) : [];
  });

  const [showOnlyFavorites, setShowOnlyFavorites] = useState(false);

  useEffect(() => {
    localStorage.setItem(FAVORITES_KEY, JSON.stringify(favoriteIds));
  }, [favoriteIds]);

  const [filterParams, setFilterParams] = useState<FilterParams>({
    page: 0,
    size: 10,
    sort: 'name',
    direction: 'asc',
    ingredients: [],
  });

  const [newIngredient, setNewIngredient] = useState('');

  useEffect(() => {
    fetchRecipes();
  }, [filterParams]);

  const handleRecipeSelect = (id: string) => {
    setSelectedIds((prev) => [...prev, id]);
  };

  const handleRecipeUnselect = (id: string) => {
    setSelectedIds((prev) => prev.filter((r) => r !== id));
  };

  const handleToggleFavorite = (id: string) => {
    setFavoriteIds((prev) => (prev.includes(id) ? prev.filter((i) => i !== id) : [...prev, id]));
  };

  const fetchRecipes = async () => {
    try {
      setLoading(true);

      let url = '';

      if (filterParams.ingredients.length > 0) {
        url = 'http://localhost:8080/api/recipes/filter?';

        filterParams.ingredients.forEach((ingredient, index) => {
          url += `ingredients=${encodeURIComponent(ingredient)}`;
          if (index < filterParams.ingredients.length - 1) {
            url += '&';
          }
        });

        url += `&page=${filterParams.page}&size=${filterParams.size}`;
        url += `&sort=${filterParams.sort}&direction=${filterParams.direction}`;
      } else {
        url = `http://localhost:8080/api/recipes?page=${filterParams.page}&size=${filterParams.size}`;
        url += `&sort=${filterParams.sort}&direction=${filterParams.direction}`;
      }

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
      setRecipes(data.content);
      setPagination(data.page);
    } catch (err) {
      setError(err instanceof Error ? err.message : 'An unknown error occurred');
    } finally {
      setLoading(false);
    }
  };

  const handlePageChange = (newPage: number) => {
    setFilterParams({
      ...filterParams,
      page: newPage,
    });
  };

  const handleOnRecipeDelete = (id: string) => {
    setRecipes((prev) => prev.filter((r) => r.id !== id));
    setSelectedIds((prev) => prev.filter((selectedId) => selectedId !== id));
  };

  const addIngredient = () => {
    if (newIngredient.trim() !== '' && !filterParams.ingredients.includes(newIngredient.trim())) {
      setFilterParams({
        ...filterParams,
        ingredients: [...filterParams.ingredients, newIngredient.trim()],
        page: 0,
      });
      setNewIngredient('');
    }
  };

  const removeIngredient = (ingredient: string) => {
    setFilterParams({
      ...filterParams,
      ingredients: filterParams.ingredients.filter((ing) => ing !== ingredient),
      page: 0,
    });
  };

  const clearFilters = () => {
    setFilterParams({
      page: 0,
      size: 10,
      sort: 'name',
      direction: 'asc',
      ingredients: [],
    });
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
            {filterParams.ingredients.length > 0
              ? 'No recipes match your filter criteria. Try removing some ingredients.'
              : 'Add your first recipe to get started!'}
          </Alert.Description>
        </Alert.Content>
      </Alert.Root>
    </Box>
  );

  const visibleRecipes = showOnlyFavorites ? recipes.filter((r) => r.id && favoriteIds.includes(r.id)) : recipes;

  return (
    <Container maxW='container.xl' py={5}>
      <VStack align='stretch'>
        <FilterControls
          filterParams={filterParams}
          setFilterParams={setFilterParams}
          newIngredient={newIngredient}
          setNewIngredient={setNewIngredient}
          addIngredient={addIngredient}
          removeIngredient={removeIngredient}
          clearFilters={clearFilters}
          selectedIds={selectedIds}
          deleteRecipe={handleOnRecipeDelete}
        />
        <Box alignSelf='flex-end' mb={3}>
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
        </Box>
        {loading && <Spinner size='md' alignSelf='center' my={4} />}

        {!loading && (
          <Flex justifyContent='space-between' alignItems='center' mb={4}>
            <Text fontWeight='medium'>
              {pagination.totalElements} recipes found
              {filterParams.ingredients.length > 0 ? ' matching your filters' : ''}
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

        {!loading && visibleRecipes.length === 0 ? (
          renderEmptyState()
        ) : (
          <VStack align='stretch'>
            {visibleRecipes.map((recipe) => (
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
                isFavorite={favoriteIds.includes(recipe.id ?? '')}
                onToggleFavorite={() => {
                  if (recipe.id) handleToggleFavorite(recipe.id);
                }}
              />
            ))}
          </VStack>
        )}

        <PaginationControls pagination={pagination} handlePageChange={handlePageChange} />
      </VStack>
    </Container>
  );
}
