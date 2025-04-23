package edu.agh.recipe.units.service;

import edu.agh.recipe.units.domain.MeasurementUnit;
import edu.agh.recipe.units.domain.MeasurementSystem;
import edu.agh.recipe.units.dto.UnitDTO;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;


@Service
public class DefaultUnitService implements UnitService {


    @Cacheable("allUnits")
    @Override
    public List<UnitDTO> getAllUnits() {
        return Arrays.stream(MeasurementUnit.values())
                .map(UnitDTO::of)
                .toList();
    }

    @Cacheable("metricUnits")
    @Override
    public List<UnitDTO> getMetricUnits() {
        return Arrays.stream(MeasurementUnit.values())
                .filter(unit -> unit.getSystem() == MeasurementSystem.METRIC)
                .map(UnitDTO::of)
                .toList();
    }

    @Cacheable("imperialUnits")
    @Override
    public List<UnitDTO> getImperialUnits() {
        return Arrays.stream(MeasurementUnit.values())
                .filter(unit -> unit.getSystem() == MeasurementSystem.IMPERIAL)
                .map(UnitDTO::of)
                .toList();
    }
}
