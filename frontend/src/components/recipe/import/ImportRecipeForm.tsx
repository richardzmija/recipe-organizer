import { Container, Input, Field, InputGroup, Group, Button, List, Box, Dialog } from '@chakra-ui/react';
import { FC, useState } from 'react';
import { IoIosLink } from 'react-icons/io';
import { toaster } from '@/components/ui/toaster';
import { ImportResponse } from '@/types/Import';
import { Recipe } from '@/types/Recipe';
import { Tooltip } from '@/components/ui/tooltip';
import RecipeCreateForm from '../create/RecipeCreateForm';

interface Props {
  onSuccess?: () => void;
}

const ImportRecipeForm: FC<Props> = () => {
  const [url, setUrl] = useState('');
  const [recipe, setRecipe] = useState<Recipe | null>(null);
  const [openEditDialog, setOpenEditDialog] = useState(false);
  const importURL = 'http://localhost:8080/api/recipes/import/jobs';

  const submitImportJob = async () => {
    try {
      const response = await fetch(importURL, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ url }),
      });

      if (!response.ok) {
        toaster.create({
          title: 'Error',
          description: `Cannot import recipe from URL: ${url}.`,
          type: 'error',
        });
        return;
      }

      const job = (await response.json()) as ImportResponse;

      return job;
    } catch (error) {
      toaster.create({
        title: 'Error',
        description: `Cannot import recipe.\n ${error}`,
        type: 'error',
      });
    }
  };

  const importRecipe = async (jobId: string, wait: boolean = true) => {
    try {
      const response = await fetch(importURL + '/' + jobId);
      if (!response.ok) {
        toaster.create({
          title: 'Error',
          description: `Cannot import recipe from URL: ${url}.`,
          type: 'error',
        });
        return;
      }

      const job = (await response.json()) as ImportResponse;

      switch (job.status) {
        case 'STARTED':
          return;
        case 'IN_PROGRESS':
          if (wait) setTimeout(() => importRecipe(jobId, false), 5000);
          else
            toaster.create({
              title: 'Error',
              description: `Importing recipe from URL: ${url} took too long.`,
              type: 'error',
            });
          return;
        case 'COMPLETED':
          setRecipe(job.result);
          setOpenEditDialog(true);
          return;
        case 'FAILED':
          toaster.create({
            title: 'Error',
            description: `Cannot import recipe from URL: ${url}.`,
            type: 'error',
          });
          return;
      }
    } catch (error) {
      toaster.create({
        title: 'Error',
        description: `Cannot import recipe.\n ${error}`,
        type: 'error',
      });
    }
  };

  const handleClick = () => {
    (async () => {
      const job = await submitImportJob();
      if (!job) return;
      await importRecipe(job.jobId);
    })();
  };

  return (
    <>
      <Container py={5}>
        <Field.Root>
          <Field.Label>
            URL <Field.RequiredIndicator />
          </Field.Label>
          <Group attached w='full'>
            <InputGroup startElement={<IoIosLink />}>
              <Input
                placeholder='Enter URL of recipe'
                value={url}
                onChange={(e) => {
                  setUrl(e.target.value);
                }}
              />
            </InputGroup>
            <Button variant='solid' colorPalette='green' onClick={handleClick}>
              Import
            </Button>
          </Group>
          <Field.HelperText>
            <Tooltip
              openDelay={10}
              closeDelay={10}
              content={
                <Box p={2}>
                  <List.Root>
                    <List.Item>aniagotuje.pl</List.Item>
                    <List.Item>mojewypieki.com</List.Item>
                    <List.Item>kwestiasmaku.com</List.Item>
                    <List.Item>przepisy.pl</List.Item>
                    <List.Item>poprostupycha.com.pl</List.Item>
                  </List.Root>
                </Box>
              }
              positioning={{ placement: 'bottom' }}>
              <u>
                <strong>Supported URLs</strong>
              </u>
            </Tooltip>
          </Field.HelperText>
        </Field.Root>
      </Container>
      <Dialog.Root open={openEditDialog} onInteractOutside={() => setOpenEditDialog(false)}>
        <Dialog.Backdrop />
        <Dialog.Positioner>
          <Dialog.Content maxW='80%' maxH='90vh' overflowY='auto'>
            <Dialog.CloseTrigger />
            <Dialog.Header>
              <Dialog.Title>Imported Recipe</Dialog.Title>
            </Dialog.Header>
            <Dialog.Body>
              {recipe && (
                <RecipeCreateForm
                  mode='create'
                  name={recipe?.name}
                  ingredients={recipe.ingredients}
                  steps={recipe?.steps}
                  tags={recipe?.tags}
                  description={recipe?.description}
                  onCancel={() => setOpenEditDialog(false)}
                />
              )}
            </Dialog.Body>
            <Dialog.Footer>
              <Button onClick={() => setOpenEditDialog(false)}>Close</Button>
            </Dialog.Footer>
          </Dialog.Content>
        </Dialog.Positioner>
      </Dialog.Root>
    </>
  );
};

export default ImportRecipeForm;
