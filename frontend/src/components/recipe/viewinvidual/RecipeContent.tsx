import { useState, useEffect } from 'react';
import {
  VStack,
  Heading,
  Text,
  List,
  Flex,
  HStack,
  Badge,
  Separator,
  Box,
  Card,
  Select,
  createListCollection,
  ListCollection,
  Portal,
} from '@chakra-ui/react';
import StepsViewer from './StepsViewer';
import { Recipe } from '../../../types/Recipe';
import { ImageCarousel } from './ImageCarousel';
import { ConvertedUnit, ConvertiableUnit } from '@/types/Unit';
import { toaster } from '@/components/ui/toaster';

interface UnitCollectionItem {
  label: string;
  value: string;
}

interface RecipeContentProps {
  recipe: Recipe;
}
export const RecipeContent = ({ recipe: initialRecipe }: RecipeContentProps) => {
  const [recipe, setRecipe] = useState(initialRecipe);
  const [, setRefreshKey] = useState(0);
  const [unitConversions, setUnitConversions] = useState<ListCollection<UnitCollectionItem>>(
    createListCollection({ items: [] as UnitCollectionItem[] }),
  );
  const [selectedUnits, setSelectedUnits] = useState<string[]>([]);
  const [quantities, setQuantities] = useState<{ value: number; formattedValue: string }[]>([]);
  const [loadedUnitConversions, setLoadedUnitConversions] = useState<boolean>(false);

  const handleImageUpdate = async () => {
    try {
      const response = await fetch(`http://localhost:8080/api/recipes/${recipe.id}`);
      if (response.ok) {
        const updatedRecipe = await response.json();
        setRecipe(updatedRecipe);
      }
    } catch (error) {
      console.error('Error refreshing recipe data:', error);
    }

    setRefreshKey((prev) => prev + 1);
  };

  const getUnitConversions = async () => {
    try {
      const response = await fetch('http://localhost:8080/api/units');
      if (response.ok) {
        const units = (await response.json()) as ConvertiableUnit[];
        setUnitConversions(formatUnitConversionToCollection(units));
      }
    } catch (error) {
      console.error('Error getting unit conversions: ', error);
    } finally {
      setLoadedUnitConversions(true);
    }
  };

  const initSelectedUnits = () => {
    setSelectedUnits(recipe.ingredients.map((ingredient) => ingredient.unit || ''));
  };

  const initQuantities = () => {
    setQuantities(
      recipe.ingredients.map((ingredient) => ({
        value: ingredient.quantity,
        formattedValue: ingredient.quantity.toString(),
      })),
    );
  };

  const convertUnitQuantity = async (
    value: number,
    fromUnit: string,
    toUnit: string,
  ): Promise<ConvertedUnit | null> => {
    try {
      const response = await fetch('http://localhost:8080/api/units/convert', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          value,
          fromUnit,
          toUnit,
          format: 'DECIMAL',
        }),
      });

      if (response.ok) {
        const convertedUnit = (await response.json()) as ConvertedUnit;
        return convertedUnit;
      } else {
        return null;
      }
    } catch (error) {
      console.error(error);
      return null;
    }
  };

  const checkConversionPossibility = async (fromUnit: string, toUnit: string): Promise<boolean> => {
    try {
      const response = await fetch(`http://localhost:8080/api/units/can-convert?fromUnit=${fromUnit}&toUnit=${toUnit}`);
      if (response.ok) {
        const canConvert = (await response.json()) as boolean;
        return canConvert;
      }
      return false;
    } catch (error) {
      console.error('Error checking conversion possiblity. ', error);
      return false;
    }
  };

  const formatUnitConversionToCollection = (
    unitConversions: ConvertiableUnit[],
  ): ListCollection<UnitCollectionItem> => {
    const items = unitConversions.map((unitConversion) => ({
      value: unitConversion.id,
      label: unitConversion.name,
    }));
    return createListCollection({ items });
  };

  const handleSelect = async (index: number, selectedUnit: UnitCollectionItem) => {
    const value = quantities[index].value;
    const fromUnit = selectedUnits[index];
    const toUnit = selectedUnit.value;
    const canConvert = await checkConversionPossibility(fromUnit, toUnit);
    if (!canConvert) {
      toaster.create({
        title: 'Impossible conversion',
        description: `Cannot convert ${fromUnit} to ${toUnit}`,
        type: 'info',
      });
      return;
    }
    const convertedUnitQuantity = await convertUnitQuantity(value, fromUnit, toUnit);

    if (convertedUnitQuantity) {
      const { convertedValue, formattedConvertedValue } = convertedUnitQuantity;
      setQuantities((prev) => {
        const tmp = [...prev];
        tmp[index] = { value: convertedValue, formattedValue: formattedConvertedValue };
        return tmp;
      });
    }
    setSelectedUnits((prev) => {
      const tmp = [...prev];
      tmp[index] = selectedUnit.value;
      return tmp;
    });
  };

  useEffect(() => {
    setRecipe(initialRecipe);
    getUnitConversions();
    initQuantities();
    initSelectedUnits();
  }, [initialRecipe]);

  return (
    <VStack align='stretch'>
      <Flex direction={{ base: 'column', md: 'row' }} align='flex-start'>
        <Box flex='2'>
          <Heading as='h1' size='3xl' mb={1} dangerouslySetInnerHTML={{ __html: recipe.name }} />

          {recipe.tags && recipe.tags.length > 0 && (
            <HStack spaceX={2} mb={4}>
              {recipe.tags.map((tag) => (
                <Badge key={tag.id} variant='surface' shadow='sm' style={{ backgroundColor: tag.color }}>
                  {tag.name}
                </Badge>
              ))}
            </HStack>
          )}

          {recipe.description && <Text fontSize='md' dangerouslySetInnerHTML={{ __html: recipe.description }} />}
        </Box>
        {recipe.images && recipe.images.length > 0 && (
          <Box flex='1'>
            <ImageCarousel images={recipe.images} recipeId={recipe.id || ''} onImageUpdate={handleImageUpdate} />
          </Box>
        )}
      </Flex>

      <Separator />

      <Box>
        <Heading as='h2' size='xl' mb='2'>
          Ingredients
        </Heading>
        <Card.Root variant='outline' shadow='xl'>
          <Card.Body>
            <List.Root>
              {recipe.ingredients.map((ingredient, index) => (
                <List.Item key={index}>
                  <HStack justifyContent='space-between' w='100%'>
                    <Box>{ingredient.ingredientName}</Box>
                    <HStack>
                      {loadedUnitConversions && quantities[index].formattedValue}
                      {loadedUnitConversions && (
                        <Select.Root
                          collection={unitConversions}
                          size='sm'
                          width={'200px'}
                          value={[selectedUnits[index]]}
                          onValueChange={(details) => handleSelect(index, details.items[0])}>
                          <Select.Control>
                            <Select.Trigger>
                              <Select.ValueText />
                            </Select.Trigger>
                          </Select.Control>
                          <Portal>
                            <Select.Positioner>
                              <Select.Content>
                                {unitConversions.items.map((unitConversion) => (
                                  <Select.Item item={unitConversion} key={unitConversion.value}>
                                    {unitConversion.label}
                                  </Select.Item>
                                ))}
                              </Select.Content>
                            </Select.Positioner>
                          </Portal>
                        </Select.Root>
                      )}
                    </HStack>
                  </HStack>
                </List.Item>
              ))}
            </List.Root>
          </Card.Body>
        </Card.Root>
      </Box>

      <Heading as='h2' size='xl'>
        Steps
      </Heading>
      <StepsViewer steps={recipe.steps} />
    </VStack>
  );
};

export default RecipeContent;
