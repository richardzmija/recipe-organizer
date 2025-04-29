package edu.agh.recipe.recipes.service;

import edu.agh.recipe.recipes.dto.CreateRecipeDTO;
import edu.agh.recipe.recipes.dto.RecipeDTO;
import edu.agh.recipe.recipes.dto.UpdateRecipeDTO;
import edu.agh.recipe.tags.dto.TagDTO;
import edu.agh.recipe.tags.dto.TagReferenceDTO;

import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Set;

public interface RecipeService {
    // Core recipe operations
    Page<RecipeDTO> getAllRecipes(Pageable pageable);
    RecipeDTO getRecipeById(String id);
    byte[] getRecipeImageById(String id);
    RecipeDTO createRecipe(@Valid CreateRecipeDTO dto);
    RecipeDTO updateRecipe(String id, @Valid UpdateRecipeDTO dto);
    void uploadRecipeImage(String id, MultipartFile image);
    void deleteRecipe(String id);
    void bulkDeleteRecipes(List<String> ids);

    // Recipe search/filtering
    Page<RecipeDTO> findRecipesContainingAnyIngredients(List<String> ingredients, Pageable pageable);
    Page<RecipeDTO> findRecipesContainingAllIngredients(List<String> ingredientName, Pageable pageable);
    Page<RecipeDTO> suggestRecipesByName(String nameQuery, Pageable pageable);

    // Tag-based recipe filtering
    Page<RecipeDTO> findRecipesByTag(String tag, Pageable pageable);
    Page<RecipeDTO> findRecipesByAnyTags(List<String> tags, Pageable pageable);
    Page<RecipeDTO> findRecipesByAllTags(List<String> tags, Pageable pageable);
    Page<RecipeDTO> findRecipesByTagName(String tagName, Pageable pageable);

    // Recipe-tag relationship management
    RecipeDTO addTagsToRecipe(String recipeId, Set<TagReferenceDTO> tagReferences);
    RecipeDTO removeTagsFromRecipe(String recipeId, Set<TagReferenceDTO> tagReferences);

    // Recipe-specific tag functionality
    List<TagDTO> suggestTagsForRecipe(String recipeId, int limit);

    // Recipe-image operations
    RecipeDTO uploadImageForRecipe(String recipeId, String description, MultipartFile image);
    RecipeDTO removeImageFromRecipe(String id, String imageId);
    RecipeDTO setImageAsPrimary(String id, String imageId);
}
