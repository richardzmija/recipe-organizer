package edu.agh.recipe.recipes.service;

import edu.agh.recipe.recipes.dto.*;
import edu.agh.recipe.recipes.model.Recipe;
import edu.agh.recipe.recipes.model.RecipeIngredient;
import edu.agh.recipe.recipes.model.RecipeStep;
import edu.agh.recipe.recipes.repository.RecipeRepository;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Objects;

@Service
@Validated
public class DefaultRecipeService implements RecipeService {

    private final RecipeRepository recipeRepository;

    public DefaultRecipeService(RecipeRepository recipeRepository) {
        this.recipeRepository = Objects.requireNonNull(recipeRepository);
    }

    @Override
    public Page<RecipeDTO> getAllRecipes(Pageable pageable) {
        Page<Recipe> recipePage = recipeRepository.findAll(pageable);
        return recipePage.map(RecipeDTO::fromEntity);
    }

    @Override
    public RecipeDTO getRecipeById(String id) {
        Recipe recipe = recipeRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Recipe not found"));
        return RecipeDTO.fromEntity(recipe);
    }

    @Override
    public RecipeDTO createRecipe(@Valid CreateRecipeDTO dto) {
        Recipe recipe = toEntity(dto);
        Recipe savedRecipe = recipeRepository.save(recipe);
        return RecipeDTO.fromEntity(savedRecipe);
    }

    @Override
    public RecipeDTO updateRecipe(String id, @Valid UpdateRecipeDTO dto) {
        if (!recipeRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Recipe not found");
        }

        Recipe recipe = toEntity(dto);
        recipe.setId(id);
        Recipe updatedRecipe = recipeRepository.save(recipe);
        return RecipeDTO.fromEntity(updatedRecipe);
    }

    @Override
    public void deleteRecipe(String id) {
        if (recipeRepository.existsById(id)) {
            recipeRepository.deleteById(id);
        }
    }

    @Override
    public void bulkDeleteRecipes(List<String> ids) {
        List<String> existingIds = recipeRepository.findAllById(ids)
                .stream()
                .map(Recipe::getId)
                .toList();

        if (existingIds.size() != ids.size()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "One of more recipes not found.");
        }

        recipeRepository.deleteAllById(ids);
    }

    @Override
    public Page<RecipeDTO> findRecipesContainingAnyIngredients(List<String> ingredients, Pageable pageable) {
        List<String> lowerCaseIngredients = ingredients.stream()
                .map(String::toLowerCase)
                .toList();
        Page<Recipe> recipePage = recipeRepository.findByIngredientsIngredientNameIn(lowerCaseIngredients, pageable);
        return recipePage.map(RecipeDTO::fromEntity);
    }

    @Override
    public Page<RecipeDTO> findRecipesContainingAllIngredients(List<String> ingredientNames, Pageable pageable) {
        List<String> lowerCaseIngredientNames = ingredientNames.stream()
                .map(String::toLowerCase)
                .toList();
        Page<Recipe> recipePage = recipeRepository.findByAllIngredientsContaining(lowerCaseIngredientNames, pageable);
        return recipePage.map(RecipeDTO::fromEntity);
    }

    @Override
    public Page<RecipeDTO> suggestRecipesByName(String nameQuery, Pageable pageable) {
        Page<Recipe> recipePage = recipeRepository.findByNameStartingWithIgnoreCase(nameQuery, pageable);
        return recipePage.map(RecipeDTO::fromEntity);
    }

    private Recipe createRecipeEntity(String name, String description,
                                      List<RecipeIngredientDTO> ingredients,
                                      List<RecipeStepDTO> steps) {
        List<RecipeIngredient> ingredientEntities = ingredients.stream()
                .map(i -> RecipeIngredient.of(i.ingredientName(), i.unit(), i.quantity()))
                .toList();

        List<RecipeStep> stepEntities = steps.stream()
                .map(s -> new RecipeStep(s.title(), s.text()))
                .toList();

        return new Recipe(name, description, ingredientEntities, stepEntities);
    }

    private Recipe toEntity(RecipeDTO dto) {
        List<RecipeIngredientDTO> ingredients = dto.ingredients().stream()
                .map(ri -> new RecipeIngredientDTO(ri.ingredientName(), ri.unit(), ri.quantity()))
                .toList();

        Recipe recipe = createRecipeEntity(dto.name(), dto.description(),
                ingredients, dto.steps());
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
