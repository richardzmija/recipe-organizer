package edu.agh.recipe.controller;

import edu.agh.recipe.model.Recipe;
import edu.agh.recipe.model.dto.MessageDTO;
import edu.agh.recipe.model.dto.RecipeRequestDTO;
import edu.agh.recipe.service.RecipeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/recipe")
//@CrossOrigin(origins = "*") // Dla frontendu, aktywacja CORS
public class RecipeController {

    private final RecipeService recipeService;

    public RecipeController(RecipeService recipeService) {
        this.recipeService = recipeService;
    }

    @GetMapping()
    public ResponseEntity<List<Recipe>> getAllRecipes() {
        return ResponseEntity.ok(recipeService.getAllRecipes());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Recipe> getRecipeById(@PathVariable String id) {
        Recipe recipe = recipeService.getRecipeById(id);
        return ResponseEntity.ok(recipe);
    }

    @GetMapping("/metrics")
    public ResponseEntity<List<String>> getRecipeMetrics() {
        return ResponseEntity.ok(recipeService.getAllMetrics());
    }

    @PostMapping()
    public ResponseEntity<MessageDTO> addRecipe(@RequestBody RecipeRequestDTO dto) {
        recipeService.addRecipe(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(new MessageDTO("Recipe added successfully"));
    }
}
