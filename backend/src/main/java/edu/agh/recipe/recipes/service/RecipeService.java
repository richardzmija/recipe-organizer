package edu.agh.recipe.recipes.service;

import edu.agh.recipe.recipes.dto.CreateRecipeDTO;
import edu.agh.recipe.recipes.dto.RecipeDTO;
import edu.agh.recipe.recipes.dto.UpdateRecipeDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface RecipeService {

    Page<RecipeDTO> getAllRecipes(Pageable pageable);
    RecipeDTO getRecipeById(String id);
    RecipeDTO createRecipe(CreateRecipeDTO dto);
    RecipeDTO updateRecipe(String id, UpdateRecipeDTO dto);
    void deleteRecipe(String id);

}
