package edu.agh.recipe.importing.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Request for importing a recipe from external URL")
public record RecipeImportRequest(

    @Schema(description = "URL to the recipe page (only mojewypieki.com supported)", example = "https://www.mojewypieki.com/przepis/sernik-nowojorski")
    @NotBlank
    @Size(min = 3, max = 2048) // 2048 is the URL length limit for Google Chrome.
    String url

)
{}

