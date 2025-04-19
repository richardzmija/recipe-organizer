package edu.agh.recipe.export.controller;

import edu.agh.recipe.export.exception.RecipeExportException;
import edu.agh.recipe.export.service.RecipeMarkdownExportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

@RestController
@RequestMapping("/recipes/export")
@Tag(name = "Recipe Export", description = "Recipe export APIs")
public class RecipeExportController {

    private static final Logger logger = LoggerFactory.getLogger(RecipeExportController.class);

    private final RecipeMarkdownExportService recipeMarkdownExportService;

    public RecipeExportController(RecipeMarkdownExportService recipeMarkdownExportService) {
        this.recipeMarkdownExportService = Objects.requireNonNull(recipeMarkdownExportService);
    }

    @Operation(
        summary = "Export all recipes as Markdown ZIP.",
        description = "Exports all available recipes to a ZIP archive containing Markdown files."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully exported all recipes",
                content = @Content(schema = @Schema(implementation = Resource.class))),
        @ApiResponse(responseCode = "500", description = "Error exporting recipes", content = @Content)
    })
    @GetMapping("/markdown/zip")
    public ResponseEntity<Resource> exportAllRecipesAsMarkdownZip() {
        logger.info("Exporting all recipes as markdown ZIP...");
        Resource zipResource = recipeMarkdownExportService.exportRecipesToMarkdownZip();
        logger.info("Successfully exported all recipes to ZIP.");

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"recipes.zip\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(zipResource);
    }

    @Operation(
        summary = "Export a single recipe as Markdown ZIP.",
        description = "Exports a specific recipe identified by its ID to a ZIP archive containing a Markdown file."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully exported recipe",
                content = @Content(schema = @Schema(implementation = Resource.class))),
        @ApiResponse(responseCode = "404", description = "Recipe not found", content = @Content),
        @ApiResponse(responseCode = "500", description = "Error exporting recipe", content = @Content)
    })
    @GetMapping("/{id}/markdown/zip")
    public ResponseEntity<Resource> exportRecipeAsMarkdownZip(
        @Parameter(description = "Recipe identifier", required = true, in = ParameterIn.PATH)
        @PathVariable String id
    ) {
        logger.info("Exporting recipe with ID: {} as markdown ZIP...", id);
        Resource zipResource = recipeMarkdownExportService.exportRecipeToMarkdownZip(id);
        logger.info("Successfully exported recipe ID: {} to ZIP", id);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"recipe.zip\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(zipResource);
    }

    @ExceptionHandler(RecipeExportException.class)
    public ResponseEntity<String> handleRecipeExportException(RecipeExportException ex) {
        logger.error("Recipe export failed: {}", ex.getMessage(), ex);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .contentType(MediaType.TEXT_PLAIN)
                .body("Failed to export recipe(s): " + ex.getMessage());
    }
}