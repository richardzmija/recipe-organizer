package edu.agh.recipe.tags.service;

import edu.agh.recipe.tags.dto.CreateTagDTO;
import edu.agh.recipe.tags.dto.TagDTO;
import edu.agh.recipe.tags.model.Tag;
import edu.agh.recipe.tags.repository.TagRepository;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Objects;

@Service
@Validated
public class DefaultTagService implements TagService {

    private final TagRepository tagRepository;

    public DefaultTagService(TagRepository tagRepository) {
        this.tagRepository = Objects.requireNonNull(tagRepository);
    }

    @Override
    public Page<TagDTO> getAllTags(Pageable pageable) {
        return tagRepository.findAll(pageable).map(this::toDTO);
    }

    @Override
    public TagDTO getTagById(String id) {
        Tag tag = tagRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Tag not found."));
        return toDTO(tag);
    }

    @Override
    public List<TagDTO> getTagsByIds(List<String> ids) {
        return tagRepository.findAllById(ids).stream()
                .map(this::toDTO)
                .toList();
    }

    @Override
    public TagDTO getTagByName(String name) {
        Tag tag = tagRepository.findByName(name)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Tag not found."));
        return toDTO(tag);
    }

    @Override
    public boolean existsByTagName(String name) {
        return tagRepository.existsByName(name);
    }

    @Override
    public TagDTO createTag(@Valid CreateTagDTO createTagDTO) {
        if (tagRepository.existsByName(createTagDTO.name())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Tag with this name already exists.");
        }

        Tag tag = toEntity(createTagDTO);
        Tag savedTag = tagRepository.save(tag);
        return toDTO(savedTag);
    }

    @Override
    public TagDTO updateTag(String id, @Valid CreateTagDTO updateTagDTO) {
        Tag existingTag = tagRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Tag not found."));

        if (!existingTag.getName().equals(updateTagDTO.name()) &&
                tagRepository.existsByName(updateTagDTO.name())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Tag with this name already exists.");
        }

        existingTag.setName(updateTagDTO.name());
        existingTag.setColor(updateTagDTO.color());
        existingTag.setDescription(updateTagDTO.description());
        existingTag.setCategory(updateTagDTO.category());

        return toDTO(tagRepository.save(existingTag));
    }

    @Override
    public void deleteTag(String id) {
        if (tagRepository.existsById(id)) {
            tagRepository.deleteById(id);
        }
    }

    @Override
    public Page<TagDTO> findTagsByNameContaining(String name, Pageable pageable) {
        return tagRepository.findByNameContainingIgnoreCase(name, pageable)
                .map(this::toDTO);
    }

    @Override
    public List<TagDTO> getTagsByNames(List<String> names) {
        return tagRepository.findByNameIn(names).stream()
                .map(this::toDTO)
                .toList();
    }

    @Override
    public Page<TagDTO> getTagsByCategory(String category, Pageable pageable) {
        return tagRepository.findByCategory(category, pageable)
                .map(this::toDTO);
    }

    @Override
    public void incrementUsageCount(String tagId) {
        Tag tag = tagRepository.findById(tagId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Tag not found."));

        tag.setUsageCount(tag.getUsageCount() + 1);
        tagRepository.save(tag);
    }

    @Override
    public void decrementUsageCount(String tagId) {
        Tag tag = tagRepository.findById(tagId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Tag not found."));

        if (tag.getUsageCount() > 0) {
            tag.setUsageCount(tag.getUsageCount() - 1);
            tagRepository.save(tag);
        }
    }

    @Override
    public List<TagDTO> getPopularTags(int limit) {
        Pageable pageable = PageRequest.of(0, limit, Sort.by(Sort.Direction.DESC, "usageCount"));
        return tagRepository.findAll(pageable).stream()
                .map(this::toDTO)
                .toList();
    }

    private TagDTO toDTO(Tag tag) {
        return new TagDTO(
                tag.getId(),
                tag.getName(),
                tag.getColor(),
                tag.getDescription(),
                tag.getCategory(),
                tag.getUsageCount()
        );
    }

    private Tag toEntity(CreateTagDTO dto) {
        return new Tag(
                dto.name(),
                dto.color(),
                dto.description(),
                dto.category()
        );
    }
}