import { Flex, Heading } from '@chakra-ui/react';

const Header = () => (
  <Flex
    as='header'
    justify='center'
    align='center'
    textAlign='center'
    px={6}
    py={5}
    bg='white'
    color='white'
    shadow='md'>
    <Heading size='4xl' fontWeight='bold' letterSpacing='wide' color='black'>
      Recipe Organizer
    </Heading>
  </Flex>
);

export default Header;
