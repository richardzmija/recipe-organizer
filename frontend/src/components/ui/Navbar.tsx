import { HStack, Button, Spacer, Dialog } from '@chakra-ui/react';
import { Link } from 'react-router-dom';
import { ColorModeButton, useColorModeValue } from './color-mode';
import Header from '../common/Header';
import ImportRecipeForm from '@/components/recipe/import/ImportRecipeForm';
import { useRef } from 'react';

interface Props {
  onRefresh: () => void;
}

export default function Navbar({ onRefresh }: Props) {
  const bgColor = useColorModeValue('gray.200', 'gray.800');
  const closeRef = useRef<HTMLButtonElement | null>(null);

  const handleOnSuccess = () => {
    if (!closeRef || !closeRef.current) return;
    closeRef.current.click();
    // refresh
    onRefresh();
  };

  return (
    <>
      <HStack as='nav' width='100%' py={4} px={6} justify='space-between' bg={bgColor}>
        <Header />

        <Spacer />

        <HStack gap={4}>
          <Link to='/recipes'>
            <Button variant='ghost'>All Recipes</Button>
          </Link>
          <Link to='/recipes/create'>
            <Button colorPalette='orange'>Create Recipe</Button>
          </Link>
          <Dialog.Root>
            <Dialog.Trigger asChild>
              <Button colorPalette='green'>Import Recipe</Button>
            </Dialog.Trigger>
            <Dialog.Backdrop />
            <Dialog.Positioner>
              <Dialog.Content maxW='xl' p={6} borderRadius='xl' bg='bg.surface' shadow='xl'>
                <Dialog.Title fontSize='lg' fontWeight='bold' mb={4}>
                  Import Recipe
                </Dialog.Title>
                <ImportRecipeForm onSuccess={handleOnSuccess} />
                <Dialog.CloseTrigger asChild>
                  <Button mt={4} colorPalette='gray' ref={closeRef}>
                    Close
                  </Button>
                </Dialog.CloseTrigger>
              </Dialog.Content>
            </Dialog.Positioner>
          </Dialog.Root>
          <ColorModeButton />
        </HStack>
      </HStack>
    </>
  );
}
