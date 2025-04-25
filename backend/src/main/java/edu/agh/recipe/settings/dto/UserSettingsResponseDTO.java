package edu.agh.recipe.settings.dto;

import edu.agh.recipe.settings.model.Theme;
import edu.agh.recipe.settings.model.UserSettings;
import edu.agh.recipe.units.domain.MeasurementSystem;

import java.util.HashMap;
import java.util.Map;

public record UserSettingsResponseDTO(
    Theme theme,
    MeasurementSystem unitSystem,
    Map<String, Object> additionalSettings
) {
    public UserSettingsResponseDTO {
        if (additionalSettings == null) {
            additionalSettings = new HashMap<>();
        }
    }

    public static UserSettingsResponseDTO fromEntity(UserSettings settings) {
        return new UserSettingsResponseDTO(
            settings.getTheme(),
            settings.getUnitSystem(),
            settings.getAdditionalPreferences()
        );
    }
}