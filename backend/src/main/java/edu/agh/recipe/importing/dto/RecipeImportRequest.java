package edu.agh.recipe.importing.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Request for importing a recipe from external URL")
public class RecipeImportRequest {

    @Schema(description = "URL to the recipe page (only mojewypieki.com supported)", example = "https://www.mojewypieki.com/przepis/sernik-nowojorski")
    private String url;

    public RecipeImportRequest() {}

    public RecipeImportRequest(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
