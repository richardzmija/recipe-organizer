import { Box, Text, Input, Flex, IconButton } from '@chakra-ui/react';
import { LuUpload, LuX } from 'react-icons/lu';
import { useRef, useState } from 'react';

interface Props {
  value: string;
  onChange: (value: string) => void;
}

const ImageInput = ({ value, onChange }: Props) => {
  const fileInputRef = useRef<HTMLInputElement | null>(null);
  const [fileName, setFileName] = useState<string | null>(null);

  const handleFileChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files?.[0];
    if (file) {
      setFileName(file.name);
      const reader = new FileReader();
      reader.onloadend = () => {
        onChange(reader.result as string);
      };
      reader.readAsDataURL(file);
    }
  };

  const clearImage = () => {
    setFileName(null);
    onChange('');
  };

  const openFileDialog = () => {
    fileInputRef.current?.click();
  };

  return (
    <Box>
      <Text fontWeight='bold' color='black' mb={2}>
        Image
      </Text>

      <Flex gap={3} align='center' wrap='wrap'>
        <Box
          border='1px solid #ccc'
          borderRadius='md'
          px={3}
          py={2}
          fontSize='sm'
          color='gray.500'
          cursor='pointer'
          display='flex'
          alignItems='center'
          gap={2}
          onClick={openFileDialog}
          minW='220px'>
          <LuUpload />
          <Text color='black'>Upload image</Text>
        </Box>

        <Input type='file' accept='image/*' onChange={handleFileChange} ref={fileInputRef} display='none' />

        {value && fileName && (
          <Flex align='center' gap={2}>
            <Text color='black' fontSize='sm'>
              {fileName}
            </Text>
            <IconButton aria-label='Remove image' variant='ghost' color='black' onClick={clearImage}>
              <LuX />
            </IconButton>
          </Flex>
        )}
      </Flex>
    </Box>
  );
};

export default ImageInput;
