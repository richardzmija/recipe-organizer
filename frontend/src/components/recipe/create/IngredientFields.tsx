'use client';

import { Input, HStack, VStack, Button, IconButton, NumberInput } from '@chakra-ui/react';
import { LuTrash2 } from 'react-icons/lu';
import MetricSelect from '@/components/common/selects/MetricSelect';
import { Ingredient } from '@/types/Ingredient';

interface Props {
  ingredients: Ingredient[];
  onChange: (ingredients: Ingredient[]) => void;
}

const IngredientFields = ({ ingredients, onChange }: Props) => {
  const handleChange = (index: number, field: keyof Ingredient, value: string | number) => {
    const updated = [...ingredients];
    updated[index] = { ...updated[index], [field]: value };
    onChange(updated);
  };

  const handleAdd = () => {
    onChange([...ingredients, { ingredientName: '', metric: 'GRAMS', quantity: 0 }]);
  };

  const handleRemove = (index: number) => {
    const updated = ingredients.filter((_, i) => i !== index);
    onChange(updated);
  };

  return (
    <VStack gap={3} w='100%' align='stretch'>
      {ingredients.map((ingredient, i) => (
        <HStack key={i} gap={3}>
          <Input
            placeholder='Ingredient'
            color='black'
            value={ingredient.ingredientName}
            onChange={(e) => handleChange(i, 'ingredientName', e.target.value)}
          />

          <MetricSelect value={ingredient.metric} onChange={(val) => handleChange(i, 'metric', val)} />

          <NumberInput.Root
            min={0}
            max={10000}
            allowOverflow={false}
            value={String(ingredient.quantity)}
            onValueChange={(details) => handleChange(i, 'quantity', Number(details.value))}
            size='xs'
            width='170px'>
            <NumberInput.Control />
            <NumberInput.Input color='black' />
          </NumberInput.Root>

          <IconButton aria-label='Delete ingredient' variant='outline' color='black' onClick={() => handleRemove(i)}>
            <LuTrash2 />
          </IconButton>
        </HStack>
      ))}

      <Button
        size='sm'
        onClick={handleAdd}
        variant='outline'
        color='black'
        _hover={{ bg: 'black', color: 'white' }}
        alignSelf='flex-start'>
        + Add ingredient
      </Button>
    </VStack>
  );
};

export default IngredientFields;
