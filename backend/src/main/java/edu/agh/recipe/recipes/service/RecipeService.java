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

    Page<RecipeDTO> getRecipesByIngredients(List<String> ingredients, Pageable pageable);

}
