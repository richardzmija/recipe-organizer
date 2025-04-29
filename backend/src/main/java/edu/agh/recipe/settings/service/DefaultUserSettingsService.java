package edu.agh.recipe.settings.service;

import edu.agh.recipe.settings.dto.UserSettingsRequestDTO;
import edu.agh.recipe.settings.dto.UserSettingsResponseDTO;
import edu.agh.recipe.settings.model.UserSettings;
import edu.agh.recipe.settings.repository.UserSettingsRepository;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.Objects;

@Service
@Validated
public class DefaultUserSettingsService implements UserSettingsService {

    private static final String SETTINGS_ID = "settings";

    private final UserSettingsRepository repository;

    public DefaultUserSettingsService(UserSettingsRepository repository) {
        this.repository = Objects.requireNonNull(repository);
    }

    @Override
    public UserSettingsResponseDTO getSettings() {
        UserSettings settings = getSettingsEntity();
        return UserSettingsResponseDTO.fromEntity(settings);
    }

    @Override
    public UserSettingsResponseDTO updateSettings(@Valid UserSettingsRequestDTO requestDTO) {
        UserSettings settings = repository.findById(SETTINGS_ID)
                .orElseGet(UserSettings::new);

        settings.setTheme(requestDTO.theme());
        settings.setUnitSystem(requestDTO.unitSystem());
        settings.setAdditionalPreferences(requestDTO.additionalSettings());

        UserSettings savedSettings = repository.save(settings);
        return UserSettingsResponseDTO.fromEntity(savedSettings);
    }

    @Override
    public Object getSetting(String key) {
        UserSettings settings = getSettingsEntity();

        return switch (key) {
            case "theme" -> settings.getTheme();
            case "unitSystem" -> settings.getUnitSystem();
            default -> settings.getAdditionalPreferences().get(key);
        };
    }

    private UserSettings getSettingsEntity() {
        return repository.findById(SETTINGS_ID)
                .orElseGet(() -> repository.save(new UserSettings()));
    }
}