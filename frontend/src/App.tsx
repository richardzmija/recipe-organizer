import { Box } from '@chakra-ui/react';
import { JSX } from '@emotion/react/jsx-runtime';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import RecipeDetails from './components/RecipeDetails';
import Navbar from './components/Navbar';

function App(): JSX.Element {
  return (
    <Router>
      <Box>
        <Navbar />
        <Box p={4}>
          <Routes>
            <Route path='/recipe/:id' element={<RecipeDetails />} />
          </Routes>
        </Box>
      </Box>
    </Router>
  );
}

export default App;
