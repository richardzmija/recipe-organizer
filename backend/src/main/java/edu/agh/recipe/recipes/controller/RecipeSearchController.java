package edu.agh.recipe.recipes.controller;

import edu.agh.recipe.recipes.dto.RecipeDTO;
import edu.agh.recipe.recipes.service.RecipeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/recipes")
@Tag(name = "Recipe Searching", description = "Recipe searching APIs")
public class RecipeSearchController {

    private final RecipeService recipeService;

    public RecipeSearchController(RecipeService recipeService) {
        this.recipeService = Objects.requireNonNull(recipeService);
    }

    @Operation(
        summary = "Advanced recipe search",
        description = "Search for recipes with combined filters for name, ingredients, and tags"
    )
    @ApiResponse(
        responseCode = "200",
        description = "Successfully retrieved filtered recipes"
    )
    @GetMapping("/search/advanced")
    public ResponseEntity<Page<RecipeDTO>> advancedSearch(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) List<String> ingredients,
            @RequestParam(required = false) List<String> tagsID,
            @RequestParam(required = false, defaultValue = "name") String sort_field,
            @RequestParam(required = false, defaultValue = "asc") String direction,
            @RequestParam(required = false, defaultValue = "0") int page_number,
            @RequestParam(required = false, defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page_number, size);

        Page<RecipeDTO> results = recipeService.advancedSearch(
                name, ingredients, tagsID, sort_field, direction, pageable);

        return ResponseEntity.ok(results);
    }
}
