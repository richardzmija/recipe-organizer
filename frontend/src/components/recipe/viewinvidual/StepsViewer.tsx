import { useState } from 'react';
import { Box, Button, Flex, Heading, Text, Card, VStack } from '@chakra-ui/react';
import { FaChevronLeft, FaChevronRight, FaList, FaStepForward } from 'react-icons/fa';
import { Step } from '../../../types/Step';

interface StepsViewerProps {
  steps: Step[];
}

export default function StepsViewer({ steps }: StepsViewerProps) {
  const [currentStep, setCurrentStep] = useState(0);
  const [viewMode, setViewMode] = useState<'navigator' | 'list'>('list');
  const [isTransitioning, setIsTransitioning] = useState(false);

  const goPrev = () => {
    setIsTransitioning(true);
    setTimeout(() => {
      setCurrentStep((s) => Math.max(s - 1, 0));
      setIsTransitioning(false);
    }, 150);
  };

  const goNext = () => {
    setIsTransitioning(true);
    setTimeout(() => {
      setCurrentStep((s) => Math.min(s + 1, steps.length - 1));
      setIsTransitioning(false);
    }, 150);
  };

  const toggleViewMode = () => {
    setViewMode(viewMode === 'navigator' ? 'list' : 'navigator');
  };

  if (viewMode === 'list') {
    return (
      <Box>
        <Flex justify='space-between' align='center' mb={4}>
          <Heading as='h3' size='lg'>
            All Steps
          </Heading>
          <Button onClick={toggleViewMode} variant='outline'>
            <FaStepForward style={{ marginRight: '8px' }} />
            Step Navigator
          </Button>
        </Flex>

        <VStack align='stretch' gap={4}>
          {steps.map((step, index) => (
            <Card.Root key={index} variant='outline' shadow='md'>
              <Card.Header>
                <Heading as='h4' size='md' dangerouslySetInnerHTML={{ __html: step.title }} />
              </Card.Header>
              <Card.Body>
                <Text dangerouslySetInnerHTML={{ __html: step.text }} />
              </Card.Body>
            </Card.Root>
          ))}
        </VStack>
      </Box>
    );
  }

  return (
    <Box>
      <Flex justify='space-between' align='center' mb={4}>
        <Text textAlign='left' fontSize='sm' color='gray.500'>
          Step {currentStep + 1} of {steps.length}
        </Text>
        <Button onClick={toggleViewMode} variant='outline'>
          <FaList style={{ marginRight: '8px' }} />
          View All Steps
        </Button>
      </Flex>

      <Flex align='center' justify='center' gap={4}>
        <Button onClick={goPrev} visibility={currentStep === 0 ? 'hidden' : 'visible'}>
          <FaChevronLeft />
        </Button>

        <Card.Root
          variant='outline'
          mb={4}
          width='50%'
          shadow='xl'
          size='sm'
          transition='all 0.3s ease-in-out'
          opacity={isTransitioning ? 0.3 : 1}
          transform={isTransitioning ? 'scale(0.95)' : 'scale(1)'}>
          <Card.Header>
            <Heading
              as='p'
              size='xl'
              textAlign='center'
              dangerouslySetInnerHTML={{ __html: steps[currentStep].title }}
            />
          </Card.Header>
          <Card.Body>
            <Text dangerouslySetInnerHTML={{ __html: steps[currentStep].text }} />
          </Card.Body>
        </Card.Root>

        <Button onClick={goNext} visibility={currentStep === steps.length - 1 ? 'hidden' : 'visible'}>
          <FaChevronRight />
        </Button>
      </Flex>
    </Box>
  );
}
