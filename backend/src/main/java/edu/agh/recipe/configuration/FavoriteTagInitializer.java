package edu.agh.recipe.configuration;

import edu.agh.recipe.tags.model.Tag;
import edu.agh.recipe.tags.repository.TagRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Objects;


@Component
public class FavoriteTagInitializer implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(FavoriteTagInitializer.class);
    private static final String FAVORITE_TAG_ID = "favorite-tag-id";

    private final TagRepository tagRepository;

    public FavoriteTagInitializer(TagRepository tagRepository) {
        this.tagRepository = Objects.requireNonNull(tagRepository);
    }

    @Override
    public void run(String... args) {
        if (!tagRepository.existsById(FAVORITE_TAG_ID)) {
            logger.info("Creating default 'favorite' tag");

            Tag favoriteTag = new Tag("Favorite", "#FFD700", "User's favorite recipes", "System");
            favoriteTag.setId(FAVORITE_TAG_ID);
            favoriteTag.setUsageCount(0);

            tagRepository.save(favoriteTag);

            logger.info("Default 'favorite' tag created successfully");
        } else {
            logger.info("Default 'favorite' tag already exists");
        }
    }
}
