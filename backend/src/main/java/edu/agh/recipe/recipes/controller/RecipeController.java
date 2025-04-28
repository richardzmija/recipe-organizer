package edu.agh.recipe.recipes.controller;

import edu.agh.recipe.recipes.dto.CreateRecipeDTO;
import edu.agh.recipe.recipes.dto.RecipeDTO;
import edu.agh.recipe.recipes.dto.UpdateRecipeDTO;
import edu.agh.recipe.recipes.service.RecipeService;
import edu.agh.recipe.tags.dto.TagDTO;
import edu.agh.recipe.tags.dto.TagReferenceDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Objects;
import java.util.Set;

@RestController
@RequestMapping("/recipes")
@Tag(name = "Recipe", description = "Recipe management APIs")
public class RecipeController {

    private final RecipeService recipeService;

    public RecipeController(RecipeService recipeService) {
        this.recipeService = Objects.requireNonNull(recipeService);
    }

    @Operation(
            summary = "Get all recipes.",
            description = "Returns a paginated list of available recipes."
    )
    @ApiResponse(
            responseCode = "200",
            description = "Successfully retrieved recipes",
            content = @Content(schema = @Schema(implementation = Page.class))
    )
    @GetMapping
    public ResponseEntity<Page<RecipeDTO>> getAllRecipes(
            @Parameter(description = "Page number (zero-based)")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Number of items per page")
            @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Sort field")
            @RequestParam(defaultValue = "name") String sort,
            @Parameter(description = "Sort direction (asc/desc)")
            @RequestParam(defaultValue = "asc") String direction
    ) {
        Sort.Direction sortDirection = Sort.Direction.fromString(direction);
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sort));
        return ResponseEntity.ok(recipeService.getAllRecipes(pageable));
    }

    @Operation(summary = "Get recipe by ID.", description = "Returns a recipe by its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Recipe found",
                    content = @Content(schema = @Schema(implementation = RecipeDTO.class))),
            @ApiResponse(responseCode = "404", description = "Recipe not found", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<RecipeDTO> getRecipeById(
            @Parameter(description = "Recipe identifier", required = true, in = ParameterIn.PATH)
            @PathVariable String id
    ) {
        return ResponseEntity.ok(recipeService.getRecipeById(id));
    }

    @Operation(
            summary = "Filter recipes by any ingredient.",
            description = "Returns a paginated list of recipes that contain *any* of the specified ingredients."
    )
    @ApiResponse(
            responseCode = "200",
            description = "Successfully retrieved filtered recipes",
            content = @Content(schema = @Schema(implementation = Page.class))
    )
    @GetMapping("/filter/any")
    public ResponseEntity<Page<RecipeDTO>> getRecipesByAnyIngredient(
            @Parameter(description = "List of ingredient names to match any", required = true)
            @RequestParam List<String> ingredients,
            @Parameter(description = "Page number (zero-based)")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Number of items per page")
            @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Sort field")
            @RequestParam(defaultValue = "name") String sort,
            @Parameter(description = "Sort direction (asc/desc)")
            @RequestParam(defaultValue = "asc") String direction
    ) {
        Sort.Direction sortDirection = Sort.Direction.fromString(direction);
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sort));
        return ResponseEntity.ok(recipeService.findRecipesContainingAnyIngredients(ingredients, pageable));
    }

    @Operation(
            summary = "Filter recipes by all ingredients.",
            description = "Returns a paginated list of recipes that contain *all* of the specified ingredients."
    )
    @ApiResponse(
            responseCode = "200",
            description = "Successfully retrieved filtered recipes",
            content = @Content(schema = @Schema(implementation = Page.class))
    )
    @GetMapping("/filter/all")
    public ResponseEntity<Page<RecipeDTO>> getRecipesByAllIngredients(
            @Parameter(description = "List of ingredient names to match all", required = true)
            @RequestParam List<String> ingredients,
            @Parameter(description = "Page number (zero-based)")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Number of items per page")
            @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Sort field")
            @RequestParam(defaultValue = "name") String sort,
            @Parameter(description = "Sort direction (asc/desc)")
            @RequestParam(defaultValue = "asc") String direction
    ) {
        Sort.Direction sortDirection = Sort.Direction.fromString(direction);
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sort));
        return ResponseEntity.ok(recipeService.findRecipesContainingAllIngredients(ingredients, pageable));
    }

    @Operation(
            summary = "Get recipe name suggestions.",
            description = "Returns a limited list of recipes whose names start with the query, for search suggestions."
    )
    @ApiResponse(
            responseCode = "200",
            description = "Successfully retrieved suggestions",
            content = @Content(schema = @Schema(implementation = Page.class))
    )
    @GetMapping("/suggestions")
    public ResponseEntity<Page<RecipeDTO>> getRecipeSuggestions(
            @Parameter(description = "Partial recipe name to search for", required = true)
            @RequestParam String query,
            @Parameter(description = "Maximum number of suggestions to return")
            @RequestParam(defaultValue = "5") int limit
    ) {
        // Enforce a limit between 1 and 20.
        int effectiveLimit = Math.max(1, Math.min(limit, 20));
        Pageable pageable = PageRequest.of(0, effectiveLimit, Sort.by(Sort.Direction.ASC, "name"));
        Page<RecipeDTO> suggestions = recipeService.suggestRecipesByName(query, pageable);
        return ResponseEntity.ok(suggestions);
    }

    @Operation(
            summary = "Create recipe.",
            description = "Creates a new recipe and returns the created entity.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Recipe details for creation",
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = CreateRecipeDTO.class)
                    )
            )
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Recipe created successfully",
                    content = @Content(schema = @Schema(implementation = RecipeDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input", content = @Content)
    })
    @PostMapping
    public ResponseEntity<RecipeDTO> createRecipe(
            @Valid @RequestBody CreateRecipeDTO recipeDTO
    ) {
        RecipeDTO createdRecipe = recipeService.createRecipe(recipeDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdRecipe);
    }

    @Operation(
            summary = "Update recipe.",
            description = "Updates an existing recipe by ID.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Updated recipe details",
                    required = true,
                    content = @Content(schema = @Schema(implementation = UpdateRecipeDTO.class))
            )
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Recipe updated successfully",
                    content = @Content(schema = @Schema(implementation = RecipeDTO.class))),
            @ApiResponse(responseCode = "404", description = "Recipe not found", content = @Content),
            @ApiResponse(responseCode = "400", description = "Invalid input", content = @Content)
    })
    @PutMapping("/{id}")
    public ResponseEntity<RecipeDTO> updateRecipe(
            @Parameter(description = "Recipe identifier", required = true, in = ParameterIn.PATH)
            @PathVariable String id,
            @Valid @RequestBody UpdateRecipeDTO recipeDTO
            ) {
        RecipeDTO updatedRecipe = recipeService.updateRecipe(id, recipeDTO);
        return ResponseEntity.ok(updatedRecipe);
    }

    @Operation(summary = "Delete recipe.", description = "Deletes a recipe by ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Recipe deleted successfully", content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRecipe(
            @Parameter(description = "Recipe identifier", required = true, in = ParameterIn.PATH)
            @PathVariable String id
    ) {
        recipeService.deleteRecipe(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Delete multiple recipes.",
            description = "Deletes multiple recipes by IDs atomically - all succeed or all fail.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Recipes deleted successfully", content = @Content),
            @ApiResponse(responseCode = "404", description = "One or more recipes not found", content = @Content)
    })
    @DeleteMapping("/bulk")
    public ResponseEntity<Void> bulkDeleteRecipes(
            @Parameter(description = "Recipe identifiers", required = true)
            @RequestBody List<String> ids
    ) {
        recipeService.bulkDeleteRecipes(ids);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Filter recipes by tag ID.",
            description = "Returns recipes that have the specified tag."
    )
    @ApiResponse(
            responseCode = "200",
            description = "Successfully retrieved filtered recipes",
            content = @Content(schema = @Schema(implementation = Page.class))
    )
    @GetMapping("/filter/tag/by-id")
    public ResponseEntity<Page<RecipeDTO>> getRecipesByTagId(
            @Parameter(description = "Tag identifier")
            @RequestParam String tagId,
            @Parameter(description = "Page number (zero-based)")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Number of items per page")
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(recipeService.findRecipesByTag(tagId, pageable));
    }

    @Operation(
            summary = "Filter recipes by tag name.",
            description = "Returns recipes that have a tag with the specified name."
    )
    @GetMapping("/filter/tag/by-name")
    public ResponseEntity<Page<RecipeDTO>> getRecipesByTagName(
            @Parameter(description = "Tag name")
            @RequestParam String tagName,
            @Parameter(description = "Page number (zero-based)")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Number of items per page")
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(recipeService.findRecipesByTagName(tagName, pageable));
    }

    @Operation(
            summary = "Filter recipes by any tags.",
            description = "Returns recipes that have any of the specified tags."
    )
    @ApiResponse(
            responseCode = "200",
            description = "Successfully retrieved filtered recipes",
            content = @Content(schema = @Schema(implementation = Page.class))
    )
    @GetMapping("/filter/tags/any")
    public ResponseEntity<Page<RecipeDTO>> getRecipesByAnyTagIds(
            @Parameter(description = "Tag IDs")
            @RequestParam List<String> tagIds,
            @Parameter(description = "Page number (zero-based)")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Number of items per page")
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(recipeService.findRecipesByAnyTags(tagIds, pageable));
    }

    @Operation(
            summary = "Filter recipes by all tags.",
            description = "Returns recipes that have all of the specified tags."
    )
    @ApiResponse(
            responseCode = "200",
            description = "Successfully retrieved filtered recipes",
            content = @Content(schema = @Schema(implementation = Page.class))
    )
    @GetMapping("/filter/tags/all")
    public ResponseEntity<Page<RecipeDTO>> getRecipesByAllTagIds(
            @Parameter(description = "Tag IDs")
            @RequestParam List<String> tagIds,
            @Parameter(description = "Page number (zero-based)")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Number of items per page")
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(recipeService.findRecipesByAllTags(tagIds, pageable));
    }

    @Operation(
            summary = "Add tags to recipe.",
            description = "Adds specified tags to a recipe."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tags added successfully",
                    content = @Content(schema = @Schema(implementation = RecipeDTO.class))),
            @ApiResponse(responseCode = "404", description = "Recipe not found", content = @Content)
    })
    @PatchMapping("/{id}/tags")
    public ResponseEntity<RecipeDTO> addTagsToRecipe(
            @Parameter(description = "Recipe identifier", required = true)
            @PathVariable String id,
            @RequestBody Set<TagReferenceDTO> tagReferences
    ) {
        return ResponseEntity.ok(recipeService.addTagsToRecipe(id, tagReferences));
    }

    @Operation(
            summary = "Remove tags from recipe.",
            description = "Removes specified tags from a recipe."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tags removed successfully",
                    content = @Content(schema = @Schema(implementation = RecipeDTO.class))),
            @ApiResponse(responseCode = "404", description = "Recipe not found", content = @Content)
    })
    @DeleteMapping("/{id}/tags")
    public ResponseEntity<RecipeDTO> removeTagsFromRecipe(
            @Parameter(description = "Recipe identifier", required = true)
            @PathVariable String id,
            @RequestBody Set<TagReferenceDTO> tagReferences
    ) {
        return ResponseEntity.ok(recipeService.removeTagsFromRecipe(id, tagReferences));
    }

    @Operation(
            summary = "Get tag suggestions for recipe",
            description = "Returns tag suggestions for a specific recipe"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved tag suggestions",
                    content = @Content(schema = @Schema(implementation = List.class)))
    })
    @GetMapping("/{id}/tag-suggestions")
    public ResponseEntity<List<TagDTO>> getTagSuggestions(
            @Parameter(description = "Recipe identifier", required = true, in = ParameterIn.PATH)
            @PathVariable String id,
            @Parameter(description = "Maximum number of suggestions")
            @RequestParam(defaultValue = "5") int limit
    ) {
        return ResponseEntity.ok(recipeService.suggestTagsForRecipe(id, limit));
    }

    @Operation(summary = "Get recipe image by recipe ID.", description = "Returns the image attached to recipe if present, else 404.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Recipe image found",
                    content = @Content(mediaType = "image/png", schema = @Schema(type = "string", format = "binary"))),
            @ApiResponse(responseCode = "404", description = "Recipe image not found", content = @Content)
    })
    @GetMapping("/{id}/image")
    public ResponseEntity<byte[]> getRecipeImageById(
            @Parameter(description = "Recipe identifier", required = true, in = ParameterIn.PATH)
            @PathVariable String id
    ) {
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, MediaType.IMAGE_PNG_VALUE)
                .body(recipeService.getRecipeImageById(id));
    }

    @Operation(
            summary = "Upload recipe image.",
            description = "Uploads an image for an existing recipe with ID.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Recipe image to upload",
                    required = true,
                    content = @Content(
                            mediaType = "multipart/form-data",
                            schema = @Schema(type = "string", format = "binary")
                    )
            )
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Image uploaded successfully"),
            @ApiResponse(responseCode = "404", description = "Recipe not found", content = @Content),
            @ApiResponse(responseCode = "400", description = "Invalid image", content = @Content)
    })
    @PutMapping("/{id}/image")
    public ResponseEntity<String> uploadRecipeImage(
            @Parameter(description = "Recipe identifier", required = true, in = ParameterIn.PATH)
            @PathVariable String id,
            @RequestParam("image") MultipartFile image
    ) {
        recipeService.uploadRecipeImage(id, image);
        return ResponseEntity.ok("Image uploaded successfully");
    }
}
