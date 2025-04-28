import { Step } from './Step';
import { Ingredient } from './Ingredient';

export interface Recipe {
  id?: string;
  name: string;
  description: string;
  image: string;
  tags: string[];
  ingredients: Ingredient[];
  steps: Step[];
}
