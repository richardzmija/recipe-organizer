package edu.agh.recipe.service;

import edu.agh.recipe.model.MeasurementUnit;
import edu.agh.recipe.model.dto.UnitDTO;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;


@Service
public class UnitService {
    public List<UnitDTO> getAllUnits() {
        return Arrays.stream(MeasurementUnit.values())
                .map(unit -> UnitDTO.of(unit.name(), unit.getSystem().name()))
                .toList();
    }

    public List<UnitDTO> getMetricUnits() {
        return Arrays.stream(MeasurementUnit.values())
                .filter(unit -> unit.getSystem() == MeasurementUnit.MeasurementSystem.METRIC)
                .map(unit -> UnitDTO.of(unit.name(), unit.getSystem().name()))
                .toList();
    }

    public List<UnitDTO> getImperialUnits() {
        return Arrays.stream(MeasurementUnit.values())
                .filter(unit -> unit.getSystem() == MeasurementUnit.MeasurementSystem.IMPERIAL)
                .map(unit -> UnitDTO.of(unit.name(), unit.getSystem().name()))
                .toList();
    }
}
