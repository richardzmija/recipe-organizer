package edu.agh.recipe.recipes.service;

import edu.agh.recipe.recipes.dto.*;
import edu.agh.recipe.recipes.model.Recipe;
import edu.agh.recipe.recipes.model.RecipeStep;
import edu.agh.recipe.recipes.repository.RecipeRepository;
import edu.agh.recipe.tags.dto.CreateTagDTO;
import edu.agh.recipe.tags.dto.TagDTO;
import edu.agh.recipe.tags.dto.TagReferenceDTO;
import edu.agh.recipe.tags.service.TagService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.data.domain.Pageable;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Validated
public class DefaultRecipeService implements RecipeService {

    private static final Logger logger = LoggerFactory.getLogger(DefaultRecipeService.class);
    private static final String AUTO_GENERATED_CATEGORY_NAME = "auto-generated";

    private final RecipeRepository recipeRepository;
    private final TagService tagService;

    public DefaultRecipeService(RecipeRepository recipeRepository, TagService tagService) {
        this.recipeRepository = Objects.requireNonNull(recipeRepository);
        this.tagService = Objects.requireNonNull(tagService);
    }

    @Override
    public Page<RecipeDTO> getAllRecipes(Pageable pageable) {
        Page<Recipe> recipePage = recipeRepository.findAll(pageable);
        return recipePage.map(recipe -> {
            List<TagDTO> tags = tagService.getTagsByIds(new ArrayList<>(recipe.getTagIds()));
            return RecipeDTO.fromEntity(recipe, tags);
        });
    }

    @Override
    public RecipeDTO getRecipeById(String id) {
        Recipe recipe = recipeRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Recipe not found."));

        List<TagDTO> tags = tagService.getTagsByIds(new ArrayList<>(recipe.getTagIds()));
        return RecipeDTO.fromEntity(recipe, tags);
    }

    @Override
    public RecipeDTO createRecipe(@Valid CreateRecipeDTO dto) {
        Set<String> tagIds = processTagReferences(dto.tags());

        Recipe recipe = new Recipe(
            dto.name(),
            dto.description(),
            dto.ingredients().stream().map(RecipeIngredientDTO::toEntity).toList(),
            dto.steps().stream().map(s -> new RecipeStep(s.title(), s.text())).toList(),
            tagIds
        );

        Recipe savedRecipe = recipeRepository.save(recipe);

        List<TagDTO> tags = tagService.getTagsByIds(new ArrayList<>(tagIds));
        return RecipeDTO.fromEntity(savedRecipe, tags);
    }

    @Override
    public RecipeDTO updateRecipe(String id, @Valid UpdateRecipeDTO dto) {
        Recipe existingRecipe = recipeRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Recipe not found."));

        // Track existing tag IDs so we can update usage counts.
        Set<String> oldTagIds = new HashSet<>(existingRecipe.getTagIds());

        // Process new tags.
        Set<String> newTagIds = processTagReferences(dto.tags());

        // Create updated recipe.
        Recipe recipe = new Recipe(
            dto.name(),
            dto.description(),
            dto.ingredients().stream().map(RecipeIngredientDTO::toEntity).toList(),
            dto.steps().stream().map(s -> new RecipeStep(s.title(), s.text())).toList(),
            newTagIds
        );
        recipe.setId(id);

        Recipe updatedRecipe = recipeRepository.save(recipe);

        // Update tag usage counts.
        updateTagUsageCounts(oldTagIds, newTagIds);

        List<TagDTO> tags = tagService.getTagsByIds(new ArrayList<>(newTagIds));
        return RecipeDTO.fromEntity(updatedRecipe, tags);
    }

    @Override
    public void deleteRecipe(String id) {
        Recipe recipe = recipeRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Recipe not found."));

        // Decrease tag usage counts before deletion.
        for (String tagId : recipe.getTagIds()) {
            tagService.decrementUsageCount(tagId);
        }

        recipeRepository.deleteById(id);
    }

    @Override
    public void bulkDeleteRecipes(List<String> ids) {
        List<Recipe> recipesToDelete = recipeRepository.findAllById(ids);

        if (recipesToDelete.size() != ids.size()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "One or more recipes not found.");
        }

        // Decrease tag usage counts for all recipes being deleted.
        for (Recipe recipe : recipesToDelete) {
            for (String tagId : recipe.getTagIds()) {
                tagService.decrementUsageCount(tagId);
            }
        }

        recipeRepository.deleteAllById(ids);
    }

    @Override
    public Page<RecipeDTO> findRecipesContainingAnyIngredients(List<String> ingredients, Pageable pageable) {
        List<String> lowerCaseIngredients = ingredients.stream()
                .map(String::toLowerCase)
                .toList();
        Page<Recipe> recipePage = recipeRepository.findByIngredientsIngredientNameIn(lowerCaseIngredients, pageable);
        return recipePage.map(recipe -> {
            List<TagDTO> tags = tagService.getTagsByIds(new ArrayList<>(recipe.getTagIds()));
            return RecipeDTO.fromEntity(recipe, tags);
        });
    }

    @Override
    public Page<RecipeDTO> findRecipesContainingAllIngredients(List<String> ingredientNames, Pageable pageable) {
        List<String> lowerCaseIngredientNames = ingredientNames.stream()
                .map(String::toLowerCase)
                .toList();
        Page<Recipe> recipePage = recipeRepository.findByAllIngredientsContaining(lowerCaseIngredientNames, pageable);
        return recipePage.map(recipe -> {
            List<TagDTO> tags = tagService.getTagsByIds(new ArrayList<>(recipe.getTagIds()));
            return RecipeDTO.fromEntity(recipe, tags);
        });
    }

    @Override
    public Page<RecipeDTO> suggestRecipesByName(String nameQuery, Pageable pageable) {
        Page<Recipe> recipePage = recipeRepository.findByNameStartingWithIgnoreCase(nameQuery, pageable);
        return recipePage.map(recipe -> {
            List<TagDTO> tags = tagService.getTagsByIds(new ArrayList<>(recipe.getTagIds()));
            return RecipeDTO.fromEntity(recipe, tags);
        });
    }

    @Override
    public Page<RecipeDTO> findRecipesByTag(String tagId, Pageable pageable) {
        Page<Recipe> recipePage = recipeRepository.findByTagIdsContaining(tagId, pageable);
        return recipePage.map(recipe -> {
            List<TagDTO> tags = tagService.getTagsByIds(new ArrayList<>(recipe.getTagIds()));
            return RecipeDTO.fromEntity(recipe, tags);
        });
    }

    @Override
    public Page<RecipeDTO> findRecipesByTagName(String tagName, Pageable pageable) {
        try {
            TagDTO tagDTO = tagService.getTagByName(tagName);
            return findRecipesByTag(tagDTO.id(), pageable);
        } catch (ResponseStatusException e) {
            // If the tag doesn't exist, return an empty page.
            return new PageImpl<>(List.of(), pageable, 0);
        }
    }

    @Override
    public Page<RecipeDTO> findRecipesByAnyTags(List<String> tagIds, Pageable pageable) {
        Page<Recipe> recipePage = recipeRepository.findByTagIdsIn(tagIds, pageable);
        return recipePage.map(recipe -> {
            List<TagDTO> tags = tagService.getTagsByIds(new ArrayList<>(recipe.getTagIds()));
            return RecipeDTO.fromEntity(recipe, tags);
        });
    }

    @Override
    public Page<RecipeDTO> findRecipesByAllTags(List<String> tagIds, Pageable pageable) {
        Page<Recipe> recipePage = recipeRepository.findByTagIdsAll(tagIds, pageable);
        return recipePage.map(recipe -> {
            List<TagDTO> tags = tagService.getTagsByIds(new ArrayList<>(recipe.getTagIds()));
            return RecipeDTO.fromEntity(recipe, tags);
        });
    }

    public RecipeDTO addTagsToRecipe(String recipeId, Set<TagReferenceDTO> tagReferences) {
        Recipe recipe = recipeRepository.findById(recipeId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Recipe not found."));

        Set<String> newTagIds = processTagReferences(tagReferences);

        Set<String> updatedTagIds = new HashSet<>(recipe.getTagIds());
        updatedTagIds.addAll(newTagIds);
        recipe.setTagIds(updatedTagIds);

        Recipe savedRecipe = recipeRepository.save(recipe);

        List<TagDTO> tags = tagService.getTagsByIds(new ArrayList<>(savedRecipe.getTagIds()));
        return RecipeDTO.fromEntity(savedRecipe, tags);
    }

    @Override
    public RecipeDTO removeTagsFromRecipe(String recipeId, Set<TagReferenceDTO> tagReferences) {
        Recipe recipe = recipeRepository.findById(recipeId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Recipe not found."));

        // Extract tag IDs from references.
        Set<String> tagIds = tagReferences.stream()
                .filter(TagReferenceDTO::isExistingTag)
                .map(TagReferenceDTO::id)
                .collect(Collectors.toSet());

        // For name-based references, try to find their IDs.
        Set<String> tagNames = tagReferences.stream()
                .filter(ref -> !ref.isExistingTag())
                .map(TagReferenceDTO::name)
                .collect(Collectors.toSet());

        if (!tagNames.isEmpty()) {
            tagService.getTagsByNames(new ArrayList<>(tagNames))
                    .forEach(tag -> tagIds.add(tag.id()));
        }

        // Find tags to remove that actually exist in the recipe.
        Set<String> existingTagIds = new HashSet<>(recipe.getTagIds());
        Set<String> tagsToRemove = new HashSet<>(tagIds);
        tagsToRemove.retainAll(existingTagIds);

        if (!tagsToRemove.isEmpty()) {
            existingTagIds.removeAll(tagsToRemove);
            recipe.setTagIds(existingTagIds);

            // Decrement usage count for each removed tag.
            for (String tagId : tagsToRemove) {
                try {
                    tagService.decrementUsageCount(tagId);
                } catch (ResponseStatusException e) {
                    logger.warn("Tag with ID {} was not removed because it doesn't exist.", tagId);
                }
            }

            recipe = recipeRepository.save(recipe);
        }

        List<TagDTO> tags = tagService.getTagsByIds(new ArrayList<>(recipe.getTagIds()));
        return RecipeDTO.fromEntity(recipe, tags);
    }

    @Override
    public List<TagDTO> suggestTagsForRecipe(String recipeId, int limit) {
        Recipe recipe = recipeRepository.findById(recipeId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Recipe not found."));

        Set<String> existingTagIds = recipe.getTagIds();
        int extendedLimit = Math.min(limit * 2, 50);
        List<TagDTO> popularTags = tagService.getPopularTags(extendedLimit);

        // Filter out the tags that the recipe already has.
        return popularTags.stream()
                .filter(tag -> !existingTagIds.contains(tag.id()))
                .limit(limit)
                .collect(Collectors.toList());
    }

    /**
     * Process tag references - either use existing tags or create new ones.
     */
    private Set<String> processTagReferences(Set<TagReferenceDTO> tagReferences) {
        if (tagReferences == null) {
            return new HashSet<>();
        }

        Set<String> tagIds = new HashSet<>();

        for (TagReferenceDTO ref : tagReferences) {
            if (ref.isExistingTag()) {
                // Handle existing tag by ID.
                TagDTO tag = tagService.getTagById(ref.id());
                tagIds.add(tag.id());
                tagService.incrementUsageCount(tag.id());
            } else if (ref.name() != null && !ref.name().isEmpty()) {
                // Check if tag exists by name.
                String tagId;
                if (tagService.existsByTagName(ref.name())) {
                    // Tag exists, get it by name.
                    TagDTO existingTag = tagService.getTagByName(ref.name());
                    tagId = existingTag.id();
                } else {
                    // Tag doesn't exist, create it.
                    CreateTagDTO createTagDTO = new CreateTagDTO(
                            ref.name(),
                            null, // default color
                            null, // no description
                            AUTO_GENERATED_CATEGORY_NAME // category
                    );
                    TagDTO newTag = tagService.createTag(createTagDTO);
                    tagId = newTag.id();
                }

                tagIds.add(tagId);
                tagService.incrementUsageCount(tagId);
            }
        }

        return tagIds;
    }

    /**
     * Updates tag usage counts when tags are changed
     */
    private void updateTagUsageCounts(Set<String> oldTagIds, Set<String> newTagIds) {
        // Decrement usage for tags that were removed.
        Set<String> removedTags = new HashSet<>(oldTagIds);
        removedTags.removeAll(newTagIds);
        for (String tagId : removedTags) {
            tagService.decrementUsageCount(tagId);
        }

        // Increment usage for new tags that were added.
        Set<String> addedTags = new HashSet<>(newTagIds);
        addedTags.removeAll(oldTagIds);
        for (String tagId : addedTags) {
            tagService.incrementUsageCount(tagId);
        }
    }
}
