import { Box } from '@chakra-ui/react';
import { JSX } from '@emotion/react/jsx-runtime';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import RecipeDetails from './pages/RecipeDetails';
import Navbar from './components/ui/Navbar';

function App(): JSX.Element {
  return (
    <Router>
      <Box>
        <Navbar />
        <Box>
          <Routes>
            <Route path='/recipe/:id' element={<RecipeDetails />} />
          </Routes>
        </Box>
      </Box>
    </Router>
  );
}

export default App;
