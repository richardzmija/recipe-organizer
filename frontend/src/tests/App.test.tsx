import { render, screen } from '@testing-library/react';
import { expect, test } from 'vitest';
import App from '../App';
import { Provider } from '@/components/ui/provider';

test('renders name', async () => {
  render(
    <Provider>
      <App />
    </Provider>,
  );
  const element = screen.getByText('Test');

  expect(element).toBeInTheDocument();
});
