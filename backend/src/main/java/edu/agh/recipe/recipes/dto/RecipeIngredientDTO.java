package edu.agh.recipe.recipes.dto;

import edu.agh.recipe.recipes.model.RecipeIngredient;
import edu.agh.recipe.units.domain.MeasurementUnit;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record RecipeIngredientDTO(
    @NotBlank(message = "Ingredient name is required.")
    String ingredientName,

    @NotNull(message = "Measurement unit is required.")
    MeasurementUnit unit,

    @NotNull(message = "Quantity is required.")
    @Positive(message = "Quantity must be positive.")
    Double quantity
) {
    public static RecipeIngredientDTO fromEntity(RecipeIngredient ingredient) {
        return new RecipeIngredientDTO(
            ingredient.ingredientName(),
            ingredient.unit(),
            ingredient.quantity()
        );
    }
}