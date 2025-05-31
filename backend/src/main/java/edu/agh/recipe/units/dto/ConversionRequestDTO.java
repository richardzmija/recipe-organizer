package edu.agh.recipe.units.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ConversionRequestDTO(
    @NotNull(message = "Value is required.")
    double value,

    @NotBlank(message = "Source unit is required.")
    String fromUnit,

    @NotBlank(message = "Target unit is required.")
    String toUnit,

    String format
) {}
