package edu.agh.recipe.importing.dictionary;

import edu.agh.recipe.importing.model.RecipeImportFieldQuery;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Component
public class DefaultQueryDictionary implements QueryDictionary {
    private final Map<String, RecipeImportFieldQuery> queryMap = new HashMap<>();

    public DefaultQueryDictionary() {
        // Can be moved to a configuration file or a database.
        queryMap.put("mojewypieki.com", new RecipeImportFieldQuery(
                "div.title h1",
                "div.article__content blockquote p em",
                "div.article__content ul li",
                "div.article__content > p[style=\"text-align: justify;\"]"
            )
        );
        queryMap.put("kwestiasmaku.com", new RecipeImportFieldQuery(
                ".name h1",
                ".field-name-field-uwagi-wstepne p",
                ".field-name-field-skladniki ul li",
                ".field-name-field-przygotowanie ul li"
            )
        );
        queryMap.put("aniagotuje.pl", new RecipeImportFieldQuery(
                "h1[itemprop=\"name\"]",
                ".article-intro p",
                "#recipeIngredients ul li span.ingredient",
                ".copy-share-lock-con + div p"
                )
        );
        queryMap.put("przepisy.pl", new RecipeImportFieldQuery(
                "h1.title",
                "h2.subtitle",
                ".ingredient-name .text-bg-white",
                "p.step-info-description"
                )
        );
        queryMap.put("poprostupycha.com.pl", new RecipeImportFieldQuery(
                "h1.entry-title",
                ".recipe-desc p",
                "li.ingredient ",
                ".step .three_fourth p"
                )
        );
    }

    @Override
    public Optional<RecipeImportFieldQuery> resolve(String website) {
        if (website == null) return Optional.empty();
        return Optional.ofNullable(queryMap.get(website.toLowerCase().trim()));
    }
}
