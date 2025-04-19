import { useState, useEffect } from 'react';
import { useParams } from 'react-router-dom';
import {
  Container,
  Spinner,
  Alert,
  VStack,
  Heading,
  Text,
  List,
  Flex,
  HStack,
  Badge,
  Separator,
  Box,
  Image,
  Card,
} from '@chakra-ui/react';
import StepNavigator from './StepNavigator';

interface Step {
  title: string;
  text: string;
}

interface Ingredient {
  ingredientName: string;
  metric: string;
  quantity: number;
}

interface Recipe {
  id: string;
  name: string;
  description: string;
  image: string;
  tags: string[];
  ingredients: Ingredient[];
  steps: Step[];
}

const mockRecipe: Recipe = {
  id: '67fe8333c122b4095776c449',
  name: 'Water with sugar and honey',
  description: 'Quick recipe for water in a glass with sugar and honey',
  image: 'https://placehold.co/600x400/EEE/31343C',
  tags: ['Vegetarian', 'Quick', 'Easy'],
  ingredients: [
    {
      ingredientName: 'Water',
      metric: 'CUPS',
      quantity: 1,
    },
    {
      ingredientName: 'Sugar',
      metric: 'TEASPOONS',
      quantity: 1,
    },
    {
      ingredientName: 'Honey',
      metric: 'TEASPOONS',
      quantity: 1,
    },
  ],
  steps: [
    {
      title: 'Pour water',
      text: 'Pour water into a glass',
    },
    {
      title: 'Add sugar',
      text: 'Add 1 teaspoon of sugar',
    },
    {
      title: 'Add honey',
      text: 'Add 1 teaspoon of honey',
    },
  ],
};

export default function RecipeDetails() {
  const { id } = useParams<{ id: string }>();
  const [recipe, setRecipe] = useState<Recipe | null>(null);
  const [loading, setLoading] = useState<boolean>(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    setRecipe(mockRecipe);
    setLoading(false);
    setError(null);

    /* const fetchRecipe = async () => {
      try {
        setLoading(true);
        const response = await fetch(`http://localhost:8080/recipe/${id}`);
        
        if (!response.ok) {
          if (response.status === 404) {
            throw new Error('Recipe not found');
          }
          throw new Error('Failed to fetch recipe');
        }
        
        const data = await response.json();
        setRecipe(data);
      } catch (err) {
        setError(err instanceof Error ? err.message : 'An unknown error occurred');
      } finally {
        setLoading(false);
      }
    };

    if (id) {
      fetchRecipe();
    } */
  }, [id]);

  if (loading) {
    return (
      <Container centerContent py={10}>
        <Spinner size='xl' />
      </Container>
    );
  }

  if (error) {
    return (
      <Container maxW='container.md' py={5}>
        <Alert.Root status='error'>
          <Alert.Indicator />
          <Alert.Content>
            <Alert.Title>{error}</Alert.Title>
          </Alert.Content>
        </Alert.Root>
      </Container>
    );
  }

  if (!recipe) {
    return (
      <Container maxW='container.md' py={5}>
        <Alert.Root status='warning'>
          <Alert.Indicator />
          <Alert.Content>
            <Alert.Title>No recipe data available</Alert.Title>
          </Alert.Content>
        </Alert.Root>
      </Container>
    );
  }

  return (
    <Container maxW='container.md' py={5}>
      <VStack align='stretch'>
        <Flex direction={{ base: 'column', md: 'row' }} align='flex-start'>
          <Box flex='2'>
            <Heading as='h1' size='3xl' mb={1}>
              {recipe.name}
            </Heading>

            {recipe.tags && recipe.tags.length > 0 && (
              <HStack spaceX={2} mb={4}>
                {recipe.tags.map((tag, index) => (
                  <Badge key={index} colorPalette='orange' variant='surface' shadow='sm'>
                    {tag}
                  </Badge>
                ))}
              </HStack>
            )}

            {recipe.description && <Text fontSize='md'>{recipe.description}</Text>}
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
                    {ingredient.ingredientName} {ingredient.quantity} {ingredient.metric.toLowerCase()}
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
    </Container>
  );
}
