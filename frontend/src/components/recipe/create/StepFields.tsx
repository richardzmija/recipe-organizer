'use client';

import { Box, Button, IconButton, HStack, VStack } from '@chakra-ui/react';
import { LuTrash2 } from 'react-icons/lu';
import TextEditor from '@/components/common/TextEditor';
import { Step } from '@/types/Step';

interface Props {
  steps: Step[];
  onChange: (steps: Step[]) => void;
}

const StepFields = ({ steps, onChange }: Props) => {
  const handleChange = (index: number, field: keyof Step, value: string) => {
    const updated = [...steps];
    updated[index][field] = value;
    onChange(updated);
  };

  const handleAdd = () => {
    onChange([...steps, { title: '', text: '' }]);
  };

  const handleRemove = (index: number) => {
    onChange(steps.filter((_, i) => i !== index));
  };

  return (
    <VStack gap={4} w='100%' align='stretch'>
      {steps.map((step, i) => (
        <HStack key={i} align='start' gap={3} w='100%'>
          <Box flexShrink={0} maxW='200px' w='200px'>
            <TextEditor value={step.title} onChange={(val) => handleChange(i, 'title', val)} placeholder='Step name' />
          </Box>

          <Box flex='1'>
            <TextEditor
              value={step.text}
              onChange={(val) => handleChange(i, 'text', val)}
              placeholder='Step description'
            />
          </Box>

          <IconButton
            aria-label='Remove step'
            variant='outline'
            color='black'
            onClick={() => handleRemove(i)}
            alignSelf='start'
            minH='77px'>
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
        + Add step
      </Button>
    </VStack>
  );
};

export default StepFields;
