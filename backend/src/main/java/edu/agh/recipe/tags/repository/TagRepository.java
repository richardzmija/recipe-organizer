package edu.agh.recipe.tags.repository;

import edu.agh.recipe.tags.model.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TagRepository extends MongoRepository<Tag, String> {
    Optional<Tag> findByName(String name);

    boolean existsByName(String name);

    Page<Tag> findByNameContainingIgnoreCase(String name, Pageable pageable);

    List<Tag> findByNameIn(List<String> names);

    Page<Tag> findByCategory(String category, Pageable pageable);
}