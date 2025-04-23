package edu.agh.recipe.recipes.dto;

import edu.agh.recipe.recipes.model.RecipeStep;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RecipeStepDTO(
    @NotBlank(message = "Step title is required.")
    @Size(max = 100, message = "Title must not exceed 100 characters.")
    String title,

    @NotBlank(message = "Step text is required.")
    @Size(max = 1000, message = "Step text must not exceed 1000 characters.")
    String text
) {
    public static RecipeStepDTO fromEntity(RecipeStep step) {
        return new RecipeStepDTO(
            step.title(),
            step.text()
        );
    }
}