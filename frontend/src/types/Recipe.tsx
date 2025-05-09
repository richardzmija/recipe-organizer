import { Step } from './Step';
import { Ingredient } from './Ingredient';
import { Tag } from './Tag';

export interface Recipe {
  id?: string;
  name: string;
  description: string;
  image: string;
  tags: Tag[];
  ingredients: Ingredient[];
  steps: Step[];
}
