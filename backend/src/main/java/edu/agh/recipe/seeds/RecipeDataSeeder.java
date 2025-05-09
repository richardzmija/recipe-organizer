package edu.agh.recipe.seeds;

import edu.agh.recipe.images.dto.ImageMetadataDTO;
import edu.agh.recipe.images.service.ImageService;
import edu.agh.recipe.recipes.model.Recipe;
import edu.agh.recipe.recipes.model.RecipeIngredient;
import edu.agh.recipe.recipes.model.RecipeStep;
import edu.agh.recipe.recipes.repository.RecipeRepository;
import edu.agh.recipe.tags.model.Tag;
import edu.agh.recipe.tags.repository.TagRepository;
import edu.agh.recipe.units.domain.MeasurementUnit;
import edu.agh.recipe.units.domain.Quantity;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

@Configuration
@Profile("dev")
public class RecipeDataSeeder {

    private final Random random = new Random();
    private final TagRepository tagRepository;
    private final ImageService imageService;
    private final GridFsTemplate gridFsTemplate;
    
    // Lists of common recipe tags
    private final List<String> commonTags = Arrays.asList(
            "Vegetarian", "Vegan", "Gluten-Free", "Dairy-Free", "Low-Carb", 
            "Keto", "Paleo", "Mediterranean", "Asian", "Italian", 
            "Mexican", "Indian", "Breakfast", "Lunch", "Dinner", 
            "Dessert", "Appetizer", "Soup", "Salad", "Snack", 
            "Quick", "Easy", "Healthy", "Comfort Food", "Spicy"
    );
    
    // Tag colors
    private final List<String> tagColors = Arrays.asList(
            "#FF5733", "#33FF57", "#3357FF", "#F033FF", "#FF33A6",
            "#33FFF0", "#FFF033", "#FF8333", "#33FFD4", "#D433FF"
    );

    // Food images stored in resources folder for recipe seeding
    // Random images will be selected from this list and attached to each recipe
    private final List<String> sampleImagePaths = Arrays.asList(
            "seed-images/food1.jpg",
            "seed-images/food2.jpg",
            "seed-images/food3.jpg",
            "seed-images/food4.jpg",
            "seed-images/food5.jpg",
            "seed-images/food6.jpg"
    );
    
    public RecipeDataSeeder(TagRepository tagRepository, ImageService imageService, GridFsTemplate gridFsTemplate) {
        this.tagRepository = tagRepository;
        this.imageService = imageService;
        this.gridFsTemplate = gridFsTemplate;
    }
    
    // Lists of common recipe ingredients
    private final List<String> commonIngredients = Arrays.asList(
            "Salt", "Pepper", "Olive oil", "Garlic", "Onion", "Butter", "Eggs",
            "Flour", "Sugar", "Milk", "Chicken", "Beef", "Pork", "Rice", "Pasta",
            "Tomatoes", "Potatoes", "Carrots", "Broccoli", "Spinach", "Mushrooms",
            "Cheese", "Lemon", "Lime", "Soy sauce", "Vinegar", "Honey", "Oregano", 
            "Basil", "Thyme", "Rosemary", "Parsley", "Cilantro", "Cumin", "Paprika",
            "Cinnamon", "Nutmeg", "Vanilla", "Chocolate", "Almonds", "Walnuts"
    );
    
    // Lists of recipe names
    private final List<String> recipeNames = Arrays.asList(
            "Classic Italian Pasta", "Creamy Mushroom Risotto", "Spicy Thai Curry",
            "Homemade Pizza", "Beef Stroganoff", "Chicken Alfredo", "Vegetable Stir-Fry",
            "BBQ Pulled Pork", "Lemon Herb Roasted Chicken", "Garlic Butter Shrimp",
            "Mediterranean Quinoa Salad", "Tomato Basil Soup", "Beef Tacos", 
            "Vegetable Lasagna", "Honey Glazed Salmon", "Eggplant Parmesan",
            "Stuffed Bell Peppers", "Teriyaki Chicken Stir-Fry", "Chocolate Lava Cake",
            "French Onion Soup", "Greek Salad", "Chicken Pot Pie", "Beef Stew",
            "Pesto Pasta", "Chicken Curry", "Mushroom Risotto", "Apple Pie",
            "Chocolate Chip Cookies", "Banana Bread", "Vegetable Curry"
    );
    
    // Lists of recipe descriptions
    private final List<String> recipeDescriptions = Arrays.asList(
            "A delicious meal that's perfect for dinner parties.",
            "A hearty dish that's comforting on cold days.",
            "A flavorful recipe that's sure to impress your family.",
            "A quick and easy meal for busy weeknights.",
            "A nutritious option packed with vegetables.",
            "A classic recipe with a modern twist.",
            "A spicy dish that brings the heat to your table.",
            "A sweet treat that's perfect for dessert lovers.",
            "A savory dish with complex flavors.",
            "A light meal that's perfect for lunch.",
            "A rich and creamy recipe that feels indulgent.",
            "A family favorite passed down through generations.",
            "A seasonal dish that highlights fresh ingredients.",
            "A crowd-pleasing recipe for entertaining guests.",
            "A healthy option for those watching their diet."
    );
    
    // Method to generate a random recipe
    private Recipe generateRandomRecipe() {
        String name = recipeNames.get(random.nextInt(recipeNames.size()));
        String description = recipeDescriptions.get(random.nextInt(recipeDescriptions.size()));
        
        // Generate random ingredients (between 4-10 ingredients)
        int ingredientCount = random.nextInt(7) + 4;
        List<RecipeIngredient> ingredients = new ArrayList<>();
        Set<String> usedIngredients = new HashSet<>();
        
        for (int i = 0; i < ingredientCount; i++) {
            String ingredient;
            do {
                ingredient = commonIngredients.get(random.nextInt(commonIngredients.size()));
            } while (usedIngredients.contains(ingredient));
            
            usedIngredients.add(ingredient);
            
            MeasurementUnit unit = MeasurementUnit.values()[random.nextInt(MeasurementUnit.values().length)];
            double quantity = (random.nextInt(20) + 1) * (random.nextBoolean() ? 0.5 : 1.0);
            
            ingredients.add(RecipeIngredient.of(ingredient, unit, quantity));
        }
        
        // Generate random steps (between 3-8 steps)
        int stepCount = random.nextInt(6) + 3;
        List<RecipeStep> steps = new ArrayList<>();
        
        for (int i = 0; i < stepCount; i++) {
            String title = "Step " + (i + 1);
            String text;
            
            switch (i) {
                case 0:
                    text = "Prepare all ingredients. " + getRandomPreparationInstruction();
                    break;
                case 1:
                    text = getRandomCookingInstruction();
                    break;
                case 2:
                    text = getRandomMixingInstruction(ingredients);
                    break;
                default:
                    if (i == stepCount - 1) {
                        text = "Serve and enjoy! " + getRandomServingInstruction();
                    } else {
                        text = getRandomCookingMethod();
                    }
            }
            
            steps.add(new RecipeStep(title, text));
        }
        
        // Get random tags for the recipe
        Set<String> tagIds = getRandomTagIds(2, 5);
        
        return new Recipe(name, description, ingredients, steps, tagIds);
    }
    
    private String getRandomPreparationInstruction() {
        List<String> instructions = Arrays.asList(
                "Chop all vegetables into small pieces.",
                "Measure out all ingredients before starting.",
                "Wash and peel vegetables as needed.",
                "Mince the garlic and dice the onions.",
                "Slice ingredients into uniform pieces for even cooking."
        );
        return instructions.get(random.nextInt(instructions.size()));
    }
    
    private String getRandomCookingInstruction() {
        List<String> instructions = Arrays.asList(
                "Heat the pan over medium heat and add oil.",
                "Preheat the oven to 350°F (175°C).",
                "Bring a large pot of salted water to a boil.",
                "Heat the skillet until hot, then reduce to medium heat.",
                "Warm the sauce over low heat, stirring occasionally."
        );
        return instructions.get(random.nextInt(instructions.size()));
    }
    
    private String getRandomMixingInstruction(List<RecipeIngredient> ingredients) {
        if (ingredients.isEmpty()) {
            return "Mix all ingredients together thoroughly.";
        }
        
        String ingredient1 = ingredients.get(random.nextInt(ingredients.size())).ingredientName();
        String ingredient2 = ingredients.get(random.nextInt(ingredients.size())).ingredientName();
        
        List<String> instructions = Arrays.asList(
                "Mix " + ingredient1 + " and " + ingredient2 + " together in a bowl.",
                "Combine all ingredients and stir well.",
                "Whisk together " + ingredient1 + " until smooth, then add remaining ingredients.",
                "Fold in " + ingredient1 + " gently to maintain texture.",
                "Stir in " + ingredient2 + " until evenly distributed."
        );
        return instructions.get(random.nextInt(instructions.size()));
    }
    
    private String getRandomCookingMethod() {
        List<String> methods = Arrays.asList(
                "Cook for 10-15 minutes until golden brown.",
                "Simmer on low heat for 20 minutes, stirring occasionally.",
                "Bake in the preheated oven for 30 minutes.",
                "Sauté until fragrant and slightly softened.",
                "Steam until tender, about 5-8 minutes.",
                "Grill on each side for 4-5 minutes until marks appear.",
                "Roast in the oven until caramelized.",
                "Stir-fry on high heat for 2-3 minutes."
        );
        return methods.get(random.nextInt(methods.size()));
    }
    
    private String getRandomServingInstruction() {
        List<String> instructions = Arrays.asList(
                "Garnish with fresh herbs before serving.",
                "Serve hot with your favorite side dish.",
                "Let rest for 5 minutes before serving.",
                "Best served immediately while still warm.",
                "Plate and add a final drizzle of sauce on top.",
                "This dish is great with a side of crusty bread.",
                "Pairs well with a glass of red wine."
        );
        return instructions.get(random.nextInt(instructions.size()));
    }
    
    // Method to seed tag data and return random tag IDs
    private Set<String> getRandomTagIds(int minTags, int maxTags) {
        // First ensure we have tags in the database
        seedTags();
        
        // Get all tags from the database
        List<Tag> allTags = tagRepository.findAll();
        
        if (allTags.isEmpty()) {
            return new HashSet<>();
        }
        
        // Select random number of tags between min and max
        int tagCount = random.nextInt(maxTags - minTags + 1) + minTags;
        tagCount = Math.min(tagCount, allTags.size());
        
        // Shuffle the list and take the first 'tagCount' elements
        Collections.shuffle(allTags);
        
        Set<String> selectedTagIds = new HashSet<>();
        for (int i = 0; i < tagCount; i++) {
            selectedTagIds.add(allTags.get(i).getId());
        }
        
        return selectedTagIds;
    }
    
    // Method to seed tags if they don't exist
    private void seedTags() {
        // Check if we already have tags
        if (tagRepository.count() > 0) {
            return;
        }
        
        List<Tag> tags = new ArrayList<>();
        for (String tagName : commonTags) {
            String color = tagColors.get(random.nextInt(tagColors.size()));
            String description = "Tag for " + tagName.toLowerCase() + " recipes";
            String category = getCategoryForTag(tagName);
            
            tags.add(new Tag(tagName, color, description, category));
        }
        
        tagRepository.saveAll(tags);
        System.out.println("Seeded database with " + tags.size() + " tags");
    }
    
    // Helper method to categorize tags
    private String getCategoryForTag(String tagName) {
        if (List.of("Vegetarian", "Vegan", "Gluten-Free", "Dairy-Free", "Low-Carb", "Keto", "Paleo").contains(tagName)) {
            return "Dietary";
        } else if (List.of("Mediterranean", "Asian", "Italian", "Mexican", "Indian").contains(tagName)) {
            return "Cuisine";
        } else if (List.of("Breakfast", "Lunch", "Dinner", "Dessert", "Appetizer", "Soup", "Salad", "Snack").contains(tagName)) {
            return "Meal Type";
        } else {
            return "Other";
        }
    }
    

    // Method to add images to a recipe using GridFsTemplate directly
    private Set<String> addImagesToRecipe(String recipeId) {
        Set<String> imageIds = new HashSet<>();
        
        try {
            // Add 1-3 images per recipe
            int imageCount = random.nextInt(3) + 1;
            
            for (int i = 0; i < imageCount; i++) {
                // Get a random sample image
                String imagePath = sampleImagePaths.get(random.nextInt(sampleImagePaths.size()));
                
                // Load the image from resources
                ClassPathResource resource = new ClassPathResource(imagePath);
                
                try (InputStream inputStream = resource.getInputStream()) {
                    // Create metadata
                    DBObject metadata = new BasicDBObject();
                    metadata.put("recipeId", recipeId);
                    metadata.put("description", "Image for " + recipeId);
                    metadata.put("isPrimary", i == 0); // First image is primary
                    metadata.put("uploadDate", new Date());
                    
                    // Store the image directly using GridFsTemplate
                    String filename = imagePath.substring(imagePath.lastIndexOf('/') + 1);
                    String imageId = gridFsTemplate.store(
                            inputStream, 
                            filename,
                            "image/jpeg", 
                            metadata
                    ).toString();
                    
                    imageIds.add(imageId);
                }
            }
        } catch (IOException e) {
            System.err.println("Error adding images to recipe: " + e.getMessage());
        }
        
        return imageIds;
    }
    
    @Bean
    public CommandLineRunner seedDatabaseWithRecipes(RecipeRepository recipeRepository) {
        return args -> {
            // Check if we need to seed the database
            long count = recipeRepository.count();
            if (count > 0) {
                System.out.println("Database already contains " + count + " recipes. Skipping seeding.");
                return;
            }
            
            System.out.println("Seeding database with 20 recipes...");
            
            List<Recipe> recipes = new ArrayList<>();
            for (int i = 0; i < 20; i++) {
                recipes.add(generateRandomRecipe());
            }
            
            // First save recipes to get their IDs
            List<Recipe> savedRecipes = recipeRepository.saveAll(recipes);
            
            // Then add images to each recipe
            for (Recipe recipe : savedRecipes) {
                Set<String> imageIds = addImagesToRecipe(recipe.getId());
                recipe.setImageIds(imageIds);
            }
            
            // Update recipes with image IDs
            recipeRepository.saveAll(savedRecipes);
            
            System.out.println("Database seeded successfully with 20 recipes and their images.");
        };
    }
} 