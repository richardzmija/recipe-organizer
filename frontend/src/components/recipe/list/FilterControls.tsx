import React from 'react';
import { Box, Text, Heading, HStack, Button, Flex, Input, Badge, Wrap, WrapItem } from '@chakra-ui/react';

interface FilterParams {
  page: number;
  size: number;
  sort: string;
  direction: 'asc' | 'desc';
  ingredients: string[];
}

interface FilterControlsProps {
  filterParams: FilterParams;
  setFilterParams: React.Dispatch<React.SetStateAction<FilterParams>>;
  newIngredient: string;
  setNewIngredient: React.Dispatch<React.SetStateAction<string>>;
  addIngredient: () => void;
  removeIngredient: (ingredient: string) => void;
  clearFilters: () => void;
}

const FilterControls = ({
  filterParams,
  setFilterParams,
  newIngredient,
  setNewIngredient,
  addIngredient,
  removeIngredient,
  clearFilters,
}: FilterControlsProps) => {
  return (
    <Box borderWidth='1px' borderRadius='md' p={4} mb={4} bg='bg.surface' shadow='sm'>
      <Heading as='h3' size='md' mb={4}>
        Recipe Filters
      </Heading>

      <Flex direction={{ base: 'column', md: 'row' }} gap={4} mb={4} align='end'>
        <Box>
          <Text mb={2} fontWeight='bold'>
            Sort By:
          </Text>
          <HStack>
            <Button
              size='sm'
              colorPalette={filterParams.sort === 'name' ? 'blue' : 'gray'}
              onClick={() => setFilterParams({ ...filterParams, sort: 'name', page: 0 })}>
              {filterParams.sort === 'name' ? '✓ ' : ''} Name
            </Button>
            <Button
              size='sm'
              colorPalette={filterParams.sort === 'description' ? 'blue' : 'gray'}
              onClick={() => setFilterParams({ ...filterParams, sort: 'description', page: 0 })}>
              {filterParams.sort === 'description' ? '✓ ' : ''} Description
            </Button>
          </HStack>
        </Box>

        <Box>
          <Text mb={2} fontWeight='bold'>
            Order:
          </Text>
          <HStack>
            <Button
              size='sm'
              colorPalette={filterParams.direction === 'asc' ? 'blue' : 'gray'}
              onClick={() => setFilterParams({ ...filterParams, direction: 'asc', page: 0 })}>
              {filterParams.direction === 'asc' ? '↑ ' : ''} Ascending
            </Button>
            <Button
              size='sm'
              colorPalette={filterParams.direction === 'desc' ? 'blue' : 'gray'}
              onClick={() => setFilterParams({ ...filterParams, direction: 'desc', page: 0 })}>
              {filterParams.direction === 'desc' ? '↓ ' : ''} Descending
            </Button>
          </HStack>
        </Box>

        <Box flex='1'>
          <Text mb={2} fontWeight='bold'>
            Filter by Ingredient:
          </Text>
          <Flex>
            <Input
              value={newIngredient}
              onChange={(e) => setNewIngredient(e.target.value)}
              placeholder='Enter ingredient'
              onKeyPress={(e) => e.key === 'Enter' && addIngredient()}
            />
            <Button ml={2} colorPalette='green' onClick={addIngredient}>
              Add
            </Button>
          </Flex>
        </Box>

        {filterParams.ingredients.length > 0 && (
          <Button colorPalette='red' variant='outline' onClick={clearFilters}>
            Reset All Filters
          </Button>
        )}
      </Flex>

      {filterParams.ingredients.length > 0 && (
        <Box mt={2} p={3} borderWidth='1px' borderRadius='md' bg='bg.subtle' borderColor='border.emphasized'>
          <Text fontSize='sm' fontWeight='bold' mb={2}>
            Active Filters: {filterParams.ingredients.length} ingredient{filterParams.ingredients.length > 1 ? 's' : ''}
          </Text>
          <Wrap>
            {filterParams.ingredients.map((ingredient) => (
              <WrapItem key={ingredient}>
                <Badge colorPalette='blue' borderRadius='full' px={2} py={1} display='flex' alignItems='center'>
                  {ingredient}
                  <Box as='span' ml={1} cursor='pointer' onClick={() => removeIngredient(ingredient)} fontWeight='bold'>
                    ×
                  </Box>
                </Badge>
              </WrapItem>
            ))}
          </Wrap>
        </Box>
      )}
    </Box>
  );
};

export default FilterControls;
