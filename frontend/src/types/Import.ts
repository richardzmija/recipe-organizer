import { Recipe } from './Recipe';

export interface ImportResponse {
  jobId: string;
  status: 'STARTED' | 'IN_PROGRESS' | 'COMPLETED' | 'FAILED';
  result: Recipe | null;
  errorMessage: string | null;
}
