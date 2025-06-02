package edu.agh.recipe.units.controller;

import edu.agh.recipe.units.domain.MeasurementUnit;
import edu.agh.recipe.units.domain.Quantity;
import edu.agh.recipe.units.domain.QuantityFormat;
import edu.agh.recipe.units.dto.ConversionRequestDTO;
import edu.agh.recipe.units.dto.ConversionResponseDTO;
import edu.agh.recipe.units.dto.UnitDTO;
import edu.agh.recipe.units.service.ConversionService;
import edu.agh.recipe.units.service.DefaultConversionService.UnsupportedConversionException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Arrays;
import java.util.Objects;

@RestController
@RequestMapping("/units")
@Tag(name = "Unit Conversion", description = "API for converting between measurement units")
public class ConversionController {
    private final ConversionService conversionService;

    public ConversionController(ConversionService conversionService) {
        this.conversionService = Objects.requireNonNull(conversionService);
    }

    @Operation(
            summary = "Convert between measurement units",
            description = "Converts a quantity from one measurement unit to another"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Conversion successful",
                    content = @Content(schema = @Schema(implementation = ConversionResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request or unsupported conversion",
                    content = @Content
            )
    })
    @PostMapping("/convert")
    public ResponseEntity<ConversionResponseDTO> convertUnit(@Valid @RequestBody ConversionRequestDTO request) {
        try {
            MeasurementUnit fromUnit = getMeasurementUnit(request.fromUnit());
            MeasurementUnit toUnit = getMeasurementUnit(request.toUnit());

            QuantityFormat format = request.format() != null ?
                    QuantityFormat.valueOf(request.format().toUpperCase()) :
                    null;

            Quantity sourceQuantity = format != null ?
                    new Quantity(request.value(), format) :
                    Quantity.of(request.value());

            Quantity convertedQuantity = conversionService.convert(sourceQuantity, fromUnit, toUnit);

            return ResponseEntity.ok(new ConversionResponseDTO(
                    request.value(),
                    fromUnit.name(),
                    fromUnit.getName(),
                    convertedQuantity.getValue(),
                    toUnit.name(),
                    toUnit.getName(),
                    sourceQuantity.getFormattedValue(),
                    convertedQuantity.getFormattedValue()
            ));
        } catch (UnsupportedConversionException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid unit or format: " + e.getMessage());
        }
    }

    @Operation(
            summary = "Check if conversion is possible",
            description = "Checks whether a conversion between the specified units is supported"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Returns true if conversion is possible, false otherwise"),
            @ApiResponse(responseCode = "400", description = "Invalid unit specified", content = @Content)
    })
    @GetMapping("/can-convert")
    public ResponseEntity<Boolean> canConvert(
            @RequestParam String fromUnit,
            @RequestParam String toUnit
    ) {
        try {
            MeasurementUnit from = getMeasurementUnit(fromUnit);
            MeasurementUnit to = getMeasurementUnit(toUnit);
            return ResponseEntity.ok(conversionService.canConvert(from, to));
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid unit: " + e.getMessage());
        }
    }

    @Operation(
            summary = "Get all possible conversions for a unit",
            description = "Returns all measurement units that can be converted from the specified unit"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of compatible units"),
            @ApiResponse(responseCode = "400", description = "Invalid unit specified", content = @Content)
    })
    @GetMapping("/{unit}/compatible")
    public ResponseEntity<Object> getCompatibleUnits(@PathVariable String unit) {
        try {
            MeasurementUnit sourceUnit = getMeasurementUnit(unit);
            var compatibleUnits = Arrays.stream(MeasurementUnit.values())
                    .filter(targetUnit -> conversionService.canConvert(sourceUnit, targetUnit))
                    .map(targetUnit -> new UnitDTO(targetUnit.name(), targetUnit.getSymbol(), targetUnit.getName(), targetUnit.getSystem().name()))
                    .toList();
            return ResponseEntity.ok(compatibleUnits);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid unit: " + e.getMessage());
        }
    }

    private MeasurementUnit getMeasurementUnit(String unitName) {
        try {
            return MeasurementUnit.valueOf(unitName.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Unknown measurement unit: " + unitName
            );
        }
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<String> handleResponseStatusException(ResponseStatusException ex) {
        return ResponseEntity.status(ex.getStatusCode()).body(ex.getReason());
    }
}