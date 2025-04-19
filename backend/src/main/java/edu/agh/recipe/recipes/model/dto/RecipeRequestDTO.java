package edu.agh.recipe.recipes.model.dto;

import edu.agh.recipe.recipes.model.RecipeIngredient;
import edu.agh.recipe.recipes.model.RecipeStep;

import java.util.List;

public record RecipeRequestDTO(
        String name,
        String description,
        String image,
        List<String> tags,
        List<RecipeIngredient> ingredients,
        List<RecipeStep> steps
) {}