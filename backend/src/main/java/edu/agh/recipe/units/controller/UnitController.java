package edu.agh.recipe.units.controller;

import edu.agh.recipe.units.dto.UnitDTO;
import edu.agh.recipe.units.service.UnitService;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/units")
@Tag(name = "Measurement Units", description = "API for retrieving measurement units")
public class UnitController {
    private final UnitService unitService;

    public UnitController(UnitService unitService) {
        this.unitService = Objects.requireNonNull(unitService);
    }

    @Operation(
            summary = "Get all measurement units.",
            description = "Retrieves all available measurement units from both metric and imperial systems."
    )
    @ApiResponse(
            responseCode = "200",
            description = "Successfully retrieved all units.",
            content = @Content(
                    array = @ArraySchema(schema = @Schema(implementation = UnitDTO.class))
            )
    )
    @GetMapping
    public ResponseEntity<List<UnitDTO>> getAllUnits() {
        return ResponseEntity.ok(unitService.getAllUnits());
    }

    @Operation(
            summary = "Get metric measurement units.",
            description = "Retrieves all measurement units from the metric system."
    )
    @ApiResponse(
            responseCode = "200",
            description = "Successfully retrieved metric units.",
            content = @Content(
                    array = @ArraySchema(schema = @Schema(implementation = UnitDTO.class))
            )
    )
    @GetMapping("/metric")
    public ResponseEntity<List<UnitDTO>> getMetricUnits() {
        return ResponseEntity.ok(unitService.getMetricUnits());
    }

    @Operation(
            summary = "Get imperial measurement units.",
            description = "Retrieves all measurement units from the imperial system."
    )
    @ApiResponse(
            responseCode = "200",
            description = "Successfully retrieved imperial units.",
            content = @Content(
                    array = @ArraySchema(schema = @Schema(implementation = UnitDTO.class))
            )
    )
    @GetMapping("/imperial")
    public ResponseEntity<List<UnitDTO>> getImperialUnits() {
        return ResponseEntity.ok(unitService.getImperialUnits());
    }
}
