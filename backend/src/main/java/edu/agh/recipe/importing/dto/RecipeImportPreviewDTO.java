package edu.agh.recipe.importing.dto;

import edu.agh.recipe.recipes.dto.RecipeIngredientDTO;
import edu.agh.recipe.recipes.dto.RecipeStepDTO;
import edu.agh.recipe.recipes.model.Recipe;
import edu.agh.recipe.tags.dto.TagReferenceDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Schema(description = "Preview of imported recipe without saving to database")
public record RecipeImportPreviewDTO(

        @Schema(description = "Recipe name", example = "Rhubarb and strawberry cake")
        @NotBlank
        @Size(min = 3, max = 100)
        String name,

        @Schema(description = "Short recipe description", example = "A simple yet delicious cake with rhubarb and strawberries.")
        @NotBlank
        @Size(max = 2000)
        String description,

        @Schema(description = "List of parsed ingredients")
        @NotEmpty
        List<@Valid RecipeIngredientDTO> ingredients,

        @Schema(description = "List of parsed steps")
        @NotEmpty
        List<@Valid RecipeStepDTO> steps,

        @Schema(description = "Tags assigned to the recipe")
        Set<TagReferenceDTO> tags

) {
    public static RecipeImportPreviewDTO fromEntity(Recipe recipe) {
        return new RecipeImportPreviewDTO(
                recipe.getName(),
                recipe.getDescription(),
                recipe.getIngredients().stream()
                        .map(ingredient -> new RecipeIngredientDTO(
                                ingredient.ingredientName(),
                                ingredient.unit(),
                                ingredient.quantity().getValue()
                        ))
                        .toList(),
                recipe.getSteps().stream()
                        .map(RecipeStepDTO::fromEntity)
                        .toList(),
                Set.of() // Empty tag set
        );

    }
}
