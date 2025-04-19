'use client';

import { Select, Portal, createListCollection } from '@chakra-ui/react';
import { useEffect, useState } from 'react';

interface Props {
  value: string;
  onChange: (value: string) => void;
}

const fallbackMetrics = ['GRAMS', 'LITERS', 'MILLILITERS', 'CUPS', 'SPOONS'];

export default function MetricSelect({ value, onChange }: Props) {
  const [metrics, setMetrics] = useState<string[]>(fallbackMetrics);

  useEffect(() => {
    fetch('http://localhost:8080/recipe/metrics')
      .then((res) => res.json())
      .then((data: string[]) => {
        setMetrics(data);
      })
      .catch(() => {
        console.warn('Default metric units used.');
      });
  }, []);

  const collection = createListCollection({
    items: metrics.map((metric) => ({
      value: metric,
    })),
  });

  return (
    <Select.Root
      collection={collection}
      value={[value]}
      onValueChange={(details) => {
        const newValue = details.value?.[0];
        if (newValue) onChange(newValue);
      }}
      size='sm'>
      <Select.HiddenSelect />
      <Select.Control>
        <Select.Trigger>
          <Select.ValueText placeholder='Jednostka' color='black' />
        </Select.Trigger>
        <Select.IndicatorGroup>
          <Select.Indicator />
        </Select.IndicatorGroup>
      </Select.Control>

      <Portal>
        <Select.Positioner>
          <Select.Content>
            {collection.items.map((item) => (
              <Select.Item key={item.value} item={item}>
                {item.value}
                <Select.ItemIndicator />
              </Select.Item>
            ))}
          </Select.Content>
        </Select.Positioner>
      </Portal>
    </Select.Root>
  );
}
