package edu.agh.recipe.images.dto;

import java.util.Date;

public record ImageDTO(
        String id,
        String filename,
        String contentType,
        String description,
        boolean isPrimary,
        Date uploadDate
) {}
