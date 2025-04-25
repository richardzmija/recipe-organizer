import { Link } from 'react-router-dom';
import { Box, Flex, Heading, Text, HStack, Badge, IconButton, Separator, VStack } from '@chakra-ui/react';
import { Recipe } from '../../../types/Recipe';
import { FaEdit } from 'react-icons/fa';
import { MdDeleteForever } from 'react-icons/md';

interface RecipeCardProps {
  recipe: Recipe;
}

const RecipeCard = ({ recipe }: RecipeCardProps) => {
  return (
    <Link key={recipe.id} to={`/recipes/${recipe.id}`}>
      <Box
        p={6}
        borderWidth='1px'
        borderRadius='md'
        shadow='md'
        _hover={{ shadow: 'xl', borderColor: 'orange.300' }}
        transition='all 0.2s'
        width='100%'>
        <Flex direction={{ base: 'column', md: 'row' }} align='flex-start'>
          <Box flex='2'>
            <Heading as='h3' size='xl' mb={1} dangerouslySetInnerHTML={{ __html: recipe.name }} />

            {recipe.tags && recipe.tags.length > 0 && (
              <HStack gap={2} mb={4}>
                {recipe.tags.map((tag, index) => (
                  <Badge key={index} colorPalette='orange' variant='subtle' shadow='sm'>
                    {tag}
                  </Badge>
                ))}
              </HStack>
            )}

            {recipe.description && <Text fontSize='md' dangerouslySetInnerHTML={{ __html: recipe.description }} />}

            {recipe.ingredients && recipe.ingredients.length > 0 && (
              <Box mt={3} mb={2}>
                <HStack flexWrap='wrap' gap={2}>
                  {recipe.ingredients.map((ingredient, index) => (
                    <Badge key={index} colorPalette='green' variant='subtle' px={2} py={1} borderRadius='full'>
                      {ingredient.ingredientName}
                    </Badge>
                  ))}
                </HStack>
              </Box>
            )}

            <HStack mt={4} gap={4}>
              <Text fontSize='sm' fontWeight='medium'>
                Ingredients: {recipe.ingredients.length}
              </Text>
              <Text fontSize='sm' fontWeight='medium'>
                Steps: {recipe.steps.length}
              </Text>
            </HStack>
          </Box>

          {/* eslint-disable-next-line no-constant-binary-expression */}
          {(recipe.image || true) && ( // placeholder for now TODO: remove
            <Box flex='1' height={{ base: '100px', md: '140px' }} maxW={{ md: '300px' }}>
              <img
                src={
                  recipe.image ||
                  'https://images.unsplash.com/photo-1546069901-ba9599a7e63c?q=80&w=1000&auto=format&fit=crop'
                }
                alt={recipe.name}
                style={{
                  objectFit: 'cover',
                  width: '100%',
                  height: '100%',
                  borderRadius: '8px',
                  boxShadow: '0 4px 8px rgba(0,0,0,0.1)',
                }}
              />
            </Box>
          )}
          <Separator size={'md'} orientation={'vertical'} marginLeft={3} marginRight={3} alignSelf={'stretch'} />
          <VStack>
            <Link to={`/recipes/edit/${recipe.id}`}>
              <IconButton size={'xs'}>
                <FaEdit />
              </IconButton>
            </Link>
            <IconButton size={'xs'}>
              <MdDeleteForever />
            </IconButton>
          </VStack>
        </Flex>
      </Box>
      <Box borderBottom='1px' borderColor='gray.200' my={2} />
    </Link>
  );
};

export default RecipeCard;
