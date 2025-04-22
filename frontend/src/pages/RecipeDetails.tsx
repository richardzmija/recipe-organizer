import { useState, useEffect } from 'react';
import { useParams } from 'react-router-dom';
import { Container, Spinner, Alert } from '@chakra-ui/react';
import RecipeContent from '../components/recipe/viewinvidual/RecipeContent';
import { Recipe } from '../types/Recipe';

export default function RecipeDetails() {
  const { id } = useParams<{ id: string }>();
  const [recipe, setRecipe] = useState<Recipe | null>(null);
  const [loading, setLoading] = useState<boolean>(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
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
      <RecipeContent recipe={recipe} />
    </Container>
  );
}
