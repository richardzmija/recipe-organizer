package edu.agh.recipe.export.service;

import edu.agh.recipe.export.exception.RecipeExportException;
import edu.agh.recipe.recipes.dto.RecipeDTO;
import edu.agh.recipe.recipes.service.RecipeService;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
public class DefaultRecipeMarkdownExportService implements RecipeMarkdownExportService {

    private final RecipeService recipeService;

    public DefaultRecipeMarkdownExportService(RecipeService recipeService) {
        this.recipeService = Objects.requireNonNull(recipeService);
    }

    @Override
    public Resource exportRecipesToMarkdownZip() {
        List<RecipeDTO> allRecipes = fetchAllRecipes();
        return createZipWithMarkdownFiles(allRecipes);
    }

    @Override
    public Resource exportRecipeToMarkdownZip(String id) {
        RecipeDTO recipe = recipeService.getRecipeById(id);
        List<RecipeDTO> recipes = List.of(recipe);
        return createZipWithMarkdownFiles(recipes);
    }

    @Override
    public String convertRecipeToMarkdown(RecipeDTO recipe) {
        StringBuilder markdown = new StringBuilder();

        // Title of the recipe
        markdown.append("# ").append(recipe.name()).append("\n\n");

        // Description in the recipe
        if (recipe.description() != null && !recipe.description().isBlank()) {
            markdown.append(recipe.description()).append("\n\n");
        }

        // Ingredients are displayed in a list
        markdown.append("## Ingredients\n\n");
        for (var ingredient : recipe.ingredients()) {
            markdown.append("* ").append(ingredient.quantity())
                   .append(" ").append(ingredient.unit().getSymbol())
                   .append(" ").append(ingredient.ingredientName())
                   .append("\n");
        }
        markdown.append("\n");

        // Steps are also displayed in a list
        markdown.append("## Preparation\n\n");
        int stepCount = 1;
        for (var step : recipe.steps()) {
            if (step.title() != null && !step.title().isBlank()) {
                markdown.append("### Step ").append(stepCount).append(": ").append(step.title()).append("\n\n");
            } else {
                markdown.append("### Step ").append(stepCount).append("\n\n");
            }
            markdown.append(step.text()).append("\n\n");
            stepCount++;
        }

        return markdown.toString();
    }

    private List<RecipeDTO> fetchAllRecipes() {
        List<RecipeDTO> allRecipes = new ArrayList<>();

        int pageNumber = 0;
        int pageSize = 100;
        Page<RecipeDTO> page;

        do {
            Pageable pageable = PageRequest.of(pageNumber, pageSize);
            page = recipeService.getAllRecipes(pageable);
            allRecipes.addAll(page.getContent());
            pageNumber++;
        } while (pageNumber < page.getTotalPages());

        return allRecipes;
    }

    private Resource createZipWithMarkdownFiles(List<RecipeDTO> recipes) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             ZipOutputStream zos = new ZipOutputStream(baos)) {

            for (RecipeDTO recipe : recipes) {
                String markdown = convertRecipeToMarkdown(recipe);
                String filename = sanitizeFilename(recipe.name()) + ".md";

                ZipEntry entry = new ZipEntry(filename);
                zos.putNextEntry(entry);
                zos.write(markdown.getBytes(StandardCharsets.UTF_8));
                zos.closeEntry();
            }

            zos.finish();
            byte[] zipBytes = baos.toByteArray();
            return new ByteArrayResource(zipBytes);

        } catch (IOException e) {
            throw new RecipeExportException("Failed to export recipes to ZIP.", e);
        }
    }

    /**
     * Remove characters not allowed in a filename.
     */
    private String sanitizeFilename(String input) {
        return input.replaceAll("[^a-zA-Z0-9._-]", "_");
    }
}
