package edu.agh.recipe.model;

public record RecipeIngredient (
        String ingredientName,
        Metric metric,
        double quantity
) {}