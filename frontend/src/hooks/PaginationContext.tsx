import { createContext, useContext, useState } from 'react';

interface PaginationParams {
  size: number;
  number: number;
  totalElements: number;
  totalPages: number;
}

interface PaginationContextType {
  pagination: PaginationParams;
  setPagination: (pagination: PaginationParams) => void;
  scrollY: number;
  setScrollY: (scrollY: number) => void;
}

const PaginationContext = createContext<PaginationContextType | null>(null);

export const PaginationProvider = ({ children }: { children: React.ReactNode }) => {
  const [pagination, setPagination] = useState<PaginationParams>({
    size: 10,
    number: 0,
    totalElements: 0,
    totalPages: 0,
  });
  const [scrollY, setScrollY] = useState(0);

  return (
    <PaginationContext.Provider value={{ pagination, setPagination, scrollY, setScrollY }}>
      {children}
    </PaginationContext.Provider>
  );
};

export const usePaginationContext = () => {
  const context = useContext(PaginationContext);
  if (!context) throw new Error('useRecipesContext must be used within RecipesProvider');
  return context;
};
