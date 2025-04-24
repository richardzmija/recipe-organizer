import { HStack, Button, Spacer } from '@chakra-ui/react';
import { Link } from 'react-router-dom';
import { ColorModeButton, useColorModeValue } from './color-mode';
import Header from '../common/Header';

export default function Navbar() {
  const bgColor = useColorModeValue('gray.200', 'gray.800');

  return (
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
        <ColorModeButton />
      </HStack>
    </HStack>
  );
}
