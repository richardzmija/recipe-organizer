package edu.agh.recipe.importrecipe.dto;

import edu.agh.recipe.recipes.model.RecipeIngredient;
import edu.agh.recipe.recipes.model.RecipeStep;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "Preview of imported recipe without saving to database")
public class RecipeImportPreviewDTO {

    @Schema(description = "Recipe name", example = "Ciasto kruche z rabarbarem i truskawkami")
    private String name;

    @Schema(description = "Short recipe description", example = "Proste a pyszne ciasto wiosenno – letnie: ciasto kruche z rabarbarem i truskawkami.")
    private String description;

    @Schema(description = "List of parsed ingredients")
    private List<RecipeIngredient> ingredients;

    @Schema(description = "List of parsed recipe steps")
    private List<RecipeStep> steps;

    public RecipeImportPreviewDTO() {}

    public RecipeImportPreviewDTO(String name, String description, List<RecipeIngredient> ingredients, List<RecipeStep> steps) {
        this.name = name;
        this.description = description;
        this.ingredients = ingredients;
        this.steps = steps;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public List<RecipeIngredient> getIngredients() {
        return ingredients;
    }

    public List<RecipeStep> getSteps() {
        return steps;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setIngredients(List<RecipeIngredient> ingredients) {
        this.ingredients = ingredients;
    }

    public void setSteps(List<RecipeStep> steps) {
        this.steps = steps;
    }
}
