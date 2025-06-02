package edu.agh.recipe.importing.dto;

import edu.agh.recipe.recipes.model.Recipe;

public class ImportJobStatus {
    private String jobId;
    private String status; // IN_PROGRESS, COMPLETED, FAILED
    private RecipeImportPreviewDTO result;
    private String errorMessage;

    public ImportJobStatus(String jobId, String status) {
        this.jobId = jobId;
        this.status = status;
    }

    public String getJobId() {
        return jobId;
    }

    public String getStatus() {
        return status;
    }

    public RecipeImportPreviewDTO getResult() {
        return result;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setResult(RecipeImportPreviewDTO result) {
        this.result = result;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}