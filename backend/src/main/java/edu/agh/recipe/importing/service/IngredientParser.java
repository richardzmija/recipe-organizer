package edu.agh.recipe.importing.service;

import edu.agh.recipe.importing.exception.InvalidQuantityFormatException;
import edu.agh.recipe.recipes.dto.RecipeIngredientDTO;
import edu.agh.recipe.units.dictionary.UnitDictionaryService;
import edu.agh.recipe.units.domain.MeasurementUnit;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Parses ingredient lines using a regex-based strategy, extracting:
 * - quantity (e.g., "1", "1.5", "1/2", "3/4")
 * - unit (e.g., "g", "ml", "teaspoon")
 * - ingredient name ("sugar", "flour", etc.)
 * Limitations:
 * - Mixed fractions like "1 1/2 glass sugar" are partially matched:
 *   - "1" becomes the quantity
 *   - "1/2" becomes the unit
 *   - "glass sugar" becomes the ingredient name
 */
@Component
public class IngredientParser implements IngredientParserService {
    private static final Logger log = LoggerFactory.getLogger(IngredientParser.class);
    private static final Pattern PATTERN = Pattern.compile(
            "(?<qty>[\\d,./]+)\\s*(?<unit>\\p{L}+)?\\s+(?<name>.+)", Pattern.UNICODE_CHARACTER_CLASS
    );

    private final UnitDictionaryService dictionaryService;

    public IngredientParser(UnitDictionaryService dictionaryService) {
        this.dictionaryService = dictionaryService;
    }

    public Optional<RecipeIngredientDTO> parse(String line) {

        Matcher matcher = PATTERN.matcher(line.trim());

        if (matcher.matches()) {
            String quantityStr = Optional.ofNullable(matcher.group("qty"))
                    .map(q -> q.replace(',', '.').trim())
                    .orElse(null);
            double quantity;
            try {
                quantity = quantityStr != null ? parseQuantity(quantityStr) : 0.0;
            } catch (InvalidQuantityFormatException e) {
                log.warn("Failed to parse quantity in ingredient line: '{}'. Error: {}", line, e.getMessage());
                return Optional.empty();
            }

            if (quantity <= 0.0) {
                log.warn("Quantity must be positive. Line: '{}'", line);
                return Optional.empty();
            }

            String unitStr = Optional.ofNullable(matcher.group("unit"))
                    .map(String::trim)
                    .orElse(null);
            MeasurementUnit unit = unitStr != null
                    ? dictionaryService.resolveUnit(unitStr.toLowerCase()).orElse(null)
                    : null;

            String ingredientName = matcher.group("name");
            if (unit == null && unitStr != null) {
                ingredientName = unitStr + " " + ingredientName;
            }

            if (ingredientName == null || ingredientName.isBlank()) {
                log.warn("Missing ingredient name after parsing: '{}'", line);
                return Optional.empty();
            }

            return Optional.of(new RecipeIngredientDTO(ingredientName, unit, quantity));
        }

        log.warn("Ingredient line did not match pattern: '{}'", line);
        return Optional.of(new RecipeIngredientDTO(line.trim(), null, 1.0));
    }

    /**
     * Parses a quantity string which can be a decimal number, integer,
     * common fraction (e.g. "3/4"), or a mixed number (e.g. "1 1/2").
     *
     * @param input the input string representing a quantity
     * @return parsed numeric value as a double
     * @throws InvalidQuantityFormatException if the format is unsupported or invalid
     */
    private double parseQuantity(String input) {
        try {
            if (input.contains("/")) {
                String[] parts = input.split(" ");
                if (parts.length == 2) {
                    // mixed number (e.g. "1 1/2")
                    double whole = Double.parseDouble(parts[0]);
                    String[] fraction = parts[1].split("/");
                    return whole + (Double.parseDouble(fraction[0]) / Double.parseDouble(fraction[1]));
                } else if (parts.length == 1) {
                    // common fraction (e.g. "3/4")
                    String[] fraction = parts[0].split("/");
                    return Double.parseDouble(fraction[0]) / Double.parseDouble(fraction[1]);
                }
            }

            // plain decimal or integer (e.g. "1.5" or "2")
            return Double.parseDouble(input);

        } catch (NumberFormatException | ArithmeticException | ArrayIndexOutOfBoundsException e) {
            throw new InvalidQuantityFormatException(input, e);
        }
    }


}
