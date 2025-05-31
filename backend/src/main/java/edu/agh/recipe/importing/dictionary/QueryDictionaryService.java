package edu.agh.recipe.importing.dictionary;

import edu.agh.recipe.importing.model.RecipeImportFieldQuery;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Service
public class QueryDictionaryService {
    private final QueryDictionary dictionary;

    public QueryDictionaryService(QueryDictionary dictionary) {
        this.dictionary = dictionary;
    }

    public Optional<RecipeImportFieldQuery> resolveWebsite(String website) {
        return dictionary.resolve(website);
    }
}
