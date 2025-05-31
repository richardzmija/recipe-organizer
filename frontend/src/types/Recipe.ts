import { Step } from './Step';
import { Ingredient } from './Ingredient';
import { Tag } from './Tag';

export interface Image {
  id: string;
  filename: string;
  contentType: string;
  description: string;
  isPrimary: boolean;
  uploadDate: string;
}

export interface Recipe {
  id?: string;
  name: string;
  description: string;
  images: Image[];
  tags: Tag[];
  ingredients: Ingredient[];
  steps: Step[];
}
