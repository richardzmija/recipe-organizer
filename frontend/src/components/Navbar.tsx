import { HStack, Box, Heading } from '@chakra-ui/react';
import { Link } from 'react-router-dom';
import { ColorModeButton, useColorModeValue } from './ui/color-mode';

export default function Navbar() {
  const bgColor = useColorModeValue('gray.200', 'gray.800');
  return (
    <HStack as='nav' width='100%' py={4} px={6} justify='space-between' bg={bgColor}>
      <Box>
        <Heading as='h1' size='md'>
          <Link to='/'>Recipe Organizer</Link>
        </Heading>
      </Box>
      <ColorModeButton />
    </HStack>
  );
}
