import { JSX } from '@emotion/react/jsx-runtime';
import { Routes, Route } from 'react-router-dom';
import RecipeList from './pages/RecipeList';
import Navbar from './components/ui/Navbar';
import AddRecipePage from './pages/AddRecipePage';
import { Toaster } from '@/components/ui/toaster';
import RecipeDetails from './pages/RecipeDetails';
import EditRecipePage from './pages/EditRecipePage';
import { PaginationProvider } from './hooks/PaginationContext';
import { useState } from 'react';

function App(): JSX.Element {
  const [refreshSignal, setRefreshSignal] = useState(0);
  const onRefresh = () => setRefreshSignal((prev) => prev + 1);
  return (
    <>
      <PaginationProvider>
        <Navbar onRefresh={onRefresh} />
        <Routes>
          <Route path='/' element={<RecipeList refreshSignal={refreshSignal} onRefresh={onRefresh} />} />
          <Route path='/recipes' element={<RecipeList refreshSignal={refreshSignal} onRefresh={onRefresh} />} />
          <Route path='/recipes/create' element={<AddRecipePage />} />
          <Route path='/recipes/:id' element={<RecipeDetails />} />
          <Route path='/recipes/edit/:id' element={<EditRecipePage />} />
        </Routes>
      </PaginationProvider>
      <Toaster />
    </>
  );
}

export default App;
