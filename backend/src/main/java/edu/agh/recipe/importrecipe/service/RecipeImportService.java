package edu.agh.recipe.importrecipe.service;

import edu.agh.recipe.recipes.model.Recipe;
import edu.agh.recipe.recipes.model.RecipeIngredient;
import edu.agh.recipe.recipes.model.RecipeStep;
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
    public Recipe importFromMojewypieki(String url) throws IOException {
        Document doc = Jsoup.connect(url).get();

        String title = doc.select("div.title h1").text();
        String description = doc.select("div.article__content blockquote p em").text();

        Elements ingredientElements = doc.select("div.article__content ul li");

        List<RecipeIngredient> ingredients = new ArrayList<>();
        for (Element li : ingredientElements) {
            String rawIngredient = li.text();

            // Póki co: nie próbujemy parsować jednostek ani ilości
            ingredients.add(new RecipeIngredient(rawIngredient, null, null));
        }

        Elements stepElements = doc.select("div.article__content > p[style=\"text-align: justify;\"]");
//        for (Element p : stepElements) {
//            String text = p.text();
//            System.out.println("- step: " + text);
//        }

        List<RecipeStep> steps = new ArrayList<>();
        for (int i = 0; i < stepElements.size(); i++) {
            Element step = stepElements.get(i);
            String text = step.text();
            steps.add(new RecipeStep("Krok " + (i + 1), text));
        }

        Recipe recipe = new Recipe();


        recipe.setName(title);
        recipe.setDescription(description);
        recipe.setIngredients(ingredients);
        recipe.setSteps(steps);

        // save in database?

        return recipe;
    }
}
