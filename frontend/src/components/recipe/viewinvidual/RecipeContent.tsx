import { VStack, Heading, Text, List, Flex, HStack, Badge, Separator, Box, Image, Card } from '@chakra-ui/react';
import StepNavigator from './StepNavigator';
import { Recipe } from '../../../types/Recipe';

interface RecipeContentProps {
  recipe: Recipe;
}

export const RecipeContent = ({ recipe }: RecipeContentProps) => (
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
      {recipe.image && (
        <Box flex='1'>
          <Image src={recipe.image} />
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
                {ingredient.ingredientName} {ingredient.quantity} {ingredient.unit.toLowerCase()}
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

export default RecipeContent;
