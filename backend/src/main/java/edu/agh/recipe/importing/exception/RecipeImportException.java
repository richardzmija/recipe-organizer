package edu.agh.recipe.importing.exception;

// We extend `RuntimeException`,
// because in Spring this is more idiomatic for Services.
public class RecipeImportException extends RuntimeException {

    public RecipeImportException(String message) {
        super(message);
    }

    public RecipeImportException(String message, Throwable cause) {
        super(message, cause);
    }
}
