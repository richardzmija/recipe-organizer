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
} from '@chakra-ui/react';
import { Card, CardBody } from '@chakra-ui/card';
import { useColorModeValue } from './ui/color-mode';

// Recipe types
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
  name: 'Woda w szklance z cukrem',
  description: 'Szybki przepis na wodę w szklance z cukrem i miodem',
  image: 'https://placehold.co/600x400/EEE/31343C',
  tags: ['Wege', 'Szybkie', 'Łatwe'],
  ingredients: [
    {
      ingredientName: 'Woda',
      metric: 'CUPS',
      quantity: 1,
    },
    {
      ingredientName: 'Cukier',
      metric: 'TEASPOONS',
      quantity: 1,
    },
    {
      ingredientName: 'Miód',
      metric: 'TEASPOONS',
      quantity: 1,
    },
  ],
  steps: [
    {
      title: 'Lanie wody',
      text: 'Wlać kubek wody do szklanki',
    },
    {
      title: 'Dodanie cukru',
      text: 'Dodaj 1 łyżeczkę cukru',
    },
    {
      title: 'Dodanie miodu',
      text: 'Dodaj 1 łyżeczkę miodu',
    },
  ],
};

export default function RecipeDetails() {
  const { id } = useParams<{ id: string }>();
  const [recipe, setRecipe] = useState<Recipe | null>(null);
  const [loading, setLoading] = useState<boolean>(true);
  const [error, setError] = useState<string | null>(null);

  const bgColor = useColorModeValue('white', 'gray.800');
  const borderColor = useColorModeValue('gray.200', 'gray.700');

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
        <Alert.Root>
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
            <Heading as='h1' size='xl'>
              {recipe.name}
            </Heading>

            {recipe.tags && recipe.tags.length > 0 && (
              <HStack spaceX={2}>
                {recipe.tags.map((tag, index) => (
                  <Badge key={index} colorScheme='teal' variant='subtle'>
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
          <Heading as='h2' size='lg' mb={4}>
            Składniki
          </Heading>
          <Card variant='outline' borderColor={borderColor} bg={bgColor}>
            <CardBody>
              <List.Root>
                {recipe.ingredients.map((ingredient, index) => (
                  <List.Item key={index}>
                    {ingredient.ingredientName} {ingredient.quantity} {ingredient.metric.toLowerCase()}
                  </List.Item>
                ))}
              </List.Root>
            </CardBody>
          </Card>
        </Box>

        <Separator />

        <Box>
          <Heading as='h2' size='lg' mb={4}>
            Kroki
          </Heading>
          <List.Root as='ol'>
            {recipe.steps.map((step, index) => (
              <List.Item key={index}>
                <Card variant='outline' borderColor={borderColor} bg={bgColor} mb={2}>
                  <CardBody>
                    <Heading as='h3' size='md' mb={2}>
                      {step.title}
                    </Heading>
                    <Text>{step.text}</Text>
                  </CardBody>
                </Card>
              </List.Item>
            ))}
          </List.Root>
        </Box>
      </VStack>
    </Container>
  );
}
