package edu.agh.recipe.tags.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record TagDTO(
    String id,

    @NotBlank(message = "Tag name is required.")
    @Size(max = 50, message = "Tag name must not exceed 50 characters.")
    String name,

    String color,

    @Size(max = 200, message = "Description must not exceed 200 characters.")
    String description,

    String category,

    int usageCount
) {}