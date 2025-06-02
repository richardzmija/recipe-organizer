package edu.agh.recipe.units.dictionary;

import edu.agh.recipe.units.domain.MeasurementUnit;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UnitDictionaryService {

    private final UnitDictionary dictionary;

    public UnitDictionaryService(UnitDictionary dictionary) {
        this.dictionary = dictionary;
    }

    public Optional<MeasurementUnit> resolveUnit(String rawUnit) {
        return dictionary.resolve(rawUnit);
    }
}
