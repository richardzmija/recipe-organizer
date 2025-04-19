package edu.agh.recipe.model.dto;

public record UnitDTO(String name, String system) {
    public static UnitDTO of(String name, String system) {
        return new UnitDTO(name, system);
    }
}
