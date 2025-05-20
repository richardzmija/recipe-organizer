package edu.agh.recipe.importing.controller;

import edu.agh.recipe.importing.dto.ImportJobStatus;
import edu.agh.recipe.importing.dto.RecipeImportRequest;
import edu.agh.recipe.importing.service.ImportJobService;
import edu.agh.recipe.importing.service.RecipeImportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Objects;

@RestController
@RequestMapping("/recipes/import")
@Tag(name = "Recipe Import", description = "Recipe import APIs")
public class RecipeImportController {

    private final RecipeImportService recipeImportService;
    private final ImportJobService importJobService;

    public RecipeImportController(RecipeImportService recipeImportService, ImportJobService importJobService) {
        this.recipeImportService = Objects.requireNonNull(recipeImportService);
        this.importJobService = Objects.requireNonNull(importJobService);
    }

    @PostMapping("/jobs")
    @Operation(summary = "Start a recipe import job")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Import job started"),
            @ApiResponse(responseCode = "400", description = "Unsupported source")
    })
    public ResponseEntity<?> startImportJob(@RequestBody RecipeImportRequest request) {
        String jobId = importJobService.startImportJob(request);
        return ResponseEntity
                .created(URI.create("/recipes/import/jobs/" + jobId))
                .body(new ImportJobStatus(jobId, "STARTED"));
    }

    @GetMapping("/jobs/{jobId}")
    @Operation(summary = "Get the status or result of an import job")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Job status or result returned"),
            @ApiResponse(responseCode = "404", description = "Job not found")
    })
    public ResponseEntity<?> getImportJobStatus(@PathVariable String jobId) {
        return importJobService.getJobStatus(jobId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
