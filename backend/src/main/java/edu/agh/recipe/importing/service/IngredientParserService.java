package edu.agh.recipe.importing.service;

import edu.agh.recipe.recipes.dto.RecipeIngredientDTO;
import edu.agh.recipe.recipes.model.RecipeIngredient;

import java.util.Optional;

/**
 * Service interface responsible for parsing a single line of ingredient text
 * into a structured {@link RecipeIngredient} object.
 *
 * <p>This interface allows for flexible parsing strategies, including language-specific
 * implementations or support for various input formats. Implementations of this interface
 * should encapsulate the parsing logic and delegate unit recognition to appropriate
 * dictionary services (e.g., {@code UnitDictionaryService}).</p>
 *
 * <p>Example input: {@code "1,5 tablespoons sugar"} → name: "sugar", quantity: 1.5, unit: TABLESPOONS</p>
 *
 * <p>Implementations must be safe for use in multi-threaded environments.</p>
 *
 * @see edu.agh.recipe.recipes.model.RecipeIngredient
 * @see edu.agh.recipe.units.domain.MeasurementUnit
 */
public interface IngredientParserService {

    /**
     * Parses a single line describing an ingredient into a DTO.
     * Returns an empty Optional if the line could not be parsed or if the result is invalid.
     *
     * @param line raw text line describing an ingredient (e.g., "1 1/2 szklanki mąki")
     * @return optional RecipeIngredientDTO representing the parsed ingredient, or empty if parsing failed
     */
    Optional<RecipeIngredientDTO> parse(String line);
}