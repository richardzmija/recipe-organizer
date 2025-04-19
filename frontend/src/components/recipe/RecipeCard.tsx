import { Box, Text, IconButton } from '@chakra-ui/react';
import { Pencil, Trash2 } from 'lucide-react';

interface RecipeCardProps {
  title: string;
}

const RecipeCard = ({ title }: RecipeCardProps) => (
  <Box
    bg='white'
    border='1px'
    borderColor='black'
    shadow='sm'
    borderRadius='lg'
    p={4}
    className='hover:shadow-md transition-shadow'>
    <Text fontWeight='semibold' color='black' mb={2} className='truncate'>
      {title}
    </Text>
    <Box mt={4} display='flex' gap={2}>
      <IconButton aria-label='Edytuj' size='sm' variant='ghost' disabled color='black'>
        <Pencil color='black' size={16} />
      </IconButton>
      <IconButton aria-label='Usuń' size='sm' variant='ghost' disabled color='black'>
        <Trash2 color='black' size={16} />
      </IconButton>
    </Box>
  </Box>
);

export default RecipeCard;
