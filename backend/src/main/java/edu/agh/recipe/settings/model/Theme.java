package edu.agh.recipe.settings.model;

public enum Theme {
    LIGHT,
    DARK;

    public static Theme fromString(String value) {
        try {
            return value != null ? valueOf(value.toUpperCase()) : null;
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}