import { useState, useEffect } from 'react';
import { VStack, Heading, Text, List, Flex, HStack, Badge, Separator, Box, Card } from '@chakra-ui/react';
import StepNavigator from './StepNavigator';
import { Recipe } from '../../../types/Recipe';
import { ImageCarousel } from './ImageCarousel';

interface RecipeContentProps {
  recipe: Recipe;
}

export const RecipeContent = ({ recipe: initialRecipe }: RecipeContentProps) => {
  const [recipe, setRecipe] = useState(initialRecipe);
  const [, setRefreshKey] = useState(0);

  const handleImageUpdate = async () => {
    try {
      const response = await fetch(`http://localhost:8080/api/recipes/${recipe.id}`);
      if (response.ok) {
        const updatedRecipe = await response.json();
        setRecipe(updatedRecipe);
      }
    } catch (error) {
      console.error('Error refreshing recipe data:', error);
    }

    setRefreshKey((prev) => prev + 1);
  };

  useEffect(() => {
    setRecipe(initialRecipe);
  }, [initialRecipe]);

  return (
    <VStack align='stretch'>
      <Flex direction={{ base: 'column', md: 'row' }} align='flex-start'>
        <Box flex='2'>
          <Heading as='h1' size='3xl' mb={1} dangerouslySetInnerHTML={{ __html: recipe.name }} />

          {recipe.tags && recipe.tags.length > 0 && (
            <HStack spaceX={2} mb={4}>
              {recipe.tags.map((tag) => (
                <Badge
                  key={tag.id}
                  colorPalette='orange'
                  variant='surface'
                  shadow='sm'
                  style={{ backgroundColor: tag.color }}>
                  {tag.name}
                </Badge>
              ))}
            </HStack>
          )}

          {recipe.description && <Text fontSize='md' dangerouslySetInnerHTML={{ __html: recipe.description }} />}
        </Box>
        {recipe.images && recipe.images.length > 0 && (
          <Box flex='1'>
            <ImageCarousel images={recipe.images} recipeId={recipe.id || ''} onImageUpdate={handleImageUpdate} />
          </Box>
        )}
      </Flex>

      <Separator />

      <Box>
        <Heading as='h2' size='xl' mb='2'>
          Ingredients
        </Heading>
        <Card.Root variant='outline' shadow='xl'>
          <Card.Body>
            <List.Root>
              {recipe.ingredients.map((ingredient, index) => (
                <List.Item key={index}>
                  {/* idk if we can do something like this, but I know that it may be hard to parse units while webscraping so for now its ok */}
                  {ingredient.ingredientName} {ingredient.quantity} {ingredient.unit?.toLowerCase() || ''}
                </List.Item>
              ))}
            </List.Root>
          </Card.Body>
        </Card.Root>
      </Box>

      <Heading as='h2' size='xl'>
        Steps
      </Heading>
      <StepNavigator steps={recipe.steps} />
    </VStack>
  );
};

export default RecipeContent;
