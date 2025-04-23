package edu.agh.recipe.recipes.model;

import edu.agh.recipe.units.domain.MeasurementUnit;

public record RecipeIngredient (
        String ingredientName,
        MeasurementUnit unit,
        double quantity
) {}