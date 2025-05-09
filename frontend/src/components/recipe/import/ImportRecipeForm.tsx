import { mojeWypiekiRegex } from '@/utils/regexes';
import { Container, Input, Field, InputGroup, Group, Button } from '@chakra-ui/react';
import { FC, useState } from 'react';
import { IoIosLink } from 'react-icons/io';
import { toaster } from '@/components/ui/toaster';

interface Props {
  onSuccess?: () => void;
}

const ImportRecipeForm: FC<Props> = ({ onSuccess }: Props) => {
  const [url, setUrl] = useState('');
  const [hasError, setHasError] = useState(false);
  const importURL = 'http://localhost:8080/api/recipes/import/mojewypieki';

  const importRecipe = async () => {
    try {
      const response = await fetch(importURL + `?url=${encodeURIComponent(url)}`, {
        method: 'POST',
        headers: { Accept: 'application/json' },
      });

      if (response.ok) {
        toaster.create({
          title: 'Success',
          description: 'Recipe imported successfully',
          type: 'success',
        });
        onSuccess?.();
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
    if (!mojeWypiekiRegex.test(url)) {
      setHasError(true);
      return;
    }
    setHasError(false);
    (async () => importRecipe())();
  };

  return (
    <Container py={5}>
      <Field.Root invalid={hasError}>
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
                if (hasError) setHasError(false); // reset error on input change
              }}
            />
          </InputGroup>
          <Button bg='bg.subtle' variant='outline' colorPalette='green' onClick={handleClick}>
            Import
          </Button>
        </Group>
        {hasError && <Field.ErrorText>Invalid URL. Must be from https://mojewypieki.com/przepis/…</Field.ErrorText>}
        <Field.HelperText>
          We are only supporting <strong>mojewypieki.com</strong> for now.
        </Field.HelperText>
      </Field.Root>
    </Container>
  );
};

export default ImportRecipeForm;
