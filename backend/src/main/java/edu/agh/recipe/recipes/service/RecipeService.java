package edu.agh.recipe.recipes.service;

import edu.agh.recipe.recipes.dto.*;
import edu.agh.recipe.recipes.model.Recipe;
import edu.agh.recipe.recipes.model.RecipeIngredient;
import edu.agh.recipe.recipes.model.RecipeStep;
import edu.agh.recipe.recipes.repository.RecipeRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Objects;

@Service
@Validated
public class RecipeService {

    private final RecipeRepository recipeRepository;

    public RecipeService(RecipeRepository recipeRepository) {
        this.recipeRepository = Objects.requireNonNull(recipeRepository);
    }

    public List<RecipeDTO> getAllRecipes() {
        return recipeRepository.findAll().stream()
                .map(RecipeDTO::fromEntity)
                .toList();
    }

    public RecipeDTO getRecipeById(String id) {
        Recipe recipe = recipeRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Recipe not found"));
        return RecipeDTO.fromEntity(recipe);
    }

    public RecipeDTO createRecipe(@Valid CreateRecipeDTO dto) {
        Recipe recipe = toEntity(dto);
        Recipe savedRecipe = recipeRepository.save(recipe);
        return RecipeDTO.fromEntity(savedRecipe);
    }

    public RecipeDTO updateRecipe(String id, @Valid UpdateRecipeDTO dto) {
        if (!recipeRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Recipe not found");
        }

        Recipe recipe = toEntity(dto);
        recipe.setId(id);
        Recipe updatedRecipe = recipeRepository.save(recipe);
        return RecipeDTO.fromEntity(updatedRecipe);
    }

    public void deleteRecipe(String id) {
        if (recipeRepository.existsById(id)) {
            recipeRepository.deleteById(id);
        }
    }

    private Recipe createRecipeEntity(String name, String description,
                                     List<RecipeIngredientDTO> ingredients,
                                     List<RecipeStepDTO> steps) {
        List<RecipeIngredient> ingredientEntities = ingredients.stream()
                .map(i -> new RecipeIngredient(i.ingredientName(), i.unit(), i.quantity()))
                .toList();

        List<RecipeStep> stepEntities = steps.stream()
                .map(s -> new RecipeStep(s.title(), s.text()))
                .toList();

        return new Recipe(name, description, ingredientEntities, stepEntities);
    }

    private Recipe toEntity(RecipeDTO dto) {
        Recipe recipe = createRecipeEntity(dto.name(), dto.description(),
                                         dto.ingredients(), dto.steps());
        recipe.setId(dto.id());
        return recipe;
    }

    private Recipe toEntity(CreateRecipeDTO dto) {
        return createRecipeEntity(dto.name(), dto.description(),
                                dto.ingredients(), dto.steps());
    }

    private Recipe toEntity(UpdateRecipeDTO dto) {
        return createRecipeEntity(dto.name(), dto.description(),
                                dto.ingredients(), dto.steps());
    }
}