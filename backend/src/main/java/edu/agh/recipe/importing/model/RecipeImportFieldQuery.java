package edu.agh.recipe.importing.model;

import edu.agh.recipe.importing.service.RecipeImportService;

/**
 * Record for keeping CSS queries used for extracting  recipe properties
 * from a website in {@link RecipeImportService}.
 *
 * @see edu.agh.recipe.importing.service.RecipeImportService
*/
public record RecipeImportFieldQuery(
        String title,
        String description,
        String ingredients,
        String steps
) {
}
