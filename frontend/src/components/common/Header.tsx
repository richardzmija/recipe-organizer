import { Heading, Box } from '@chakra-ui/react';
import { Link } from 'react-router-dom';
const Header = () => (
  <Box>
    <Heading as='h1' size='3xl'>
      <Link to='/'>Recipe Organizer</Link>
    </Heading>
  </Box>
);

export default Header;
