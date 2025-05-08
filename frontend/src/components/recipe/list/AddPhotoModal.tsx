import { useRef, useState } from 'react';
import { Box, Button, Dialog, Input, Text, VStack, CloseButton, Portal, Spinner } from '@chakra-ui/react';
import { toaster } from '@/components/ui/toaster';
import { LuUpload } from 'react-icons/lu';
import { MdAddAPhoto } from 'react-icons/md';

interface AddPhotoModalProps {
  recipeId: string;
  recipeName: string;
  onSuccess?: () => void;
}

const AddPhotoModal = ({ recipeId, recipeName, onSuccess }: AddPhotoModalProps) => {
  const [isOpen, setIsOpen] = useState(false);
  const [isLoading, setIsLoading] = useState(false);
  const [description, setDescription] = useState('');
  const fileInputRef = useRef<HTMLInputElement>(null);
  const closeRef = useRef<HTMLButtonElement>(null);
  const [selectedFile, setSelectedFile] = useState<File | null>(null);
  const [previewUrl, setPreviewUrl] = useState<string | null>(null);

  const handleOpen = () => setIsOpen(true);
  const handleClose = () => {
    setIsOpen(false);
    resetForm();
  };

  const resetForm = () => {
    setSelectedFile(null);
    setPreviewUrl(null);
    setDescription('');
    if (fileInputRef.current) {
      fileInputRef.current.value = '';
    }
  };

  const handleFileChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    if (e.target.files && e.target.files.length > 0) {
      const file = e.target.files[0];
      setSelectedFile(file);

      const reader = new FileReader();
      reader.onloadend = () => {
        setPreviewUrl(reader.result as string);
      };
      reader.readAsDataURL(file);
    }
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();

    if (!selectedFile) {
      toaster.create({
        title: 'Error',
        description: 'Please select an image to upload',
        type: 'error',
      });
      return;
    }

    setIsLoading(true);

    const formData = new FormData();
    formData.append('image', selectedFile);
    formData.append('description', description || `Image for ${recipeName}`);

    try {
      const response = await fetch(`http://localhost:8080/api/recipes/${recipeId}/image`, {
        method: 'PATCH',
        body: formData,
      });

      if (!response.ok) {
        throw new Error('Failed to upload image');
      }

      toaster.create({
        title: 'Success',
        description: 'Image uploaded successfully',
        type: 'success',
      });

      handleClose();

      if (onSuccess) {
        onSuccess();
      }
    } catch (error) {
      toaster.create({
        title: 'Upload error' + error,
        description: 'There was a problem uploading the image',
        type: 'error',
      });
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <>
      <Button
        size='xs'
        onClick={(e) => {
          e.stopPropagation();
          handleOpen();
        }}>
        <MdAddAPhoto />
      </Button>

      <Dialog.Root open={isOpen}>
        <Portal>
          <Dialog.Backdrop />
          <Dialog.Positioner>
            <Dialog.Content onClick={(e) => e.stopPropagation()}>
              <Dialog.CloseTrigger />
              <Dialog.Header>
                <Dialog.Title>Add Photo to {recipeName}</Dialog.Title>
                <Dialog.CloseTrigger asChild>
                  <CloseButton size='sm' ref={closeRef} onClick={handleClose} />
                </Dialog.CloseTrigger>
              </Dialog.Header>

              <Dialog.Body>
                <VStack gap={4} align='stretch'>
                  <Box>
                    <Text fontWeight='bold' mb={1}>
                      Select Image
                    </Text>
                    <Input
                      type='file'
                      ref={fileInputRef}
                      accept='image/*'
                      onChange={handleFileChange}
                      onClick={(e) => e.stopPropagation()}
                    />
                  </Box>

                  {previewUrl && (
                    <Box>
                      <Text fontWeight='bold' fontSize='sm' mb={2}>
                        Preview:
                      </Text>
                      <Box
                        borderRadius='md'
                        overflow='hidden'
                        maxHeight='200px'
                        display='flex'
                        justifyContent='center'
                        borderWidth='1px'>
                        <img
                          src={previewUrl}
                          alt='Preview'
                          style={{
                            maxHeight: '200px',
                            maxWidth: '100%',
                            objectFit: 'contain',
                          }}
                        />
                      </Box>
                    </Box>
                  )}

                  <Box>
                    <Text fontWeight='bold' mb={1}>
                      Description (optional)
                    </Text>
                    <Input
                      type='text'
                      value={description}
                      onChange={(e) => setDescription(e.target.value)}
                      placeholder={`Image for ${recipeName}`}
                      onClick={(e) => e.stopPropagation()}
                    />
                  </Box>
                </VStack>
              </Dialog.Body>

              <Dialog.Footer>
                <Button
                  variant='outline'
                  onClick={(e) => {
                    e.stopPropagation();
                    handleClose();
                  }}>
                  Cancel
                </Button>
                <Button
                  onClick={(e) => {
                    e.stopPropagation();
                    handleSubmit(e);
                  }}
                  disabled={!selectedFile || isLoading}>
                  {isLoading ? (
                    <Spinner size='sm' />
                  ) : (
                    <>
                      <LuUpload style={{ marginRight: '6px' }} />
                      Upload
                    </>
                  )}
                </Button>
              </Dialog.Footer>
            </Dialog.Content>
          </Dialog.Positioner>
        </Portal>
      </Dialog.Root>
    </>
  );
};

export default AddPhotoModal;
