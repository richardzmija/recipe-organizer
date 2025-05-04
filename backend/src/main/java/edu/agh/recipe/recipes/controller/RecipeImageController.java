package edu.agh.recipe.recipes.controller;

import edu.agh.recipe.recipes.dto.RecipeDTO;
import edu.agh.recipe.recipes.service.RecipeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Objects;

@RestController
@RequestMapping("/recipes")
@Tag(name = "Recipe Images", description = "Recipe images management APIs")
public class RecipeImageController {

    private final RecipeService recipeService;

    public RecipeImageController(RecipeService recipeService) {
        this.recipeService = Objects.requireNonNull(recipeService);
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
            @ApiResponse(responseCode = "200", description = "Image uploaded successfully",
                    content = @Content(schema = @Schema(implementation = RecipeDTO.class))),
            @ApiResponse(responseCode = "404", description = "Recipe not found", content = @Content),
            @ApiResponse(responseCode = "400", description = "Invalid image", content = @Content)
    })
    @PatchMapping("/{id}/image")
    public ResponseEntity<RecipeDTO> uploadRecipeImage(
            @Parameter(description = "Recipe identifier", required = true, in = ParameterIn.PATH)
            @PathVariable String id,
            @Parameter(description = "Image description")
            @RequestParam("description") String description,
            @RequestParam("image") MultipartFile image
    ) {
        return ResponseEntity.ok(recipeService.uploadImageForRecipe(id, description, image));
    }

    @Operation(
            summary = "Remove image from recipe.",
            description = "Removes specified image from a recipe."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Image removed successfully",
                    content = @Content(schema = @Schema(implementation = RecipeDTO.class))),
            @ApiResponse(responseCode = "404", description = "Recipe not found", content = @Content)
    })
    @DeleteMapping("/{id}/image/{imageId}")
    public ResponseEntity<RecipeDTO> removeImageFromRecipe(
            @Parameter(description = "Recipe identifier", required = true)
            @PathVariable String id,
            @Parameter(description = "Image identifier", required = true)
            @PathVariable String imageId
    ) {
        return ResponseEntity.ok(recipeService.removeImageFromRecipe(id, imageId));
    }

    @Operation(
            summary = "Set recipe primary image.",
            description = "Changes thumbnail for provided recipe to provided image id."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Thumbnail changed successfully",
                    content = @Content(schema = @Schema(implementation = RecipeDTO.class))),
            @ApiResponse(responseCode = "404", description = "Recipe not found", content = @Content),
    })
    @PatchMapping("/{id}/image/{imageId}/primary")
    public ResponseEntity<RecipeDTO> setImageAsPrimary(
            @Parameter(description = "Recipe identifier", required = true)
            @PathVariable String id,
            @Parameter(description = "Image identifier", required = true)
            @PathVariable String imageId
    ) {
        return ResponseEntity.ok(recipeService.setImageAsPrimary(id, imageId));
    }
}
