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
import StepNavigator from '../components/recipe/viewinvidual/StepNavigator';
import { Recipe } from '../types/Recipe';

interface RecipeDetailsProps {
  previewRecipe?: Recipe;
  isPreview?: boolean;
}

export default function RecipeDetails({ previewRecipe, isPreview = false }: RecipeDetailsProps) {
  const { id } = useParams<{ id: string }>();
  const [recipe, setRecipe] = useState<Recipe | null>(null);
  const [loading, setLoading] = useState<boolean>(!isPreview);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    if (isPreview && previewRecipe) {
      setRecipe(previewRecipe);
      setLoading(false);
      return;
    }

    const fetchRecipe = async () => {
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
    }
  }, [id, isPreview, previewRecipe]);

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

  const RecipeContent = () => (
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
  );

  if (isPreview) {
    return <RecipeContent />;
  }

  return (
    <Container maxW='container.md' py={5}>
      <RecipeContent />
    </Container>
  );
}
