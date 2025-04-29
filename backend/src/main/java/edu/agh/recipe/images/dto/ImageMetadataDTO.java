package edu.agh.recipe.images.dto;

import java.util.Date;

public record ImageMetadataDTO (
        String recipeId,
        String description,
        boolean isPrimary,
        Date uploadDate
) {}
