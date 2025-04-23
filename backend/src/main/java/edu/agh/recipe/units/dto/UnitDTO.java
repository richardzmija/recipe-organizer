package edu.agh.recipe.units.dto;

import edu.agh.recipe.units.domain.MeasurementUnit;

public record UnitDTO(String id, String symbol, String name,  String system) {
    public static UnitDTO of(MeasurementUnit unit) {
        return new UnitDTO(unit.name(), unit.getSymbol(), unit.getName(), unit.getSystem().name());
    }
}
