package edu.agh.recipe.recipes.dto;

import edu.agh.recipe.recipes.model.Recipe;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public record RecipeDTO(
    @NotNull(message = "ID is required.")
    String id,

    @NotBlank(message = "Recipe name is required.")
    @Size(min = 3, max = 100, message = "Recipe name must be between 3 and 100 characters.")
    String name,

    @NotBlank(message = "Description is required.")
    @Size(max = 2000, message = "Description must not exceed 2000 characters.")
    String description,

    @NotEmpty(message = "Recipe must have at least one ingredient.")
    List<@Valid RecipeIngredientDTO> ingredients,

    @NotEmpty(message = "Recipe must have at least one step.")
    List<@Valid RecipeStepDTO> steps
) {
    public static RecipeDTO fromEntity(Recipe recipe) {
        return new RecipeDTO(
            recipe.getId(),
            recipe.getName(),
            recipe.getDescription(),
            recipe.getIngredients().stream().map(RecipeIngredientDTO::fromEntity).toList(),
            recipe.getSteps().stream().map(RecipeStepDTO::fromEntity).toList()
        );
    }
}