import { JSX } from '@emotion/react/jsx-runtime';
import { Routes, Route } from 'react-router-dom';
import RecipeList from './pages/RecipeList';
import Navbar from './components/ui/Navbar';
import AddRecipePage from './pages/AddRecipePage';
import { Toaster } from '@/components/ui/toaster';
import RecipeDetails from './pages/RecipeDetails';

function App(): JSX.Element {
  return (
    <>
      <Navbar />
      <Routes>
        <Route path='/' element={<RecipeList />} />
        <Route path='/recipes' element={<RecipeList />} />
        <Route path='/recipe/create' element={<AddRecipePage />} />
        <Route path='/recipe/:id' element={<RecipeDetails />} />
      </Routes>
      <Toaster />
    </>
  );
}

export default App;
