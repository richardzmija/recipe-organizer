package edu.agh.recipe.importing.service;

import edu.agh.recipe.importing.dto.ImportJobStatus;
import edu.agh.recipe.importing.dto.RecipeImportPreviewDTO;
import edu.agh.recipe.importing.dto.RecipeImportRequest;
import edu.agh.recipe.recipes.model.Recipe;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.*;

@Service
public class ImportJobService {

    private final RecipeImportService recipeImportService;
    private final Map<String, ImportJobStatus> jobs = new ConcurrentHashMap<>();

    public ImportJobService(RecipeImportService recipeImportService) {
        this.recipeImportService = recipeImportService;
    }

    @Async
    public void startImportJobAsync(RecipeImportRequest request, String jobId) {
        ImportJobStatus jobStatus = jobs.get(jobId);
        try {
            Recipe recipe = recipeImportService.importRecipe(request.url());
            jobStatus.setStatus("COMPLETED");
            jobStatus.setResult(RecipeImportPreviewDTO.fromEntity(recipe));
        } catch (IllegalArgumentException e) {
            jobStatus.setStatus("FAILED");
            jobStatus.setErrorMessage("Unsupported source URL: " + e.getMessage());
        } catch (Exception e) {
            jobStatus.setStatus("FAILED");
            jobStatus.setErrorMessage("Import failed: " + e.getMessage());
        }
    }

    public String startImportJob(RecipeImportRequest request) {
        String jobId = UUID.randomUUID().toString();
        ImportJobStatus jobStatus = new ImportJobStatus(jobId, "IN_PROGRESS");
        jobs.put(jobId, jobStatus);

        startImportJobAsync(request, jobId);

        return jobId;
    }

    public Optional<ImportJobStatus> getJobStatus(String jobId) {
        return Optional.ofNullable(jobs.get(jobId));
    }
}
