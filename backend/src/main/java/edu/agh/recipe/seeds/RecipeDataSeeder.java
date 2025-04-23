package edu.agh.recipe.seeds;

import edu.agh.recipe.recipes.model.Recipe;
import edu.agh.recipe.recipes.model.RecipeIngredient;
import edu.agh.recipe.recipes.model.RecipeStep;
import edu.agh.recipe.recipes.repository.RecipeRepository;
import edu.agh.recipe.units.domain.MeasurementUnit;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.util.*;

@Configuration
public class RecipeDataSeeder {

    private final Random random = new Random();
    
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
            
            ingredients.add(new RecipeIngredient(ingredient, unit, quantity));
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
        
        return new Recipe(name, description, ingredients, steps);
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
    
    @Bean
    @Profile("dev")
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
            
            recipeRepository.saveAll(recipes);
            
            System.out.println("Database seeded successfully with 20 recipes.");
        };
    }
} 