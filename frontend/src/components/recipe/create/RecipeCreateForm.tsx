import { Box, Button, Text, VStack, HStack } from '@chakra-ui/react';
import { Dialog } from '@chakra-ui/react';
import { useState } from 'react';
import { toaster } from '@/components/ui/toaster';
import IngredientFields from './IngredientFields';
import StepFields from './StepFields';
import ImageInput from './ImageInput';
import TagInput from './TagInput';
import { Ingredient } from '@/types/Ingredient';
import { Step } from '@/types/Step';
import TextEditor from '@/components/common/TextEditor';
import RecipeContent from '../viewinvidual/RecipeContent';
import { Recipe } from '@/types/Recipe';

const RecipeCreateForm = ({ onCancel }: { onCancel: () => void }) => {
  const [name, setName] = useState('');
  const [description, setDescription] = useState('');
  const [image, setImage] = useState('');
  const [tags, setTags] = useState<string[]>([]);
  const [ingredients, setIngredients] = useState<Ingredient[]>([]);
  const [steps, setSteps] = useState<Step[]>([]);
  const [showPreview, setShowPreview] = useState(false);

  const handleSave = async () => {
    if (!name.trim() || steps.length === 0 || ingredients.length === 0) {
      toaster.create({
        title: 'Error',
        description: 'Title, ingredients and steps cannot be empty',
        type: 'error',
      });
      return;
    }

    const payload = {
      name,
      description,
      ingredients,
      steps,
    };

    try {
      await fetch('http://localhost:8080/api/recipes', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(payload),
      });

      toaster.create({
        title: 'Success',
        description: 'Recipe has been saved',
        type: 'success',
      });

      onCancel();
    } catch (error) {
      toaster.create({
        title: 'Saving failed',
        description: "Recipe couldn't be saved",
        type: 'error',
      });
      console.error('Save error:', error);
    }
  };

  const openPreview = () => setShowPreview(true);
  const closePreview = () => setShowPreview(false);

  const previewRecipe: Recipe = {
    id: 'preview',
    name,
    description,
    image,
    tags,
    ingredients,
    steps,
  };

  return (
    <Box bg='white' p={6}>
      <VStack align='start' w='100%'>
        <Text fontWeight='bold' color='black'>
          Recipe title
        </Text>
        <TextEditor value={name} onChange={setName} />

        <Text fontWeight='bold' color='black'>
          Description
        </Text>
        <TextEditor value={description} onChange={setDescription} height='150px' />

        <ImageInput value={image} onChange={setImage} />

        <Text fontWeight='bold' color='black'>
          Tags
        </Text>
        <TagInput tags={tags} onChange={setTags} />

        <Text fontWeight='bold' color='black'>
          Ingredients
        </Text>
        <IngredientFields ingredients={ingredients} onChange={setIngredients} />

        <Text fontWeight='bold' color='black'>
          Steps
        </Text>
        <StepFields steps={steps} onChange={setSteps} />

        <HStack pt={4}>
          <Button variant='solid' bg='black' color='white' _hover={{ bg: 'gray.800' }} onClick={handleSave}>
            Save recipe
          </Button>
          <Button variant='outline' color='black' onClick={openPreview} _hover={{ bg: 'black', color: 'white' }}>
            Show preview
          </Button>
          <Button variant='outline' color='black' onClick={onCancel} _hover={{ bg: 'black', color: 'white' }}>
            Cancel
          </Button>
        </HStack>
      </VStack>

      <Dialog.Root open={showPreview}>
        <Dialog.Backdrop />
        <Dialog.Positioner>
          <Dialog.Content maxW='80%' maxH='90vh' overflowY='auto'>
            <Dialog.CloseTrigger />
            <Dialog.Header>
              <Dialog.Title>Recipe Preview</Dialog.Title>
            </Dialog.Header>
            <Dialog.Body>
              <RecipeContent recipe={previewRecipe} />
            </Dialog.Body>
            <Dialog.Footer>
              <Button onClick={closePreview}>Close</Button>
            </Dialog.Footer>
          </Dialog.Content>
        </Dialog.Positioner>
      </Dialog.Root>
    </Box>
  );
};

export default RecipeCreateForm;
