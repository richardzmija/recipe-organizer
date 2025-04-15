package edu.agh.recipe.model.dto;

import edu.agh.recipe.model.RecipeIngredient;
import edu.agh.recipe.model.RecipeStep;

import java.util.List;

public record RecipeRequestDTO(
        String name,
        String description,
        String image,
        List<String> tags,
        List<RecipeIngredient> ingredients,
        List<RecipeStep> steps
) {}