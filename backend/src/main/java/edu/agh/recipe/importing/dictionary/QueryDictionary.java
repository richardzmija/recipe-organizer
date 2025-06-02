package edu.agh.recipe.importing.dictionary;

import edu.agh.recipe.importing.model.RecipeImportFieldQuery;

import java.util.Map;
import java.util.Optional;

public interface QueryDictionary {
    /**
     * Tries to resolve a website name into a {@link RecipeImportFieldQuery}.
     *
     * @param website the website and the domain, used inside URL, e.g. "mojewypieki.com", "kwestiasmaku.com"
     * @return Optional with corresponding {@link RecipeImportFieldQuery} or empty if not recognized
     */
    Optional<RecipeImportFieldQuery> resolve(String website);
}
