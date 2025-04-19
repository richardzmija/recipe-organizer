import { Box, SimpleGrid } from '@chakra-ui/react';
import RecipeCard from '@/components/recipe/RecipeCard';

const mockRecipes = Array.from({ length: 8 }).map((_, i) => ({
  id: i + 1,
  title: `Przepis ${i + 1}`,
}));

const RecipeList = () => (
  <Box p={4}>
    <SimpleGrid columns={{ base: 1, sm: 2, md: 3, lg: 4 }} rowGap={4} columnGap={4}>
      {mockRecipes.map((r) => (
        <RecipeCard key={r.id} title={r.title} />
      ))}
    </SimpleGrid>
  </Box>
);

export default RecipeList;
