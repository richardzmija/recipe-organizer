package edu.agh.recipe.recipes.dto;

import edu.agh.recipe.units.domain.MeasurementUnit;
import edu.agh.recipe.recipes.model.RecipeIngredient;

/**
 * DTO for recipe ingredients that contains the formatted quantity for the ingredient.
 */
public record RecipeIngredientResponseDTO(
        String ingredientName,
        MeasurementUnit unit,
        Double quantity,
        String formattedQuantity
) {
    public static RecipeIngredientResponseDTO fromEntity(RecipeIngredient ingredient) {
        return new RecipeIngredientResponseDTO(
                ingredient.ingredientName(),
                ingredient.unit(),
                ingredient.quantity().getValue(),
                ingredient.quantity().getFormattedValue()
        );
    }
}