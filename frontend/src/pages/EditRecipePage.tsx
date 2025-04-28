import RecipeCreateForm from '@/components/recipe/create/RecipeCreateForm';
import { Recipe } from '@/types/Recipe';
import { Container, Spinner, Alert } from '@chakra-ui/react';
import { FC, useEffect, useState } from 'react';
import { useNavigate, useParams } from 'react-router-dom';

const EditRecipePage: FC = () => {
  const { id } = useParams<{ id: string }>();
  const [recipe, setRecipe] = useState<Recipe | null>(null);
  const [loading, setLoading] = useState<boolean>(true);
  const [error, setError] = useState<string | null>(null);
  const navigate = useNavigate();

  useEffect(() => {
    const fetchRecipe = async () => {
      try {
        setLoading(true);
        const response = await fetch(`http://localhost:8080/api/recipes/${id}`);

        if (!response.ok) {
          if (response.status === 404) {
            throw new Error('Recipe not found');
          }
          throw new Error('Failed to fetch recipe');
        }

        const data = await response.json();
        console.log(data);
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
    <RecipeCreateForm
      mode='edit'
      id={recipe.id}
      description={recipe.description}
      ingredients={recipe.ingredients}
      steps={recipe.steps}
      image={recipe.image}
      tags={recipe.tags}
      name={recipe.name.replace(/<[^>]*>?/gm, '')}
      onCancel={() => navigate('/')}
    />
  );
};

export default EditRecipePage;
