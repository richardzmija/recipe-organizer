import { Heading, Box, Image, HStack } from '@chakra-ui/react';
import { Link } from 'react-router-dom';
const Header = () => (
  <Box>
    <Heading as='h1' size='3xl'>
      <Link to='/'>
        <HStack>
          <Image src='/favicon.png'></Image>Recipe Organizer
        </HStack>
      </Link>
    </Heading>
  </Box>
);

export default Header;
