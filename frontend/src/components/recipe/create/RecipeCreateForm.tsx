import { Box, Button, Text, VStack, HStack, Input } from '@chakra-ui/react';
import { Dialog } from '@chakra-ui/react';
import { FC, useState } from 'react';
import { toaster } from '@/components/ui/toaster';
import IngredientFields from './IngredientFields';
import StepFields from './StepFields';
import TagInput from './TagInput';
import { Ingredient } from '@/types/Ingredient';
import { Step } from '@/types/Step';
import { Tag } from '@/types/Tag';
import TextEditor from '@/components/common/TextEditor';
import RecipeContent from '../viewinvidual/RecipeContent';
import { Recipe, Image } from '@/types/Recipe';

interface Props {
  id?: string;
  name?: string;
  description?: string;
  tags?: Tag[];
  ingredients?: Ingredient[];
  steps?: Step[];
  images?: Image[];
  onCancel: () => void;
  mode: 'edit' | 'create';
}

const RecipeCreateForm: FC<Props> = (props: Props) => {
  const [name, setName] = useState(props.name || '');
  const [description, setDescription] = useState(props.description || '');
  const [tags, setTags] = useState<Tag[]>(props.tags || []);
  const [ingredients, setIngredients] = useState<Ingredient[]>(props.ingredients || []);
  const [steps, setSteps] = useState<Step[]>(props.steps || []);
  const [images] = useState(props.images || []);
  const [showPreview, setShowPreview] = useState(false);
  const { onCancel, mode } = props;

  const handleSave = async () => {
    if (!name.trim() || steps.length === 0 || ingredients.length === 0) {
      toaster.create({
        title: 'Error',
        description: 'Title, ingredients and steps cannot be empty',
        type: 'error',
      });
      return;
    }

    if (ingredients.some((ingredient) => ingredient.quantity <= 0)) {
      toaster.create({
        title: 'Error',
        description: 'Ingredient quantity has to be greater than 0.',
        type: 'error',
      });
      return;
    }

    const tagReferences = tags.map((tag) => ({
      id: tag.id,
    }));

    const payload = {
      name,
      description,
      ingredients,
      steps,
      tags: tagReferences,
    };

    try {
      let savedRecipeId = '';

      if (mode === 'create') {
        const response = await fetch('http://localhost:8080/api/recipes', {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify(payload),
        });

        if (response.ok) {
          const data = await response.json();
          savedRecipeId = data.id;
        }
      }

      if (mode === 'edit') {
        const response = await fetch(`http://localhost:8080/api/recipes/${props.id}`, {
          method: 'PUT',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify(payload),
        });

        if (response.ok) {
          savedRecipeId = props.id as string;

          if (images && images.length > 0) {
            try {
              const imageIdsToPreserve = images.map((img) => img.id);

              await fetch(`http://localhost:8080/api/recipes/${savedRecipeId}/images/link`, {
                method: 'PATCH',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(imageIdsToPreserve),
              });
            } catch (imageError) {
              console.error('Error preserving images:', imageError);
              toaster.create({
                title: 'Warning',
                description: 'Recipe was updated but there was an issue preserving the images',
                type: 'warning',
              });
            }
          }
        }
      }

      toaster.create({
        title: 'Success',
        description: mode === 'create' ? 'Recipe has been saved' : 'Recipe has been updated',
        type: 'success',
      });

      onCancel();
    } catch (error) {
      toaster.create({
        title: 'Saving failed',
        description: mode === 'create' ? "Recipe couldn't be saved" : "Recipe couldn't be saved",
        type: 'error',
      });
      console.error('Save error:', error);
    }
  };

  const openPreview = () => {
    if (!name.trim() || steps.length === 0 || ingredients.length === 0) {
      toaster.create({
        title: 'Error',
        description: 'Title, ingredients and steps are required to preview',
        type: 'error',
      });
      return;
    }
    setShowPreview(true);
  };

  const closePreview = () => setShowPreview(false);

  const previewRecipe: Recipe = {
    id: 'preview',
    name,
    description,
    images: images,
    tags,
    ingredients,
    steps,
  };

  return (
    <Box p={6}>
      <VStack align='start' w='100%'>
        <Text fontWeight='bold'>Recipe title</Text>
        <Input value={name} onChange={(e) => setName(e.target.value)} />

        <Text fontWeight='bold'>Description</Text>
        <TextEditor value={description} onChange={setDescription} height='150px' />

        <TagInput tags={tags} onChange={setTags} />

        <Text fontWeight='bold'>Ingredients</Text>
        <IngredientFields ingredients={ingredients} onChange={setIngredients} />

        <Text fontWeight='bold'>Steps</Text>
        <StepFields steps={steps} onChange={setSteps} />

        <HStack pt={4}>
          <Button variant='solid' onClick={handleSave}>
            Save recipe
          </Button>
          <Button variant='outline' onClick={openPreview}>
            Show preview
          </Button>
          <Button variant='outline' onClick={onCancel}>
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
