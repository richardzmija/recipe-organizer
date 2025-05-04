package edu.agh.recipe.images.controller;

import edu.agh.recipe.images.service.ImageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

@RestController
@RequestMapping("/images")
@Tag(name = "Image", description = "Image management APIs")
public class ImageController {

    private final ImageService imageService;

    public ImageController(ImageService imageService) {
        this.imageService = Objects.requireNonNull(imageService);
    }

    @Operation(summary = "Image by its ID.", description = "Returns the image if present, else 404.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Image found",
                    content = @Content(schema = @Schema(type = "string", format = "binary"))),
            @ApiResponse(responseCode = "404", description = "Image not found", content = @Content)
    })
    @GetMapping("/{id}/image")
    public ResponseEntity<InputStreamResource> getRecipeImageById(
            @Parameter(description = "Image identifier", required = true, in = ParameterIn.PATH)
            @PathVariable String id
    ) {
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, imageService.getImageDataById(id).contentType())
                .body(imageService.getImage(id));
    }
}
