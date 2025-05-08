import { useState } from 'react';
import { Box, HStack, IconButton, Image, AspectRatio } from '@chakra-ui/react';
import { FaChevronLeft, FaChevronRight } from 'react-icons/fa';
import { Image as ImageType } from '../../../types/Recipe';

interface ImageCarouselProps {
  images: ImageType[];
}

const ImageCarousel = ({ images }: ImageCarouselProps) => {
  const [currentIndex, setCurrentIndex] = useState(0);

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

  if (!images || images.length === 0) {
    return null;
  }

  const currentImage = images[currentIndex];
  const imageUrl = `http://localhost:8080/api/images/${currentImage.id}/image`;

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
            {images.map((_, index) => (
              <Box
                key={index}
                width='8px'
                height='8px'
                borderRadius='full'
                bg={index === currentIndex ? 'white' : 'whiteAlpha.600'}
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
