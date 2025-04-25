package edu.agh.recipe.recipes.service;

import edu.agh.recipe.recipes.dto.CreateRecipeDTO;
import edu.agh.recipe.recipes.dto.RecipeDTO;
import edu.agh.recipe.recipes.dto.UpdateRecipeDTO;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;

public interface RecipeService {

    Page<RecipeDTO> getAllRecipes(Pageable pageable);
    RecipeDTO getRecipeById(String id);
    RecipeDTO createRecipe(@Valid CreateRecipeDTO dto);
    RecipeDTO updateRecipe(String id, @Valid UpdateRecipeDTO dto);
    void deleteRecipe(String id);

    /**
     * Finds recipes containing *any* of the specified ingredients.
     * Ingredient names are matched case-insensitively.
     */
    Page<RecipeDTO> findRecipesContainingAnyIngredients(List<String> ingredients, Pageable pageable);

    /**
     * Finds recipes that contain *all* of the specified ingredients.
     * Ingredient names are matched case-insensitively.
     */
    Page<RecipeDTO> findRecipesContainingAllIngredients(List<String> ingredientName, Pageable pageable);

    /**
     * Finds recipes whose names start with the given query, case-insensitively.
     */
    Page<RecipeDTO> suggestRecipesByName(String nameQuery, Pageable pageable);
}
