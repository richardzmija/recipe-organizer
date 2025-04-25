package edu.agh.recipe.recipes.controller;

import edu.agh.recipe.recipes.dto.CreateRecipeDTO;
import edu.agh.recipe.recipes.dto.RecipeDTO;
import edu.agh.recipe.recipes.dto.UpdateRecipeDTO;
import edu.agh.recipe.recipes.service.RecipeService;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

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
}
