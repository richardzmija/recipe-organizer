package edu.agh.recipe.export.service;

import edu.agh.recipe.recipes.dto.RecipeDTO;
import edu.agh.recipe.recipes.service.RecipeService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Component
public class RecipeDataFetcher {

    private final RecipeService recipeService;

    public RecipeDataFetcher(RecipeService recipeService) {
        this.recipeService = Objects.requireNonNull(recipeService);
    }

    public List<RecipeDTO> fetchAllRecipes() {
        List<RecipeDTO> allRecipes = new ArrayList<>();

        int pageNumber = 0;
        int pageSize = 100;
        Page<RecipeDTO> page;

        do {
            Pageable pageable = PageRequest.of(pageNumber, pageSize);
            page = recipeService.getAllRecipes(pageable);
            allRecipes.addAll(page.getContent());
            pageNumber++;
        } while (pageNumber < page.getTotalPages());

        return allRecipes;
    }

    public RecipeDTO fetchRecipeById(String id) {
        return recipeService.getRecipeById(id);
    }
}