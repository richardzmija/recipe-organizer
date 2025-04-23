import { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { Container, VStack, Heading, Spinner, Alert, Grid, Card, Text, Badge, HStack, Button } from '@chakra-ui/react';
import { Recipe } from '../types/Recipe';

export default function RecipeList() {
  const [recipes, setRecipes] = useState<Recipe[]>([]);
  const [loading, setLoading] = useState<boolean>(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    const fetchRecipes = async () => {
      try {
        setLoading(true);
        const response = await fetch('http://localhost:8080/recipe');

        if (!response.ok) {
          throw new Error('Failed to fetch recipes');
        }

        const data = await response.json();
        setRecipes(data);
      } catch (err) {
        setError(err instanceof Error ? err.message : 'An unknown error occurred');
      } finally {
        setLoading(false);
      }
    };

    fetchRecipes();
  }, []);

  if (loading) {
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

  if (recipes.length === 0) {
    return (
      <Container maxW='container.lg' py={5}>
        <VStack spacing={5}>
          <Heading as='h1' size='2xl'>
            Recipes
          </Heading>
          <Alert.Root status='info'>
            <Alert.Indicator />
            <Alert.Content>
              <Alert.Title>No recipes found</Alert.Title>
              <Alert.Description>Add your first recipe to get started!</Alert.Description>
            </Alert.Content>
          </Alert.Root>
          <Button as={Link} to='/recipe/create' colorSchema='orange'>
            Create Recipe
          </Button>
        </VStack>
      </Container>
    );
  }

  return (
    <Container maxW='container.lg' py={5}>
      <VStack spacing={5} align='stretch'>
        <Heading as='h1' size='2xl' mb={5}>
          Recipes
        </Heading>

        <Grid templateColumns={{ base: '1fr', md: 'repeat(2, 1fr)', lg: 'repeat(3, 1fr)' }} gap={6}>
          {recipes.map((recipe) => (
            <Link key={recipe.id} to={`/recipe/${recipe.id}`}>
              <Card.Root
                shadow='md'
                variant='outline'
                height='100%'
                _hover={{ shadow: 'xl', transform: 'translateY(-5px)' }}
                transition='all 0.3s'>
                {recipe.image && <Card.Image src={recipe.image} objectFit='cover' height='200px' />}
                <Card.Header>
                  <Heading as='h3' size='lg'>
                    {recipe.name}
                  </Heading>
                </Card.Header>
                <Card.Body>
                  {recipe.description && (
                    <Text noOfLines={2} mb={3}>
                      {recipe.description}
                    </Text>
                  )}

                  {recipe.tags && recipe.tags.length > 0 && (
                    <HStack spaceX={2} wrap='wrap'>
                      {recipe.tags.map((tag, index) => (
                        <Badge key={index} colorPalette='orange' variant='surface' shadow='sm'>
                          {tag}
                        </Badge>
                      ))}
                    </HStack>
                  )}
                </Card.Body>
                <Card.Footer>
                  <Text>Ingredients: {recipe.ingredients.length}</Text>
                  <Text>Steps: {recipe.steps.length}</Text>
                </Card.Footer>
              </Card.Root>
            </Link>
          ))}
        </Grid>

        <Button as={Link} to='/recipe/create' colorSchema='orange' alignSelf='center' my={5}>
          Add New Recipe
        </Button>
      </VStack>
    </Container>
  );
}
