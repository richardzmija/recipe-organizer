import { Select, createListCollection } from '@chakra-ui/react';
import { useEffect, useState } from 'react';

interface UnitType {
  id: string;
  name: string;
  symbol: string;
  system: string;
}

interface Props {
  value: string;
  onChange: (value: string) => void;
}

const fallbackMetrics = ['GRAMS', 'LITERS', 'MILLILITERS', 'CUPS', 'SPOONS'];

// Convert fallback strings to UnitType objects
const defaultMetrics = fallbackMetrics.map((metric) => ({
  id: metric,
  name: metric.charAt(0) + metric.slice(1).toLowerCase(),
  symbol: metric.substring(0, 2).toLowerCase(),
  system: 'METRIC',
}));

export default function MetricSelect({ value, onChange }: Props) {
  const [metrics, setMetrics] = useState<UnitType[]>(defaultMetrics);

  useEffect(() => {
    fetch('http://localhost:8080/api/units')
      .then((res) => res.json())
      .then((data: UnitType[]) => {
        setMetrics(data);
      })
      .catch(() => {
        console.warn('Default metric units used.');
      });
  }, []);

  const collection = createListCollection({
    items: metrics.map((metric) => ({
      value: metric.id,
      label: `${metric.name} (${metric.symbol})`,
      unit: metric,
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
          <Select.ValueText placeholder='Jednostka' />
        </Select.Trigger>
        <Select.IndicatorGroup>
          <Select.Indicator />
        </Select.IndicatorGroup>
      </Select.Control>

      <Select.Positioner>
        <Select.Content>
          {collection.items.map((item) => (
            <Select.Item key={item.value} item={item}>
              {item.label}
              <Select.ItemIndicator />
            </Select.Item>
          ))}
        </Select.Content>
      </Select.Positioner>
    </Select.Root>
  );
}
