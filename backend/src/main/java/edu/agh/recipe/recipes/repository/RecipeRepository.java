package edu.agh.recipe.recipes.repository;

import edu.agh.recipe.recipes.model.Recipe;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Repository
public interface RecipeRepository extends MongoRepository<Recipe, String> {
    Page<Recipe> findByIngredientsIngredientNameIn(List<String> ingredientNames, Pageable pageable);
}
