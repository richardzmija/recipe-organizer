import { Box, Button, IconButton, HStack, VStack } from '@chakra-ui/react';
import { LuTrash2 } from 'react-icons/lu';
import { useMemo } from 'react';
import TextEditor from '@/components/common/TextEditor';
import { Step } from '@/types/Step';

interface Props {
  steps: Step[];
  onChange: (steps: Step[]) => void;
}

const editorHeight = '100px';

const StepFields = ({ steps, onChange }: Props) => {
  // Generate stable random keys for each step - fixes weird behavior
  const stepKeys = useMemo(() => {
    return Array.from({ length: steps.length }, () => `step-${Math.random().toString(36).substr(2, 9)}`);
  }, [steps.length]);

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
        <HStack key={stepKeys[i]} align='start' gap={3} w='100%'>
          <Box flexShrink={0} maxW='200px' w='200px'>
            <TextEditor
              value={step.title}
              onChange={(val) => handleChange(i, 'title', val)}
              placeholder='Step name'
              height={editorHeight}
            />
          </Box>

          <Box flex='1'>
            <TextEditor
              value={step.text}
              onChange={(val) => handleChange(i, 'text', val)}
              placeholder='Step description'
              height={editorHeight}
            />
          </Box>

          <IconButton
            aria-label='Remove step'
            variant='outline'
            onClick={() => handleRemove(i)}
            alignSelf='start'
            height={editorHeight}>
            <LuTrash2 />
          </IconButton>
        </HStack>
      ))}

      <Button size='sm' onClick={handleAdd} variant='outline' alignSelf='flex-start'>
        + Add step
      </Button>
    </VStack>
  );
};

export default StepFields;
