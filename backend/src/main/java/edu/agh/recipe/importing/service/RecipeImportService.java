package edu.agh.recipe.importing.service;

import edu.agh.recipe.importing.dto.RecipeImportPreviewDTO;
import edu.agh.recipe.recipes.dto.RecipeIngredientDTO;
import edu.agh.recipe.recipes.dto.RecipeStepDTO;
import edu.agh.recipe.recipes.model.Recipe;
import edu.agh.recipe.recipes.model.RecipeIngredient;
import edu.agh.recipe.recipes.model.RecipeStep;
import edu.agh.recipe.recipes.repository.RecipeRepository;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Service
public class RecipeImportService {

    private final RecipeRepository recipeRepository;

    public RecipeImportService(RecipeRepository recipeRepository) {
        this.recipeRepository = Objects.requireNonNull(recipeRepository);
    }

    public Recipe importRecipe(String url) throws IOException {
        if (url.contains("mojewypieki.com")) {
            return importFromMojewypieki(url);
        }
        // else if (url.contains("other-website.com")) {
        //     return importFromOtherWebsite(url);
        // }
        else {
            throw new IllegalArgumentException("Unsupported source URL: " + url);
        }
    }

    public RecipeImportPreviewDTO previewRecipe(String url) throws IOException {
        if (url.contains("mojewypieki.com")) {
            return previewFromMojewypieki(url);
        }
        // other conditions (see above)
        else {
            throw new IllegalArgumentException("Unsupported source URL: " + url);
        }
    }

    public Recipe importAndSaveRecipe(String url) throws IOException {
        Recipe recipe = importRecipe(url);
        return recipeRepository.save(recipe);
    }

    public Recipe importFromMojewypieki(String url) throws IOException {
        Document doc = Jsoup.connect(url).get();

        String title = doc.select("div.title h1").text();
        String description = doc.select("div.article__content blockquote p em").text();

        List<RecipeIngredient> ingredients = doc.select("div.article__content ul li").stream()
                .map(Element::text)
                .map(IngredientParser::parse)
                .toList();

        Elements stepElements = doc.select("div.article__content > p[style=\"text-align: justify;\"]");

        List<RecipeStep> steps = new ArrayList<>();
        for (int i = 0; i < stepElements.size(); i++) {
            Element step = stepElements.get(i);
            String text = step.text();
            steps.add(new RecipeStep("Step " + (i + 1), text));
        }

        Recipe recipe = new Recipe();

        recipe.setName(title);
        recipe.setDescription(description);
        recipe.setIngredients(ingredients);
        recipe.setSteps(steps);

        return recipe;
    }

    public RecipeImportPreviewDTO previewFromMojewypieki(String url) throws IOException {
        Recipe recipe = importFromMojewypieki(url);

        return new RecipeImportPreviewDTO(
                recipe.getName(),
                recipe.getDescription(),
                recipe.getIngredients().stream()
                        .map(ingredient -> new RecipeIngredientDTO(
                                ingredient.ingredientName(),
                                ingredient.unit(),
                                ingredient.quantity().getValue()
                        ))
                        .toList(),
                recipe.getSteps().stream()
                        .map(step -> new RecipeStepDTO(step.title(), step.text()))
                        .toList(),
                Set.of() // Default: empty set of tags.
        );
    }

    // private Recipe importFromOtherWebsite(String url) { ... }
}