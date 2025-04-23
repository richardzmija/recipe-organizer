import { Routes, Route } from 'react-router-dom';
import Header from '@/components/common/Header';
import OptionsBar from '@/components/common/OptionsBar';
import HomePage from '@/pages/HomePage';
import AddRecipePage from '@/pages/AddRecipePage';
import { Toaster } from '@/components/ui/toaster';

function App() {
  return (
    <>
      <Header />
      <OptionsBar />
      <Routes>
        <Route path='/' element={<HomePage />} />
        <Route path='/add' element={<AddRecipePage />} />
      </Routes>
      <Toaster />
    </>
  );
}

export default App;
