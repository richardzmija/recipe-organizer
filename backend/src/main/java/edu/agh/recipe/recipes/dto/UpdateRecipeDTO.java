package edu.agh.recipe.recipes.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import java.util.List;
import java.util.Set;
import edu.agh.recipe.tags.dto.TagReferenceDTO;

public record UpdateRecipeDTO(
    @NotBlank(message = "Recipe name is required.")
    @Size(min = 3, max = 100, message = "Recipe name must be between 3 and 100 characters.")
    String name,

    @NotBlank(message = "Description is required.")
    @Size(max = 2000, message = "Description must not exceed 2000 characters.")
    String description,

    @NotEmpty(message = "Recipe must have at least one ingredient.")
    List<@Valid RecipeIngredientDTO> ingredients,

    @NotEmpty(message = "Recipe must have at least one step.")
    List<@Valid RecipeStepDTO> steps,

    Set<TagReferenceDTO> tags
) {}
