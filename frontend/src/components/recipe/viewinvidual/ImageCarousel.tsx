import { useState, useEffect } from 'react';
import { Box, HStack, IconButton, Image, AspectRatio, Text, Flex } from '@chakra-ui/react';
import { FaChevronLeft, FaChevronRight, FaStar } from 'react-icons/fa';
import { LuStar } from 'react-icons/lu';
import { Image as ImageType } from '../../../types/Recipe';
import { toaster } from '@/components/ui/toaster';

interface ImageCarouselProps {
  images: ImageType[];
  recipeId: string;
  onImageUpdate?: () => void;
}

const ImageCarousel = ({ images, recipeId, onImageUpdate }: ImageCarouselProps) => {
  const [currentIndex, setCurrentIndex] = useState(0);
  const [isSettingPrimary, setIsSettingPrimary] = useState(false);

  useEffect(() => {
    const primaryImageIndex = images.findIndex((img) => img.isPrimary);
    if (primaryImageIndex >= 0) {
      setCurrentIndex(primaryImageIndex);
    }
  }, [images]);

  const goToPrevious = () => {
    const isFirstImage = currentIndex === 0;
    const newIndex = isFirstImage ? images.length - 1 : currentIndex - 1;
    setCurrentIndex(newIndex);
  };

  const goToNext = () => {
    const isLastImage = currentIndex === images.length - 1;
    const newIndex = isLastImage ? 0 : currentIndex + 1;
    setCurrentIndex(newIndex);
  };

  const setPrimaryImage = async (imageId: string) => {
    if (isSettingPrimary) return;

    setIsSettingPrimary(true);
    try {
      const response = await fetch(`http://localhost:8080/api/recipes/${recipeId}/image/${imageId}/primary`, {
        method: 'PATCH',
      });

      if (!response.ok) {
        throw new Error('Failed to set primary image');
      }

      const updatedRecipeResponse = await fetch(`http://localhost:8080/api/recipes/${recipeId}`);
      if (!updatedRecipeResponse.ok) {
        throw new Error('Failed to refresh recipe data');
      }

      toaster.create({
        title: 'Success',
        description: 'Primary image updated successfully',
        type: 'success',
      });

      if (onImageUpdate) {
        onImageUpdate();
      } else {
        window.location.reload();
      }
    } catch (error) {
      console.error('Error setting primary image:', error);
      toaster.create({
        title: 'Error',
        description: 'Failed to set primary image',
        type: 'error',
      });
    } finally {
      setIsSettingPrimary(false);
    }
  };

  const handleSetPrimary = async (imageId: string) => {
    await setPrimaryImage(imageId);
  };

  if (!images || images.length === 0) {
    return null;
  }

  const currentImage = images[currentIndex];
  const imageUrl = `http://localhost:8080/api/images/${currentImage.id}/image`;
  const isPrimary = currentImage.isPrimary;

  return (
    <Box position='relative' width='100%' borderRadius='lg' overflow='hidden'>
      <AspectRatio ratio={4 / 3}>
        <Image
          src={imageUrl}
          alt={currentImage.description || 'Recipe image'}
          objectFit='cover'
          width='100%'
          height='100%'
          borderRadius='lg'
        />
      </AspectRatio>

      <Box position='absolute' top='10px' left='10px' height='32px' minWidth='32px' borderRadius='md'>
        {isPrimary ? (
          <Flex
            bg='blackAlpha.600'
            color='white'
            borderRadius='md'
            alignItems='center'
            justifyContent='center'
            gap={2}
            height='100%'
            padding='0 12px'>
            <FaStar color='gold' size={14} />
            <Text fontSize='sm' fontWeight='medium'>
              Primary
            </Text>
          </Flex>
        ) : (
          <IconButton
            aria-label='Set as primary image'
            size='sm'
            bg='blackAlpha.600'
            _hover={{ bg: 'blackAlpha.800' }}
            colorPalette='white'
            borderRadius='md'
            disabled={isSettingPrimary}
            onClick={() => handleSetPrimary(currentImage.id)}
            title='Set as primary image'
            height='100%'
            width='32px'
            padding='0'>
            <LuStar color='white' size={14} />
          </IconButton>
        )}
      </Box>

      {images.length > 1 && (
        <>
          <IconButton
            aria-label='Previous image'
            position='absolute'
            left='10px'
            top='50%'
            transform='translateY(-50%)'
            size='sm'
            colorPalette='white'
            borderRadius='full'
            opacity='0.7'
            _hover={{ opacity: 1 }}
            onClick={goToPrevious}>
            <FaChevronLeft />
          </IconButton>

          <IconButton
            aria-label='Next image'
            position='absolute'
            right='10px'
            top='50%'
            transform='translateY(-50%)'
            size='sm'
            colorPalette='white'
            borderRadius='full'
            opacity='0.7'
            _hover={{ opacity: 1 }}
            onClick={goToNext}>
            <FaChevronRight />
          </IconButton>

          <HStack position='absolute' bottom='10px' left='50%' transform='translateX(-50%)' justifyContent='center'>
            {images.map((image, index) => (
              <Box
                key={index}
                width='10px'
                height='10px'
                borderRadius='full'
                bg={index === currentIndex ? 'white' : 'whiteAlpha.600'}
                border={image.isPrimary ? '1px solid gold' : 'none'}
                cursor='pointer'
                onClick={() => setCurrentIndex(index)}
              />
            ))}
          </HStack>
        </>
      )}
    </Box>
  );
};

export default ImageCarousel;

export { ImageCarousel };
