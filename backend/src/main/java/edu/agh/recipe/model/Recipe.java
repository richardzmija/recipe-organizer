package edu.agh.recipe.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Document("recipes")
public class Recipe {
    @Id
    private String id;
    private String name;
    private String description;
    private String image;
    // TODO: To na razie lista Stringów, potem (chyba) obiektów? zależy od implementacji
    private List<String> tags = new ArrayList<>();
    private List<RecipeIngredient> ingredients = new ArrayList<>();
    private List<RecipeStep> steps;

    public Recipe(String name, String description, String image, List<String> tags, List<RecipeIngredient> ingredients, List<RecipeStep> steps) {
        this.name = name;
        this.description = description;
        this.image = image;
        this.tags = tags;
        this.ingredients = ingredients;
        this.steps = steps;
    }

    public Recipe(String name, String description, String image) {
        this.name = name;
        this.description = description;
        this.image = image;
    }

    public Recipe() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public List<RecipeIngredient> getIngredients() {
        return ingredients;
    }

    public void setIngredients(List<RecipeIngredient> ingredients) {
        this.ingredients = ingredients;
    }

    public List<RecipeStep> getSteps() {
        return steps;
    }

    public void setSteps(List<RecipeStep> steps) {
        this.steps = steps;
    }
}
