package edu.agh.recipe.export.service;

import org.springframework.core.io.Resource;

public interface RecipeJsonExportService {

    Resource exportRecipesToJson();
    Resource exportRecipeToJson(String id);

}
