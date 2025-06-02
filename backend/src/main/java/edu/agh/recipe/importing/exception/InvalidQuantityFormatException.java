package edu.agh.recipe.importing.exception;

/**
 * Exception thrown when the quantity string in an ingredient line
 * cannot be parsed into a valid number.
 */
public class InvalidQuantityFormatException extends RuntimeException {

    public InvalidQuantityFormatException(String input) {
        super("Could not parse quantity from input: '" + input + "'");
    }

    public InvalidQuantityFormatException(String input, Throwable cause) {
        super("Could not parse quantity from input: '" + input + "'", cause);
    }
}
