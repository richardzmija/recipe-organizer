package edu.agh.recipe.tags.dto;

public record TagReferenceDTO(
    String id,        // For existing tags.
    String name       // For new tags to create on-the-fly.
) {
    public boolean isExistingTag() {
        return id != null && !id.isEmpty();
    }
}