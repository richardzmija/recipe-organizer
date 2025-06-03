import {
  Dialog,
  Button,
  VStack,
  HStack,
  Input,
  Text,
  Box,
  Badge,
  IconButton,
  Select,
  Portal,
  createListCollection,
  Table,
  ListCollection,
} from '@chakra-ui/react';
import { useState, useEffect, useRef } from 'react';
import { Tag } from '@/types/Tag';
import { toaster } from '@/components/ui/toaster';
import { LuPlus, LuX, LuSettings, LuSave } from 'react-icons/lu';
import { FAVORITES_TAG_ID, FAVORITES_TAG_NAME } from '@/config/tags';

interface TagManagementModalProps {
  isOpen: boolean;
  onClose: () => void;
  selectedTags: Tag[];
  onTagsChange: (tags: Tag[]) => void;
}

interface CollectionItem {
  label: string;
  value: string;
}

const TagManagementModal = ({ isOpen, onClose, selectedTags, onTagsChange }: TagManagementModalProps) => {
  const [tags, setTags] = useState<Tag[]>([]);
  const [loading, setLoading] = useState(false);
  const [activeTab, setActiveTab] = useState(0);
  const modalRef = useRef<HTMLDivElement>(null);

  const [editingTag, setEditingTag] = useState<Tag | null>(null);
  const [tagName, setTagName] = useState('');
  const [tagColor, setTagColor] = useState('#FF5733');
  const [tagDescription, setTagDescription] = useState('');
  const [tagCategory, setTagCategory] = useState('Other');

  const [searchTerm, setSearchTerm] = useState('');
  const [categoryFilter, setCategoryFilter] = useState('All');

  useEffect(() => {
    if (isOpen) {
      fetchTags();
    }
  }, [isOpen]);

  const fetchTags = async () => {
    try {
      setLoading(true);
      const response = await fetch('http://localhost:8080/api/tags?size=100');
      if (response.ok) {
        const data = await response.json();
        setTags(data.content || []);
      } else {
        toaster.create({
          title: 'Error',
          description: 'Failed to fetch tags',
          type: 'error',
        });
      }
    } catch (error) {
      console.error('Error fetching tags:', error);
    } finally {
      setLoading(false);
    }
  };

  const resetForm = () => {
    setEditingTag(null);
    setTagName('');
    setTagColor('#FF5733');
    setTagDescription('');
    setTagCategory('Other');
  };

  const handleCreateTag = async () => {
    if (!tagName.trim()) {
      toaster.create({
        title: 'Error',
        description: 'Tag name is required',
        type: 'error',
      });
      return;
    }

    try {
      setLoading(true);
      const payload = {
        name: tagName.trim(),
        color: tagColor,
        description: tagDescription.trim(),
        category: tagCategory,
      };

      const url = editingTag ? `http://localhost:8080/api/tags/${editingTag.id}` : 'http://localhost:8080/api/tags';
      const method = editingTag ? 'PUT' : 'POST';

      const response = await fetch(url, {
        method,
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(payload),
      });

      if (response.ok) {
        const newTag = await response.json();
        if (editingTag) {
          setTags(tags.map((tag) => (tag.id === editingTag.id ? newTag : tag)));

          if (selectedTags.some((tag) => tag.id === editingTag.id)) {
            onTagsChange(selectedTags.map((tag) => (tag.id === editingTag.id ? newTag : tag)));
          }
        } else {
          setTags([...tags, newTag]);
        }

        toaster.create({
          title: 'Success',
          description: editingTag ? 'Tag updated successfully' : 'Tag created successfully',
          type: 'success',
        });
        resetForm();
      } else {
        const errorData = await response.json();
        toaster.create({
          title: 'Error',
          description: errorData.message || 'Failed to save tag',
          type: 'error',
        });
      }
    } catch (error) {
      console.error('Error saving tag:', error);
      toaster.create({
        title: 'Error',
        description: 'An error occurred while saving the tag',
        type: 'error',
      });
    } finally {
      setLoading(false);
    }
  };

  const handleDeleteTag = async (tagId: string) => {
    if (confirm('Are you sure you want to delete this tag?')) {
      try {
        setLoading(true);
        const response = await fetch(`http://localhost:8080/api/tags/${tagId}`, {
          method: 'DELETE',
        });

        if (response.ok) {
          setTags(tags.filter((tag) => tag.id !== tagId));
          if (selectedTags.some((tag) => tag.id === tagId)) {
            onTagsChange(selectedTags.filter((tag) => tag.id !== tagId));
          }
          toaster.create({
            title: 'Success',
            description: 'Tag deleted successfully',
            type: 'success',
          });
        } else {
          toaster.create({
            title: 'Error',
            description: 'Failed to delete tag',
            type: 'error',
          });
        }
      } catch (error) {
        console.error('Error deleting tag:', error);
      } finally {
        setLoading(false);
      }
    }
  };

  const handleEditTag = (tag: Tag) => {
    setEditingTag(tag);
    setTagName(tag.name);
    setTagColor(tag.color);
    setTagDescription(tag.description || '');
    setTagCategory(tag.category || 'Other');
    setActiveTab(0); // Switch to the create/edit tab
  };

  const handleAddToRecipe = (tag: Tag) => {
    if (!selectedTags.some((t) => t.id === tag.id)) {
      onTagsChange([...selectedTags, tag]);
      toaster.create({
        title: 'Success',
        description: `Added "${tag.name}" to recipe`,
        type: 'success',
      });
    } else {
      toaster.create({
        title: 'Info',
        description: 'This tag is already added to the recipe',
        type: 'info',
      });
    }
  };

  const handleRemoveFromRecipe = (tagId: string) => {
    onTagsChange(selectedTags.filter((tag) => tag.id !== tagId));
  };

  const categories = ['All', ...new Set(tags.map((tag) => tag.category).filter(Boolean))];

  const filteredTags = tags
    .filter((tag) => tag.id !== FAVORITES_TAG_ID && tag.name !== FAVORITES_TAG_NAME)
    .filter((tag) => {
      const matchesSearch =
        tag.name.toLowerCase().includes(searchTerm.toLowerCase()) ||
        (tag.description && tag.description.toLowerCase().includes(searchTerm.toLowerCase()));
      const matchesCategory = categoryFilter === 'All' || tag.category === categoryFilter;
      return matchesSearch && matchesCategory;
    });

  const categoryCollection = createListCollection({
    items: [
      { label: 'Dietary', value: 'Dietary' },
      { label: 'Cuisine', value: 'Cuisine' },
      { label: 'Meal Type', value: 'Meal Type' },
      { label: 'Difficulty', value: 'Difficulty' },
      { label: 'Other', value: 'Other' },
    ],
  });

  const filterCollection = createListCollection({
    items: categories.map((category) => ({
      label: category,
      value: category,
    })),
  });

  const getSelectComponent = (
    collection: ListCollection<CollectionItem>,
    value: string,
    onChange: (val: string) => void,
    placeholder?: string,
  ) => (
    <Select.Root
      collection={collection}
      value={[value]}
      onValueChange={(details) => {
        if (details.value?.[0]) onChange(details.value[0]);
      }}
      size='md'>
      <Select.Control>
        <Select.Trigger>
          <Select.ValueText placeholder={placeholder} />
        </Select.Trigger>
        <Select.IndicatorGroup>
          <Select.Indicator />
        </Select.IndicatorGroup>
      </Select.Control>

      <Portal container={modalRef}>
        <Select.Positioner>
          <Select.Content>
            {collection.items.map((item: CollectionItem) => (
              <Select.Item key={item.value} item={item}>
                {item.label}
                <Select.ItemIndicator />
              </Select.Item>
            ))}
          </Select.Content>
        </Select.Positioner>
      </Portal>
    </Select.Root>
  );

  return (
    <Dialog.Root open={isOpen}>
      <Dialog.Backdrop />
      <Dialog.Positioner>
        <Dialog.Content ref={modalRef} maxW='80%' w='800px'>
          <Dialog.CloseTrigger />
          <Dialog.Header>
            <HStack>
              <Dialog.Title>Tag Management</Dialog.Title>
              <Dialog.Description>Add, edit, or remove tags and add them to your recipe</Dialog.Description>
            </HStack>
          </Dialog.Header>

          <Dialog.Body>
            <HStack mb={4} gap={0} borderBottom='1px solid'>
              <Button
                variant='ghost'
                onClick={() => setActiveTab(0)}
                borderBottom={activeTab === 0 ? '2px solid' : 'none'}
                borderRadius='0'
                mb='-1px'
                py={3}>
                Create / Edit
              </Button>
              <Button
                variant='ghost'
                onClick={() => setActiveTab(1)}
                borderBottom={activeTab === 1 ? '2px solid' : 'none'}
                borderRadius='0'
                mb='-1px'
                py={3}>
                Browse Tags
              </Button>
              <Button
                variant='ghost'
                onClick={() => setActiveTab(2)}
                borderBottom={activeTab === 2 ? '2px solid' : 'none'}
                borderRadius='0'
                mb='-1px'
                py={3}>
                Selected Tags
              </Button>
            </HStack>

            <VStack gap={4} align='stretch'>
              {activeTab === 0 && (
                <Box p={4} borderWidth='1px' borderRadius='md'>
                  <Box mb={3}>
                    <Text fontWeight='bold' mb={1}>
                      Tag Name
                    </Text>
                    <Input value={tagName} onChange={(e) => setTagName(e.target.value)} placeholder='Enter tag name' />
                  </Box>

                  <Box mb={3}>
                    <Text fontWeight='bold' mb={1}>
                      Color
                    </Text>
                    <HStack>
                      <Input
                        type='color'
                        value={tagColor}
                        onChange={(e) => setTagColor(e.target.value)}
                        width='100px'
                      />
                      <Badge px={2} py={1} variant='subtle' shadow='sm' style={{ backgroundColor: tagColor }}>
                        Preview
                      </Badge>
                    </HStack>
                  </Box>

                  <Box mb={3}>
                    <Text fontWeight='bold' mb={1}>
                      Description
                    </Text>
                    <Input
                      value={tagDescription}
                      onChange={(e) => setTagDescription(e.target.value)}
                      placeholder='Enter description'
                    />
                  </Box>

                  <Box mb={3}>
                    <Text fontWeight='bold' mb={1}>
                      Category
                    </Text>
                    {getSelectComponent(categoryCollection, tagCategory, setTagCategory, 'Select category')}
                  </Box>

                  <HStack gap={2} mt={4}>
                    <Button onClick={handleCreateTag} disabled={loading}>
                      <HStack>
                        <LuSave />
                        <Text>{editingTag ? 'Update Tag' : 'Create Tag'}</Text>
                      </HStack>
                    </Button>

                    {editingTag && (
                      <Button onClick={resetForm} variant='outline'>
                        Cancel Editing
                      </Button>
                    )}
                  </HStack>
                </Box>
              )}

              {activeTab === 1 && (
                <Box>
                  <HStack mb={4}>
                    <Input
                      placeholder='Search tags...'
                      value={searchTerm}
                      onChange={(e) => setSearchTerm(e.target.value)}
                      flex={1}
                    />
                    <Box width='200px'>
                      {getSelectComponent(filterCollection, categoryFilter, setCategoryFilter, 'Filter by category')}
                    </Box>
                  </HStack>

                  <Box overflowY='auto' maxHeight='400px'>
                    <Table.Root variant='outline' size='sm'>
                      <Table.Header>
                        <Table.Row>
                          <Table.ColumnHeader width='15%' textAlign='center'>
                            Tag
                          </Table.ColumnHeader>
                          <Table.ColumnHeader width='40%'>Description</Table.ColumnHeader>
                          <Table.ColumnHeader width='20%'>Category</Table.ColumnHeader>
                          <Table.ColumnHeader width='25%' textAlign='center'>
                            Actions
                          </Table.ColumnHeader>
                        </Table.Row>
                      </Table.Header>
                      <Table.Body>
                        {filteredTags.map((tag) => (
                          <Table.Row key={tag.id}>
                            <Table.Cell textAlign='center'>
                              <Badge
                                width='100%'
                                display='flex'
                                justifyContent='center'
                                px={2}
                                py={1}
                                variant='subtle'
                                shadow='sm'
                                style={{ backgroundColor: tag.color }}>
                                {tag.name}
                              </Badge>
                            </Table.Cell>
                            <Table.Cell>
                              {tag.description ? (
                                <Text fontSize='xs'>{tag.description}</Text>
                              ) : (
                                <Text fontSize='xs' as='i'>
                                  No description
                                </Text>
                              )}
                            </Table.Cell>
                            <Table.Cell>{tag.category || 'Other'}</Table.Cell>
                            <Table.Cell textAlign='center'>
                              <HStack gap={1} justifyContent='center'>
                                <IconButton size='xs' aria-label='Edit tag' onClick={() => handleEditTag(tag)}>
                                  <LuSettings size={12} />
                                </IconButton>
                                <IconButton size='xs' aria-label='Delete tag' onClick={() => handleDeleteTag(tag.id)}>
                                  <LuX size={12} />
                                </IconButton>
                                <IconButton
                                  size='xs'
                                  aria-label='Add to recipe'
                                  onClick={() => handleAddToRecipe(tag)}
                                  variant={selectedTags.some((t) => t.id === tag.id) ? 'solid' : 'outline'}>
                                  <LuPlus size={12} />
                                </IconButton>
                              </HStack>
                            </Table.Cell>
                          </Table.Row>
                        ))}
                      </Table.Body>
                    </Table.Root>
                  </Box>
                </Box>
              )}

              {activeTab === 2 && (
                <Box>
                  <Text mb={4}>Selected Tags for this Recipe</Text>
                  {selectedTags.length === 0 ? (
                    <Text fontSize='sm'>No tags selected for this recipe</Text>
                  ) : (
                    <Box overflowY='auto' maxHeight='400px'>
                      <Table.Root variant='outline' size='sm'>
                        <Table.Header>
                          <Table.Row>
                            <Table.ColumnHeader width='15%' textAlign='center'>
                              Tag
                            </Table.ColumnHeader>
                            <Table.ColumnHeader width='40%'>Description</Table.ColumnHeader>
                            <Table.ColumnHeader width='20%'>Category</Table.ColumnHeader>
                            <Table.ColumnHeader width='25%' textAlign='center'>
                              Actions
                            </Table.ColumnHeader>
                          </Table.Row>
                        </Table.Header>
                        <Table.Body>
                          {selectedTags.map((tag) => (
                            <Table.Row key={tag.id}>
                              <Table.Cell textAlign='center'>
                                <Badge
                                  width='100%'
                                  display='flex'
                                  justifyContent='center'
                                  px={2}
                                  py={1}
                                  variant='subtle'
                                  shadow='sm'
                                  style={{ backgroundColor: tag.color }}>
                                  {tag.name}
                                </Badge>
                              </Table.Cell>
                              <Table.Cell>
                                {tag.description ? (
                                  <Text fontSize='xs'>{tag.description}</Text>
                                ) : (
                                  <Text fontSize='xs' as='i'>
                                    No description
                                  </Text>
                                )}
                              </Table.Cell>
                              <Table.Cell>{tag.category || 'Other'}</Table.Cell>
                              <Table.Cell textAlign='center'>
                                <IconButton
                                  size='xs'
                                  aria-label='Remove from recipe'
                                  onClick={() => handleRemoveFromRecipe(tag.id)}>
                                  <LuX size={12} />
                                </IconButton>
                              </Table.Cell>
                            </Table.Row>
                          ))}
                        </Table.Body>
                      </Table.Root>
                    </Box>
                  )}
                </Box>
              )}
            </VStack>
          </Dialog.Body>

          <Dialog.Footer>
            <Button variant='outline' onClick={onClose}>
              Close
            </Button>
          </Dialog.Footer>
        </Dialog.Content>
      </Dialog.Positioner>
    </Dialog.Root>
  );
};

export default TagManagementModal;
