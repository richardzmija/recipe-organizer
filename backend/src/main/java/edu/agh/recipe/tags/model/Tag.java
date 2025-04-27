package edu.agh.recipe.tags.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("tags")
public class Tag {
    @Id
    private String id;

    @Indexed(unique = true)
    private String name;

    private String color;
    private String description;
    private String category;
    private int usageCount;

    public Tag() {}

    public Tag(String name, String color, String description, String category) {
        this.name = name;
        this.color = color;
        this.description = description;
        this.category = category;
        this.usageCount = 0;
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

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getUsageCount() {
        return usageCount;
    }

    public void setUsageCount(int usageCount) {
        this.usageCount = usageCount;
    }
}