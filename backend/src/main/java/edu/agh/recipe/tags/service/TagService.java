package edu.agh.recipe.tags.service;

import edu.agh.recipe.tags.dto.CreateTagDTO;
import edu.agh.recipe.tags.dto.TagDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface TagService {
    Page<TagDTO> getAllTags(Pageable pageable);
    TagDTO getTagById(String id);
    TagDTO getTagByName(String name);
    boolean existsByTagName(String name);
    TagDTO createTag(CreateTagDTO createTagDTO);
    TagDTO updateTag(String id, CreateTagDTO updateTagDTO);
    void deleteTag(String id);
    Page<TagDTO> findTagsByNameContaining(String name, Pageable pageable);
    List<TagDTO> getTagsByNames(List<String> names);
    Page<TagDTO> getTagsByCategory(String category, Pageable pageable);
    void incrementUsageCount(String tagId);
    void decrementUsageCount(String tagId);
    List<TagDTO> getPopularTags(int limit);
    List<TagDTO> getTagsByIds(List<String> ids);
}
