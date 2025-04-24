package edu.agh.recipe.export.service;

import edu.agh.recipe.recipes.dto.RecipeDTO;
import org.springframework.core.io.Resource;

public interface RecipeMarkdownExportService {

    Resource exportRecipesToMarkdownZip();
    Resource exportRecipeToMarkdownZip(String id);
    String convertRecipeToMarkdown(RecipeDTO recipe);

}
