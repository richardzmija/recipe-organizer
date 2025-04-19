import { useState } from 'react';
import { Box, Button, Flex, Heading, Text, Card } from '@chakra-ui/react';
import { FaChevronLeft, FaChevronRight } from 'react-icons/fa';

interface Step {
  title: string;
  text: string;
}

interface StepNavigatorProps {
  steps: Step[];
}

export default function StepNavigator({ steps }: StepNavigatorProps) {
  const [currentStep, setCurrentStep] = useState(0);
  const goPrev = () => setCurrentStep((s) => Math.max(s - 1, 0));
  const goNext = () => setCurrentStep((s) => Math.min(s + 1, steps.length - 1));

  return (
    <Box>
      <Text textAlign='center' fontSize='sm' color='gray.500' mb={2}>
        Step {currentStep + 1} of {steps.length}
      </Text>

      <Flex align='center' justify='center' gap={4}>
        <Button onClick={goPrev} visibility={currentStep === 0 ? 'hidden' : 'visible'}>
          <FaChevronLeft />
        </Button>

        <Card.Root variant='outline' mb={4} width='50%' shadow='xl' size='sm'>
          <Card.Header>
            <Heading as='p' size='xl' textAlign='center'>
              {steps[currentStep].title}
            </Heading>
          </Card.Header>
          <Card.Body>
            <Text>{steps[currentStep].text}</Text>
          </Card.Body>
        </Card.Root>

        <Button onClick={goNext} visibility={currentStep === steps.length - 1 ? 'hidden' : 'visible'}>
          <FaChevronRight />
        </Button>
      </Flex>
    </Box>
  );
}
