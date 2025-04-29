package edu.agh.recipe.settings.controller;

import edu.agh.recipe.settings.dto.UserSettingsRequestDTO;
import edu.agh.recipe.settings.dto.UserSettingsResponseDTO;
import edu.agh.recipe.settings.service.UserSettingsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

@RestController
@RequestMapping("/settings")
@Tag(name = "User Settings", description = "User settings management APIs")
public class UserSettingsController {

    private final UserSettingsService settingsService;

    public UserSettingsController(UserSettingsService settingsService) {
        this.settingsService = Objects.requireNonNull(settingsService);
    }

    @Operation(
            summary = "Get user settings.",
            description = "Returns the current user settings including theme, unit system, and additional preferences."
    )
    @ApiResponse(
            responseCode = "200",
            description = "Successfully retrieved user settings",
            content = @Content(schema = @Schema(implementation = UserSettingsResponseDTO.class))
    )
    @GetMapping
    public ResponseEntity<UserSettingsResponseDTO> getSettings() {
        return ResponseEntity.ok(settingsService.getSettings());
    }

    @Operation(
            summary = "Update user settings.",
            description = "Updates the user settings with the provided values.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Settings to update",
                    required = true,
                    content = @Content(schema = @Schema(implementation = UserSettingsRequestDTO.class))
            )
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Settings updated successfully",
                    content = @Content(schema = @Schema(implementation = UserSettingsResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input", content = @Content)
    })
    @PutMapping
    public ResponseEntity<UserSettingsResponseDTO> updateSettings(@Valid @RequestBody UserSettingsRequestDTO settings) {
        return ResponseEntity.ok(settingsService.updateSettings(settings));
    }

    @Operation(
            summary = "Get specific setting by key.",
            description = "Returns the value of a specific setting by its key."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Setting found"),
            @ApiResponse(responseCode = "404", description = "Setting not found", content = @Content)
    })
    @GetMapping("/{key}")
    public ResponseEntity<Object> getSetting(
            @Parameter(description = "Setting key", required = true)
            @PathVariable String key) {
        Object value = settingsService.getSetting(key);
        return value != null ? ResponseEntity.ok(value) : ResponseEntity.notFound().build();
    }
}