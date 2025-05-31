package edu.agh.recipe.recipes.repository;

import edu.agh.recipe.recipes.model.Recipe;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Repository
public interface RecipeRepository extends MongoRepository<Recipe, String> {
    Page<Recipe> findByIngredientsIngredientNameIn(List<String> ingredientNames, Pageable pageable);

    /**
     * Finds recipes that contain all the specified ingredient names.
     * It assumes that the recipes in the database and in the input are
     * in lowercase.
     */
    @Query("{ 'ingredients.ingredientName': { $all: ?0 } }")
    Page<Recipe> findByAllIngredientsContaining(List<String> ingredientNames, Pageable pageable);

    /**
     * Finds recipes whose names start with the given prefix, case-insensitively.
     * It is used for search suggestions.
     */
    @Query("{ 'name':  { $regex: '^?0', $options:  'i' } }")
    Page<Recipe> findByNameStartingWithIgnoreCase(String namePrefix, Pageable pageable);

    /**
     * Find recipes by tag id.
     */
    Page<Recipe> findByTagIdsContaining(String tagId, Pageable pageable);

    /**
     * Find recipes with any of the given tag ids.
     */
    @Query("{ 'tagIds': { $in: ?0 } }")
    Page<Recipe> findByTagIdsIn(List<String> tagIds, Pageable pageable);

    /**
     * Find recipes with all the given tag ids.
     */
    @Query("{ 'tagIds': { $all: ?0 } }")
    Page<Recipe> findByTagIdsAll(List<String> tagIds, Pageable pageable);

    @Query("{ $and: [ " +
           "?#{ [0] == null ? { $expr: true } : { 'name': { $regex: [0], $options: 'i' } } }, " +
           "?#{ [1] == null ? { $expr: true } : { 'ingredients.ingredientName': { $all: [1] } } }, " +
           "?#{ [2] == null ? { $expr: true } : { 'tagIds': { $all: [2] } } } " +
           "] }")
    Page<Recipe> advancedSearch(String name, List<String> ingredients, List<String> tagIds, Pageable pageable);
}
