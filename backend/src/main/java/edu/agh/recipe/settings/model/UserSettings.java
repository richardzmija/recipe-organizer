package edu.agh.recipe.settings.model;

import edu.agh.recipe.units.domain.MeasurementSystem;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.HashMap;
import java.util.Map;

@Document("settings")
public class UserSettings {
    @Id
    private String id = "settings";

    private Theme theme;
    private MeasurementSystem unitSystem;
    private Map<String, Object> additionalPreferences = new HashMap<>();

    public UserSettings() {
        this.theme = Theme.LIGHT;
        this.unitSystem = MeasurementSystem.METRIC;
    }

    public String getId() {
        return id;
    }

    public Theme getTheme() {
        return theme;
    }

    public void setTheme(Theme theme) {
        this.theme = theme;
    }

    public MeasurementSystem getUnitSystem() {
        return unitSystem;
    }

    public void setUnitSystem(MeasurementSystem unitSystem) {
        this.unitSystem = unitSystem;
    }

    public Map<String, Object> getAdditionalPreferences() {
        return additionalPreferences;
    }

    public void setAdditionalPreferences(Map<String, Object> additionalPreferences) {
        this.additionalPreferences = additionalPreferences != null ?
            additionalPreferences : new HashMap<>();
    }
}