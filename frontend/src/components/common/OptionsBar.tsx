'use client';

import { Box, Button, Input, Flex, Portal, Select, createListCollection } from '@chakra-ui/react';
import { useNavigate } from 'react-router-dom';

const categories = createListCollection({
  items: [
    { label: 'Obiady', value: 'obiady' },
    { label: 'Desery', value: 'desery' },
    { label: 'Śniadania', value: 'sniadania' },
  ],
});

const times = createListCollection({
  items: [
    { label: '< 15 min', value: 'lt15' },
    { label: '15-30 min', value: '15to30' },
    { label: '> 30 min', value: 'gt30' },
  ],
});

const difficulties = createListCollection({
  items: [
    { label: 'Łatwa', value: 'easy' },
    { label: 'Średnia', value: 'medium' },
    { label: 'Trudna', value: 'hard' },
  ],
});

const OptionsBar = () => {
  const navigate = useNavigate();

  return (
    <Box bg='white' color='black' px={6} py={4} borderBottom='1px' borderColor='gray.200'>
      <Flex wrap='wrap' gap={4} align='center' justify='space-between'>
        <Flex gap={2} wrap='wrap'>
          <Button
            size='sm'
            color='black'
            variant='outline'
            onClick={() => navigate('/add')}
            _hover={{ bg: 'black', color: 'white' }}>
            + Dodaj przepis
          </Button>
          <Button size='sm' color='black' variant='outline' _hover={{ bg: 'black', color: 'white' }}>
            Importuj z linku
          </Button>
        </Flex>

        <Flex gap={3} wrap='wrap' align='center' flex='1' justify='flex-end'>
          <Input
            placeholder='Szukaj przepisu...'
            size='sm'
            bg='white'
            color='black'
            _placeholder={{ color: 'gray.800' }}
            maxW='200px'
            flexShrink={0}
          />

          {[
            { label: 'Kategoria', data: categories },
            { label: 'Czas', data: times },
            { label: 'Trudność', data: difficulties },
          ].map(({ label, data }) => (
            <Box key={label} minW='120px' flex='1 1 140px'>
              <Select.Root collection={data} size='sm'>
                <Select.HiddenSelect />
                <Select.Control>
                  <Select.Trigger>
                    <Select.ValueText placeholder={label} color='gray.800' />
                  </Select.Trigger>
                  <Select.IndicatorGroup>
                    <Select.Indicator />
                  </Select.IndicatorGroup>
                </Select.Control>
                <Portal>
                  <Select.Positioner>
                    <Select.Content>
                      {data.items.map((item) => (
                        <Select.Item key={item.value} item={item}>
                          {item.label}
                          <Select.ItemIndicator />
                        </Select.Item>
                      ))}
                    </Select.Content>
                  </Select.Positioner>
                </Portal>
              </Select.Root>
            </Box>
          ))}
        </Flex>
      </Flex>
    </Box>
  );
};

export default OptionsBar;
