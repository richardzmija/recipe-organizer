package edu.agh.recipe.settings.service;

import edu.agh.recipe.settings.dto.UserSettingsRequestDTO;
import edu.agh.recipe.settings.dto.UserSettingsResponseDTO;
import jakarta.validation.Valid;

public interface UserSettingsService {

    UserSettingsResponseDTO getSettings();
    UserSettingsResponseDTO updateSettings(@Valid UserSettingsRequestDTO requestDTO);
    Object getSetting(String key);

}