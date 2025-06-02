package edu.agh.recipe.importing.service;

import edu.agh.recipe.importing.dictionary.QueryDictionaryService;
import edu.agh.recipe.importing.dto.RecipeImportPreviewDTO;
import edu.agh.recipe.importing.exception.RecipeImportException;
import edu.agh.recipe.importing.model.RecipeImportFieldQuery;
import edu.agh.recipe.recipes.dto.RecipeIngredientDTO;
import edu.agh.recipe.recipes.dto.RecipeStepDTO;
import edu.agh.recipe.recipes.repository.RecipeRepository;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;

@Service
public class RecipeImportService {
    private static final Logger log = LoggerFactory.getLogger(IngredientParser.class);

    private final RecipeRepository recipeRepository;
    private final IngredientParserService ingredientParser;
    private final QueryDictionaryService dictionaryService;

    public RecipeImportService(
            RecipeRepository recipeRepository,
            IngredientParserService ingredientParser,
            QueryDictionaryService dictionaryService) {
        this.recipeRepository = Objects.requireNonNull(recipeRepository);
        this.ingredientParser = ingredientParser;
        this.dictionaryService = dictionaryService;
    }

    public RecipeImportPreviewDTO importRecipe(String url) {
        try {
            URI uri = new URI(url);
            String host = uri.getHost();
            String domainName = host.startsWith("www.") ? host.substring(4) : host;
            Optional<RecipeImportFieldQuery> importFields = dictionaryService.resolveWebsite(domainName);

            if (importFields.isEmpty()) {
                throw new RecipeImportException("The domain \"" + domainName + "\" is not supported.");
            }

            Document doc = Jsoup.connect(url).get();

            String title = doc.select(importFields.get().title()).text();
            String description = doc.select(importFields.get().description()).text();

            List<RecipeIngredientDTO> ingredients = doc.select(importFields.get().ingredients()).stream()
                    .map(Element::text)
                    .map(text -> {
                        Optional<RecipeIngredientDTO> parsed = ingredientParser.parse(text);
                        if (parsed.isEmpty()) {
                            log.warn("Could not parse ingredient line: '{}'", text);
                        }
                        return parsed;
                    })
                    .flatMap(Optional::stream) // Skips cases where parse(...) returns Optional.empty().
                    .toList();

            Elements stepElements = doc.select(importFields.get().steps());

            List<RecipeStepDTO> steps = new ArrayList<>();
            for (int i = 0; i < stepElements.size(); i++) {
                Element step = stepElements.get(i);
                String text = step.text();
                steps.add(new RecipeStepDTO("Step " + (i + 1), text));
            }

            return new RecipeImportPreviewDTO(title, description, ingredients, steps, Set.of());


        } catch (URISyntaxException e) {
            throw new IllegalArgumentException("Incorrect URL Syntax: " + e.getMessage());
        } catch (IOException e) {
            throw new RecipeImportException("Failed to import recipe from " + url, e);
        }
    }
}
