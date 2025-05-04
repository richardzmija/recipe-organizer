package edu.agh.recipe.importing.controller;

import edu.agh.recipe.importing.dto.RecipeImportPreviewDTO;
import edu.agh.recipe.importing.dto.RecipeImportRequest;
import edu.agh.recipe.importing.service.RecipeImportService;
import edu.agh.recipe.recipes.model.Recipe;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/recipes/import")
@Tag(name = "Recipe Import", description = "Recipe import APIs")
public class RecipeImportController {

    private final RecipeImportService recipeImportService;

    public RecipeImportController(RecipeImportService recipeImportService) {
        this.recipeImportService = recipeImportService;
    }

    @PostMapping
    @Operation(summary = "Import a recipe from mojewypieki.com")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Recipe imported"),
            @ApiResponse(responseCode = "400", description = "Unsupported URL"),
            @ApiResponse(responseCode = "500", description = "Internal error")
    })
    public ResponseEntity<?> importRecipe(@RequestBody RecipeImportRequest request) {
        if (!request.getUrl().contains("mojewypieki.com")) {
            return ResponseEntity.badRequest().body("Only mojewypieki.com is supported at this time.");
        }

        try {
            Recipe recipe = recipeImportService.importFromMojewypieki(request.getUrl());
            return ResponseEntity.ok(recipe);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to import recipe: " + e.getMessage());
        }
    }

    @PostMapping("mojewypieki/preview")
    @Operation(summary = "Preview a recipe from mojewypieki.com (no DB save)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Preview generated",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = RecipeImportPreviewDTO.class))),

            @ApiResponse(responseCode = "400", description = "Unsupported URL"),
            @ApiResponse(responseCode = "500", description = "Internal error")
    })
    public ResponseEntity<?> previewRecipe(@RequestParam String url) {
        if (!url.contains("mojewypieki.com")) {
            return ResponseEntity.badRequest().body("Only mojewypieki.com is supported at this time.");
        }

        try {
            RecipeImportPreviewDTO preview = recipeImportService.previewFromMojewypieki(url);
            return ResponseEntity.ok(preview);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to preview recipe: " + e.getMessage());
        }
    }


    @PostMapping("/mojewypieki")
    @Operation(summary = "Import and save a recipe from mojewypieki.com")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Recipe imported and saved",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Recipe.class))),
            @ApiResponse(responseCode = "400", description = "Unsupported URL"),
            @ApiResponse(responseCode = "500", description = "Internal error")
    })
    public ResponseEntity<?> importAndSaveRecipe(@RequestParam String url) {
        if (!url.contains("mojewypieki.com")) {
            return ResponseEntity.badRequest().body("Only mojewypieki.com is supported at this time.");
        }

        try {
            Recipe recipe = recipeImportService.importAndSaveFromMojewypieki(url);
            return ResponseEntity.ok(recipe);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to import and save recipe: " + e.getMessage());
        }
    }



}
