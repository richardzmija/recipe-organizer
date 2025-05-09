package edu.agh.recipe.recipes.dto;

import edu.agh.recipe.images.dto.ImageDTO;
import edu.agh.recipe.recipes.model.Recipe;
import edu.agh.recipe.tags.dto.TagDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
    List<RecipeIngredientResponseDTO> ingredients,

    @NotEmpty(message = "Recipe must have at least one step.")
    List<@Valid RecipeStepDTO> steps,

    Set<TagDTO> tags,

    Set<ImageDTO> images
) {
    public static RecipeDTO fromEntity(Recipe recipe, List<TagDTO> tags, List<ImageDTO> images) {
        return new RecipeDTO(
            recipe.getId(),
            recipe.getName(),
            recipe.getDescription(),
            recipe.getIngredients().stream().map(RecipeIngredientResponseDTO::fromEntity).toList(),
            recipe.getSteps().stream().map(RecipeStepDTO::fromEntity).toList(),
            new HashSet<>(tags),
            new HashSet<>(images)
        );
    }
}