package edu.agh.recipe.recipes.repository;

import edu.agh.recipe.recipes.model.Recipe;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RecipeRepository extends MongoRepository<Recipe, String> {
}
