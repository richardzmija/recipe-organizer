import { Input, IconButton, HStack, Box, Flex } from '@chakra-ui/react';
import { LuPlus, LuX } from 'react-icons/lu';
import { useState } from 'react';

interface Props {
  tags: string[];
  onChange: (tags: string[]) => void;
}

const TagInput = ({ tags, onChange }: Props) => {
  const [tagInput, setTagInput] = useState('');

  const handleAdd = () => {
    const trimmed = tagInput.trim();
    if (trimmed && !tags.includes(trimmed)) {
      onChange([...tags, trimmed]);
      setTagInput('');
    }
  };

  const handleRemove = (index: number) => {
    const newTags = tags.filter((_, i) => i !== index);
    onChange(newTags);
  };

  return (
    <>
      <HStack mb={2}>
        <Input value={tagInput} onChange={(e) => setTagInput(e.target.value)} placeholder='Add tag' />
        <IconButton aria-label='Add tag' onClick={handleAdd}>
          <LuPlus />
        </IconButton>
      </HStack>

      <HStack wrap='wrap' gap={2}>
        {tags.map((tag, i) => (
          <Flex key={i} px={2} py={1} borderRadius='md' align='center' gap={1}>
            <Box fontWeight='bold'>{tag}</Box>
            <IconButton aria-label={`Usuń tag ${tag}`} size='xs' variant='ghost' onClick={() => handleRemove(i)}>
              <LuX />
            </IconButton>
          </Flex>
        ))}
      </HStack>
    </>
  );
};

export default TagInput;
