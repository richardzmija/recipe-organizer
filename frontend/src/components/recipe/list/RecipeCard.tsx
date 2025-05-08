import { useNavigate } from 'react-router-dom';
import {
  Box,
  Flex,
  Heading,
  Text,
  HStack,
  Badge,
  IconButton,
  Separator,
  VStack,
  Dialog,
  Button,
  CloseButton,
  Portal,
  Checkbox,
} from '@chakra-ui/react';
import { Recipe } from '../../../types/Recipe';
import { FaEdit } from 'react-icons/fa';
import { MdDeleteForever } from 'react-icons/md';
import { toaster } from '@/components/ui/toaster';
import { useRef } from 'react';

interface RecipeCardProps {
  recipe: Recipe;
  onDelete: () => void;
  onSelect: () => void;
  onUnselect: () => void;
}

const RecipeCard = ({ recipe, onDelete, onSelect, onUnselect }: RecipeCardProps) => {
  const navigate = useNavigate();
  const closeRef = useRef<HTMLButtonElement>(null);

  const handleCardClick = () => {
    navigate(`/recipes/${recipe.id}`);
  };

  const handleEditIconClick = () => {
    navigate(`recipes/edit/${recipe.id}`);
  };

  const handleDeleteConfirmation = async (e: React.MouseEvent<HTMLButtonElement>) => {
    e.stopPropagation();
    try {
      await fetch(`http://localhost:8080/api/recipes/${recipe.id}`, {
        method: 'DELETE',
        headers: {
          'Content-Type': 'application/json',
        },
      });
      toaster.create({
        title: 'Success',
        description: 'Recipe deleted successfully',
        type: 'success',
      });
      onDelete();
      // eslint-disable-next-line @typescript-eslint/no-unused-vars
    } catch (error) {
      toaster.create({
        title: 'Delete error',
        description: 'There was a problem with deleting a recipe',
        type: 'error',
      });
    } finally {
      if (closeRef && closeRef.current) closeRef.current.click();
    }
  };

  return (
    <>
      <Box
        className='cursor-pointer'
        onClick={handleCardClick}
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
                {recipe.tags.map((tag) => (
                  <Badge key={tag.id} variant='subtle' shadow='sm' style={{ backgroundColor: tag.color }}>
                    {tag.name}
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
                // src={
                //   recipe.image ||
                //   'https://images.unsplash.com/photo-1546069901-ba9599a7e63c?q=80&w=1000&auto=format&fit=crop'
                // }
                src={recipe.image}
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
          <VStack alignSelf={'stretch'}>
            <IconButton
              onClick={(e) => {
                e.stopPropagation();
                handleEditIconClick();
              }}
              size={'xs'}>
              <FaEdit />
            </IconButton>

            <Dialog.Root>
              <Dialog.Trigger asChild>
                <IconButton
                  onClick={(e) => {
                    e.stopPropagation();
                  }}
                  size={'xs'}>
                  <MdDeleteForever />
                </IconButton>
              </Dialog.Trigger>
              <Portal>
                <Dialog.Backdrop />
                <Dialog.Positioner>
                  <Dialog.Content>
                    <Dialog.Header>
                      <Dialog.Title>Delete</Dialog.Title>
                    </Dialog.Header>
                    <Dialog.Body>
                      <p>Are you sure to delete this recipe?</p>
                      <p>
                        This action is <b>irreversable.</b>
                      </p>
                    </Dialog.Body>
                    <Dialog.Footer>
                      <Dialog.ActionTrigger asChild>
                        <Button onClick={(e) => e.stopPropagation()} variant='outline'>
                          Cancel
                        </Button>
                      </Dialog.ActionTrigger>
                      <Button onClick={(e) => handleDeleteConfirmation(e)}>Delete</Button>
                    </Dialog.Footer>
                    <Dialog.CloseTrigger asChild>
                      <CloseButton size='sm' ref={closeRef} />
                    </Dialog.CloseTrigger>
                  </Dialog.Content>
                </Dialog.Positioner>
              </Portal>
            </Dialog.Root>
            <Checkbox.Root
              size={'md'}
              colorPalette='yellow'
              variant='subtle'
              onClick={(e) => e.stopPropagation()}
              className='cursor-pointer'
              style={{ marginTop: 'auto' }}
              onCheckedChange={(details) => {
                if (details.checked) onSelect();
                else onUnselect();
              }}>
              <Checkbox.HiddenInput />
              <Checkbox.Control />
            </Checkbox.Root>
          </VStack>
        </Flex>
      </Box>
      <Box borderBottom='1px' borderColor='gray.200' my={2} />
    </>
  );
};

export default RecipeCard;
