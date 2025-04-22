package edu.agh.recipe.units.service;

import edu.agh.recipe.units.dto.UnitDTO;

import java.util.List;

public interface UnitService {

    List<UnitDTO> getAllUnits();
    List<UnitDTO> getMetricUnits();
    List<UnitDTO> getImperialUnits();

}
