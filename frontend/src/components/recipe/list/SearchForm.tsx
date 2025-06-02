import React, { useState, useEffect } from 'react';
import {
  Box,
  Text,
  VStack,
  Button,
  Flex,
  Input,
  Badge,
  Wrap,
  WrapItem,
  Select,
  Portal,
  createListCollection,
  Collapsible,
  IconButton,
  Spinner,
} from '@chakra-ui/react';
import { Tag } from '../../../types/Tag';
import { LuChevronDown, LuChevronUp, LuX, LuSearch, LuRotateCcw } from 'react-icons/lu';
import { FAVORITES_TAG_ID } from '@/config/tags';

export interface SearchParams {
  name?: string;
  ingredients: string[];
  tagIds: string[];
  sortField: 'name' | 'created_date' | 'modification_date' | 'last_access_date';
  direction: 'asc' | 'desc';
  pageNumber: number;
  size: number;
}

interface SearchFormProps {
  searchParams: SearchParams;
  onSearchParamsChange: (params: SearchParams) => void;
  onSearch: () => void;
  loading?: boolean;
}

const SearchForm = ({ searchParams, onSearchParamsChange, onSearch, loading = false }: SearchFormProps) => {
  const [isExpanded, setIsExpanded] = useState(false);
  const [availableTags, setAvailableTags] = useState<Tag[]>([]);
  const [newIngredient, setNewIngredient] = useState('');
  const [tagsLoading, setTagsLoading] = useState(false);

  useEffect(() => {
    fetchTags();
  }, []);

  const fetchTags = async () => {
    try {
      setTagsLoading(true);
      const response = await fetch('http://localhost:8080/api/tags?size=100');
      if (response.ok) {
        const data = await response.json();
        setAvailableTags((data.content as Tag[]).filter((t) => t.id !== FAVORITES_TAG_ID));
      }
    } catch (error) {
      console.error('Error fetching tags:', error);
    } finally {
      setTagsLoading(false);
    }
  };

  const updateSearchParams = (updates: Partial<SearchParams>) => {
    onSearchParamsChange({
      ...searchParams,
      ...updates,
      pageNumber: 0,
    });
  };

  const handleNameChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const value = e.target.value;
    updateSearchParams({ name: value || undefined });
  };

  const addIngredient = () => {
    if (newIngredient.trim() !== '' && !searchParams.ingredients.includes(newIngredient.trim())) {
      updateSearchParams({
        ingredients: [...searchParams.ingredients, newIngredient.trim()],
      });
      setNewIngredient('');
    }
  };

  const removeIngredient = (ingredient: string) => {
    updateSearchParams({
      ingredients: searchParams.ingredients.filter((ing) => ing !== ingredient),
    });
  };

  const toggleTag = (tagId: string) => {
    const isSelected = searchParams.tagIds.includes(tagId);
    if (isSelected) {
      updateSearchParams({
        tagIds: searchParams.tagIds.filter((id) => id !== tagId),
      });
    } else {
      updateSearchParams({
        tagIds: [...searchParams.tagIds, tagId],
      });
    }
  };

  const clearAllFilters = () => {
    onSearchParamsChange({
      name: undefined,
      ingredients: [],
      tagIds: [],
      sortField: 'name',
      direction: 'asc',
      pageNumber: 0,
      size: searchParams.size,
    });
  };

  const hasActiveFilters = searchParams.name || searchParams.ingredients.length > 0 || searchParams.tagIds.length > 0;

  const sortFieldCollection = createListCollection({
    items: [
      { label: 'Name', value: 'name' },
      { label: 'Created Date', value: 'created_date' },
      { label: 'Modified Date', value: 'modification_date' },
      { label: 'Last Access Date', value: 'last_access_date' },
    ],
  });

  const directionCollection = createListCollection({
    items: [
      { label: 'Ascending', value: 'asc' },
      { label: 'Descending', value: 'desc' },
    ],
  });

  const selectedTags = availableTags.filter((tag) => searchParams.tagIds.includes(tag.id));

  return (
    <Box borderWidth='1px' borderRadius='md' p={4} mb={4} bg='bg.surface' shadow='sm'>
      <VStack align='stretch' gap={4}>
        <Flex justify='space-between' align='flex-start' mb={-2}>
          <Text fontWeight='bold'>Recipe Name</Text>
          <Flex gap={2} align='center' minH='32px'>
            <Box w='140px' display='flex' justifyContent='flex-end'>
              {hasActiveFilters && (
                <Button colorPalette='red' variant='outline' onClick={clearAllFilters} size='xs'>
                  <LuRotateCcw />
                  Clear All Filters
                </Button>
              )}
            </Box>
            <Box w='20px' display='flex' justifyContent='center'>
              {loading && <Spinner size='sm' />}
            </Box>
          </Flex>
        </Flex>

        <Flex gap={2} align='end'>
          <Box flex='1'>
            <Input
              value={searchParams.name || ''}
              onChange={handleNameChange}
              placeholder='Search by recipe name...'
              onKeyPress={(e) => e.key === 'Enter' && onSearch()}
            />
          </Box>
          <Button colorPalette='blue' onClick={onSearch}>
            <LuSearch />
            Search
          </Button>
          <IconButton
            variant='outline'
            onClick={() => setIsExpanded(!isExpanded)}
            aria-label={isExpanded ? 'Hide filters' : 'Show filters'}>
            {isExpanded ? <LuChevronUp /> : <LuChevronDown />}
          </IconButton>
        </Flex>

        <Collapsible.Root open={isExpanded}>
          <Collapsible.Content>
            <VStack align='stretch' gap={4} pt={2}>
              <Box>
                <Text mb={2} fontWeight='bold'>
                  Filter by Ingredients
                </Text>
                <Flex gap={2} mb={2}>
                  <Input
                    value={newIngredient}
                    onChange={(e) => setNewIngredient(e.target.value)}
                    placeholder='Enter ingredient name'
                    onKeyPress={(e) => e.key === 'Enter' && addIngredient()}
                  />
                  <Button colorPalette='green' onClick={addIngredient}>
                    Add
                  </Button>
                </Flex>
                <Wrap minH='24px'>
                  {searchParams.ingredients.map((ingredient) => (
                    <WrapItem key={ingredient}>
                      <Badge colorPalette='green' borderRadius='full' px={2} py={1} display='flex' alignItems='center'>
                        {ingredient}
                        <Box as='span' ml={1} cursor='pointer' onClick={() => removeIngredient(ingredient)}>
                          <LuX size={12} />
                        </Box>
                      </Badge>
                    </WrapItem>
                  ))}
                </Wrap>
              </Box>

              <Box>
                <Text mb={2} fontWeight='bold'>
                  Filter by Tags
                </Text>
                {tagsLoading ? (
                  <Text fontSize='sm' color='gray.500'>
                    Loading tags...
                  </Text>
                ) : (
                  <>
                    <Box mb={2}>
                      <Text fontSize='sm' mb={1} color='gray.600'>
                        Selected tags:
                      </Text>
                      {selectedTags.length > 0 ? (
                        <Wrap>
                          {selectedTags.map((tag) => (
                            <WrapItem key={tag.id}>
                              <Badge
                                borderRadius='full'
                                px={2}
                                py={1}
                                display='flex'
                                alignItems='center'
                                style={{ backgroundColor: tag.color }}
                                cursor='pointer'
                                onClick={() => toggleTag(tag.id)}>
                                {tag.name}
                                <Box as='span' ml={1}>
                                  <LuX size={12} />
                                </Box>
                              </Badge>
                            </WrapItem>
                          ))}
                        </Wrap>
                      ) : (
                        <Text fontSize='sm' color='gray.500'>
                          None
                        </Text>
                      )}
                    </Box>
                    <Box maxH='200px' overflowY='auto' borderWidth='1px' borderRadius='md' p={2}>
                      <Wrap>
                        {availableTags
                          .filter((tag) => !searchParams.tagIds.includes(tag.id))
                          .map((tag) => (
                            <WrapItem key={tag.id}>
                              <Badge
                                borderRadius='full'
                                px={2}
                                py={1}
                                style={{ backgroundColor: tag.color }}
                                cursor='pointer'
                                onClick={() => toggleTag(tag.id)}
                                _hover={{ opacity: 0.8 }}>
                                {tag.name}
                              </Badge>
                            </WrapItem>
                          ))}
                      </Wrap>
                    </Box>
                  </>
                )}
              </Box>

              <Flex gap={4} wrap='wrap'>
                <Box flex='1' minW='200px'>
                  <Text mb={2} fontWeight='bold'>
                    Sort by
                  </Text>
                  <Select.Root
                    collection={sortFieldCollection}
                    value={[searchParams.sortField]}
                    onValueChange={(details) => {
                      if (details.value?.[0]) {
                        updateSearchParams({
                          sortField: details.value[0] as SearchParams['sortField'],
                        });
                      }
                    }}
                    size='md'>
                    <Select.Control>
                      <Select.Trigger>
                        <Select.ValueText />
                      </Select.Trigger>
                      <Select.IndicatorGroup>
                        <Select.Indicator />
                      </Select.IndicatorGroup>
                    </Select.Control>
                    <Portal>
                      <Select.Positioner>
                        <Select.Content>
                          {sortFieldCollection.items.map((item) => (
                            <Select.Item key={item.value} item={item}>
                              {item.label}
                              <Select.ItemIndicator />
                            </Select.Item>
                          ))}
                        </Select.Content>
                      </Select.Positioner>
                    </Portal>
                  </Select.Root>
                </Box>

                <Box flex='1' minW='150px'>
                  <Text mb={2} fontWeight='bold'>
                    Direction
                  </Text>
                  <Select.Root
                    collection={directionCollection}
                    value={[searchParams.direction]}
                    onValueChange={(details) => {
                      if (details.value?.[0]) {
                        updateSearchParams({
                          direction: details.value[0] as SearchParams['direction'],
                        });
                      }
                    }}
                    size='md'>
                    <Select.Control>
                      <Select.Trigger>
                        <Select.ValueText />
                      </Select.Trigger>
                      <Select.IndicatorGroup>
                        <Select.Indicator />
                      </Select.IndicatorGroup>
                    </Select.Control>
                    <Portal>
                      <Select.Positioner>
                        <Select.Content>
                          {directionCollection.items.map((item) => (
                            <Select.Item key={item.value} item={item}>
                              {item.label}
                              <Select.ItemIndicator />
                            </Select.Item>
                          ))}
                        </Select.Content>
                      </Select.Positioner>
                    </Portal>
                  </Select.Root>
                </Box>
              </Flex>
            </VStack>
          </Collapsible.Content>
        </Collapsible.Root>
      </VStack>
    </Box>
  );
};

export default SearchForm;
