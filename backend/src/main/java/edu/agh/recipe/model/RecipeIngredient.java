package edu.agh.recipe.model;

public record RecipeIngredient (
        String ingredientName,
        MeasurementUnit unit,
        double quantity
) {}