package edu.agh.recipe.importing.service;

import edu.agh.recipe.importing.dto.RecipeImportPreviewDTO;
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

@Service
public class RecipeImportService {
    private final RecipeRepository recipeRepository;

    public Recipe importFromMojewypieki(String url) throws IOException {
        Document doc = Jsoup.connect(url).get();

        String title = doc.select("div.title h1").text();
        String description = doc.select("div.article__content blockquote p em").text();

        List<RecipeIngredient> ingredients = doc.select("div.article__content ul li").stream()
                .map(Element::text)
                .map(IngredientParser::parse)
                .toList();

//        List<RecipeIngredient> ingredients = new ArrayList<>();
//        for (Element li : ingredientElements) {
//            String rawIngredient = li.text();
//
//            // Póki co: nie próbujemy parsować jednostek ani ilości
//            ingredients.add(new RecipeIngredient(rawIngredient, null, null));
//        }

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

        // save in database?

        return recipe;
    }

    public RecipeImportPreviewDTO previewFromMojewypieki(String url) throws IOException {
        Recipe recipe = importFromMojewypieki(url);

        return new RecipeImportPreviewDTO(
                recipe.getName(),
                recipe.getDescription(),
                recipe.getIngredients(),
                recipe.getSteps()
        );
    }


    public RecipeImportService(RecipeRepository recipeRepository) {
        this.recipeRepository = recipeRepository;
    }

    public Recipe importAndSaveFromMojewypieki(String url) throws IOException {
        Recipe recipe = importFromMojewypieki(url);
        return recipeRepository.save(recipe);
    }
}
