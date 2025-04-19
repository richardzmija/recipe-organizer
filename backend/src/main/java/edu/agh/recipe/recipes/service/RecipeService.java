package edu.agh.recipe.recipes.service;

import edu.agh.recipe.recipes.model.Recipe;
import edu.agh.recipe.recipes.model.dto.RecipeRequestDTO;
import edu.agh.recipe.recipes.repository.RecipeRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class RecipeService {

    private final RecipeRepository recipeRepository;

    public RecipeService(RecipeRepository recipeRepository) {
        this.recipeRepository = recipeRepository;
    }

    public List<Recipe> getAllRecipes() {
        return recipeRepository.findAll();
    }

    public Recipe getRecipeById(String id) {
        return recipeRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Recipe not found"));
    }

    public void addRecipe(RecipeRequestDTO dto) {
        if(dto.name().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Name cannot be empty");
        }
        Recipe recipe = new Recipe(
                dto.name(),
                dto.description(),
                dto.image(),
                dto.tags(),
                dto.ingredients(),
                dto.steps()
        );
        recipeRepository.save(recipe);
    }
}
