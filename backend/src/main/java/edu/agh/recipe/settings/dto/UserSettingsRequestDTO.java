package edu.agh.recipe.settings.dto;

import edu.agh.recipe.settings.model.Theme;
import edu.agh.recipe.settings.model.UserSettings;
import edu.agh.recipe.units.domain.MeasurementSystem;
import jakarta.validation.constraints.NotNull;

import java.util.HashMap;
import java.util.Map;

public record UserSettingsRequestDTO(
    @NotNull(message = "Theme is required")
    Theme theme,

    @NotNull(message = "Unit system is required")
    MeasurementSystem unitSystem,

    Map<String, Object> additionalSettings
) {
    public UserSettingsRequestDTO {
        if (additionalSettings == null) {
            additionalSettings = new HashMap<>();
        }
    }

    public UserSettings toEntity(String id) {
        UserSettings settings = new UserSettings();
        settings.setTheme(theme);
        settings.setUnitSystem(unitSystem);
        settings.setAdditionalPreferences(additionalSettings);
        return settings;
    }
}