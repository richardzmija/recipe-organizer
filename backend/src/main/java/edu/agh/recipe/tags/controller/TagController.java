package edu.agh.recipe.tags.controller;

import edu.agh.recipe.tags.dto.CreateTagDTO;
import edu.agh.recipe.tags.dto.TagDTO;
import edu.agh.recipe.tags.service.TagService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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
@RequestMapping("/tags")
@Tag(name = "Tag", description = "Tag management APIs")
public class TagController {

    private final TagService tagService;

    public TagController(TagService tagService) {
        this.tagService = Objects.requireNonNull(tagService);
    }

    @Operation(summary = "Get all tags.", description = "Returns a paginated list of all tags.")
    @ApiResponse(
            responseCode = "200",
            description = "Successfully retrieved tags",
            content = @Content(schema = @Schema(implementation = Page.class))
    )
    @GetMapping
    public ResponseEntity<Page<TagDTO>> getAllTags(
            @Parameter(description = "Page number (zero-based)")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Number of items per page")
            @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "Sort field")
            @RequestParam(defaultValue = "name") String sort,
            @Parameter(description = "Sort direction (asc/desc)")
            @RequestParam(defaultValue = "asc") String direction
    ) {
        Sort.Direction sortDirection = Sort.Direction.fromString(direction);
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sort));
        return ResponseEntity.ok(tagService.getAllTags(pageable));
    }

    @Operation(summary = "Get tag by ID.", description = "Returns a tag by its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tag found",
                    content = @Content(schema = @Schema(implementation = TagDTO.class))),
            @ApiResponse(responseCode = "404", description = "Tag not found", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<TagDTO> getTagById(@PathVariable String id) {
        return ResponseEntity.ok(tagService.getTagById(id));
    }

    @Operation(summary = "Get tag by name.", description = "Returns a tag by its name.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tag found",
                    content = @Content(schema = @Schema(implementation = TagDTO.class))),
            @ApiResponse(responseCode = "404", description = "Tag not found", content = @Content)
    })
    @GetMapping("/by-name")
    public ResponseEntity<TagDTO> getTagByName(@RequestParam String name) {
        return ResponseEntity.ok(tagService.getTagByName(name));
    }

    @Operation(summary = "Create tag.", description = "Creates a new tag.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Tag created",
                    content = @Content(schema = @Schema(implementation = TagDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input", content = @Content),
            @ApiResponse(responseCode = "409", description = "Tag with this name already exists", content = @Content)
    })
    @PostMapping
    public ResponseEntity<TagDTO> createTag(@Valid @RequestBody CreateTagDTO createTagDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(tagService.createTag(createTagDTO));
    }

    @Operation(summary = "Update tag.", description = "Updates an existing tag.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tag updated",
                    content = @Content(schema = @Schema(implementation = TagDTO.class))),
            @ApiResponse(responseCode = "404", description = "Tag not found", content = @Content),
            @ApiResponse(responseCode = "409", description = "Tag with new name already exists", content = @Content)
    })
    @PutMapping("/{id}")
    public ResponseEntity<TagDTO> updateTag(
            @PathVariable String id,
            @Valid @RequestBody CreateTagDTO updateTagDTO) {
        return ResponseEntity.ok(tagService.updateTag(id, updateTagDTO));
    }

    @Operation(summary = "Delete tag.", description = "Deletes a tag by ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Tag deleted", content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTag(@PathVariable String id) {
        tagService.deleteTag(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Search tags.", description = "Searches tags by name.")
    @ApiResponse(
            responseCode = "200",
            description = "Successfully retrieved matching tags",
            content = @Content(schema = @Schema(implementation = Page.class))
    )
    @GetMapping("/search")
    public ResponseEntity<Page<TagDTO>> searchTags(
            @RequestParam String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(tagService.findTagsByNameContaining(query, pageable));
    }

    @Operation(summary = "Get tags by category.", description = "Returns tags in a specific category.")
    @ApiResponse(
            responseCode = "200",
            description = "Successfully retrieved category tags",
            content = @Content(schema = @Schema(implementation = Page.class))
    )
    @GetMapping("/by-category")
    public ResponseEntity<Page<TagDTO>> getTagsByCategory(
            @RequestParam String category,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(tagService.getTagsByCategory(category, pageable));
    }

    @Operation(summary = "Get popular tags.", description = "Returns the most frequently used tags.")
    @ApiResponse(
            responseCode = "200",
            description = "Successfully retrieved popular tags",
            content = @Content(schema = @Schema(implementation = List.class))
    )
    @GetMapping("/popular")
    public ResponseEntity<List<TagDTO>> getPopularTags(
            @RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(tagService.getPopularTags(limit));
    }
}