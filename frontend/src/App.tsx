import { Button, HStack } from '@chakra-ui/react';
import { JSX } from '@emotion/react/jsx-runtime';

function App(): JSX.Element {
  return (
    <>
      <div className='w-100 bg-amber-500 text-blue-600'>Test</div>
      <HStack>
        <Button>Click me</Button>
        <Button>Click me</Button>
      </HStack>
    </>
  );
}

export default App;
