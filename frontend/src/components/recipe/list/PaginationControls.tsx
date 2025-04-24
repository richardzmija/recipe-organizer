import React from 'react';
import { HStack, Button, Text } from '@chakra-ui/react';

interface PaginationProps {
  pagination: {
    size: number;
    number: number;
    totalElements: number;
    totalPages: number;
  };
  handlePageChange: (page: number) => void;
}

const PaginationControls = ({ pagination, handlePageChange }: PaginationProps) => {
  if (pagination.totalPages <= 1) return null;

  return (
    <HStack justifyContent='center' gap={2} mt={4}>
      <Button disabled={pagination.number === 0} onClick={() => handlePageChange(pagination.number - 1)} size='sm'>
        Previous
      </Button>

      <Text>
        Page {pagination.number + 1} of {pagination.totalPages}
      </Text>

      <Button
        disabled={pagination.number >= pagination.totalPages - 1}
        onClick={() => handlePageChange(pagination.number + 1)}
        size='sm'>
        Next
      </Button>
    </HStack>
  );
};

export default PaginationControls;
