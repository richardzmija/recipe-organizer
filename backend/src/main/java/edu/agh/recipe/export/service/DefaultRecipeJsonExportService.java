package edu.agh.recipe.export.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.agh.recipe.export.exception.RecipeExportException;
import edu.agh.recipe.recipes.dto.RecipeDTO;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

@Service
public class DefaultRecipeJsonExportService implements RecipeJsonExportService {

    private final RecipeDataFetcher recipeDataFetcher;
    private final ObjectMapper objectMapper;

    public DefaultRecipeJsonExportService(RecipeDataFetcher recipeDataFetcher, ObjectMapper objectMapper) {
        this.recipeDataFetcher = Objects.requireNonNull(recipeDataFetcher);
        this.objectMapper = Objects.requireNonNull(objectMapper);
    }

    @Override
    public Resource exportRecipesToJson() {
        List<RecipeDTO> allRecipes = recipeDataFetcher.fetchAllRecipes();
        return createJsonFile(allRecipes);
    }

    @Override
    public Resource exportRecipeToJson(String id) {
        RecipeDTO recipe = recipeDataFetcher.fetchRecipeById(id);
        List<RecipeDTO> recipes = List.of(recipe);
        return createJsonFile(recipes);
    }

    private Resource createJsonFile(List<RecipeDTO> recipes) {
        try {
            byte[] jsonBytes = objectMapper.writeValueAsBytes(recipes);
            return new ByteArrayResource(jsonBytes);
        } catch (IOException e) {
            throw new RecipeExportException("Failed to export recipes to JSON.", e);
        }
    }
}
