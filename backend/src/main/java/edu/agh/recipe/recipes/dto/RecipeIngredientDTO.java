package edu.agh.recipe.recipes.dto;

import edu.agh.recipe.recipes.model.RecipeIngredient;
import edu.agh.recipe.units.domain.MeasurementUnit;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/**
 * Default DTO for recipe ingredients that is used for example for requests.
 */
public record RecipeIngredientDTO(
    @NotBlank(message = "Ingredient name is required.")
    String ingredientName,

    @NotNull(message = "Measurement unit is required.")
    MeasurementUnit unit,

    @NotNull(message = "Quantity is required.")
    @Positive(message = "Quantity must be positive.")
    Double quantity
) {
    public RecipeIngredient toEntity() {
        return RecipeIngredient.of(ingredientName, unit, quantity);
    }
}