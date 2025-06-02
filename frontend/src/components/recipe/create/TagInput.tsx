import { HStack, Box, Flex, Badge, Button, VStack, Text } from '@chakra-ui/react';
import { LuX, LuSettings } from 'react-icons/lu';
import { useState } from 'react';
import { Tag } from '@/types/Tag';
import TagManagementModal from './TagManagementModal';

interface Props {
  tags: Tag[];
  onChange: (tags: Tag[]) => void;
}

const TagInput = ({ tags, onChange }: Props) => {
  const [showTagModal, setShowTagModal] = useState(false);

  const handleRemoveTag = (tagId: string) => {
    const newTags = tags.filter((tag) => tag.id !== tagId);
    onChange(newTags);
  };

  return (
    <>
      <VStack align='start' w='100%' gap={2}>
        <HStack w='100%' justify='space-between'>
          <Text fontWeight='bold'>Tags</Text>
          <Button size='sm' onClick={() => setShowTagModal(true)}>
            <HStack>
              <LuSettings />
              <Text>Manage Tags</Text>
            </HStack>
          </Button>
        </HStack>

        {tags.length === 0 ? (
          <Text fontSize='sm'>No tags selected. Use the Manage Tags button to add tags.</Text>
        ) : (
          <Flex wrap='wrap' gap={2}>
            {tags.map((tag) => (
              <Badge
                key={tag.id}
                px={2}
                py={1}
                borderRadius='md'
                variant='subtle'
                shadow='sm'
                style={{ backgroundColor: tag.color }}
                display='flex'
                alignItems='center'>
                <Text>{tag.name}</Text>
                <Box as='span' ml={1} cursor='pointer' onClick={() => handleRemoveTag(tag.id)}>
                  <LuX size={14} />
                </Box>
              </Badge>
            ))}
          </Flex>
        )}
      </VStack>

      <TagManagementModal
        isOpen={showTagModal}
        onClose={() => setShowTagModal(false)}
        selectedTags={tags}
        onTagsChange={onChange}
      />
    </>
  );
};

export default TagInput;
