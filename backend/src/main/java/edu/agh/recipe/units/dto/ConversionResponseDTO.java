package edu.agh.recipe.units.dto;

public record ConversionResponseDTO(
    double originalValue,
    String originalUnit,
    String originalUnitName,
    double convertedValue,
    String convertedUnit,
    String convertedUnitName,
    String formattedOriginalValue,
    String formattedConvertedValue
) {}
