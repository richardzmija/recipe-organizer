import RecipeCreateForm from '@/components/recipe/create/RecipeCreateForm';
import { useNavigate } from 'react-router-dom';

const AddRecipePage = () => {
  const navigate = useNavigate();

  return <RecipeCreateForm onCancel={() => navigate('/')} />;
};

export default AddRecipePage;
