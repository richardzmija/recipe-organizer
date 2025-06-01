import { createContext, useContext, useState } from 'react';

interface PaginationParams {
  size: number;
  number: number;
  totalElements: number;
  totalPages: number;
}

interface SearchParams {
  name?: string;
  ingredients: string[];
  tagIds: string[];
  sortField: 'name' | 'created_date' | 'modification_date' | 'last_access_date';
  direction: 'asc' | 'desc';
  pageNumber: number;
  size: number;
}

interface PaginationContextType {
  pagination: PaginationParams;
  setPagination: (pagination: PaginationParams) => void;
  scrollY: number;
  setScrollY: (scrollY: number) => void;
  searchParams: SearchParams;
  setSearchParams: (searchParams: SearchParams) => void;
  showOnlyFavorites: boolean;
  setShowOnlyFavorites: (showOnlyFavorites: boolean) => void;
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
  const [searchParams, setSearchParams] = useState<SearchParams>({
    name: undefined,
    ingredients: [],
    tagIds: [],
    sortField: 'name',
    direction: 'asc',
    pageNumber: 0,
    size: 10,
  });
  const [showOnlyFavorites, setShowOnlyFavorites] = useState(false);

  return (
    <PaginationContext.Provider
      value={{
        pagination,
        setPagination,
        scrollY,
        setScrollY,
        searchParams,
        setSearchParams,
        showOnlyFavorites,
        setShowOnlyFavorites,
      }}>
      {children}
    </PaginationContext.Provider>
  );
};

export const usePaginationContext = () => {
  const context = useContext(PaginationContext);
  if (!context) throw new Error('useRecipesContext must be used within RecipesProvider');
  return context;
};
