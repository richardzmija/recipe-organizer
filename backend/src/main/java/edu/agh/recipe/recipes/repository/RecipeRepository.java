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
}
