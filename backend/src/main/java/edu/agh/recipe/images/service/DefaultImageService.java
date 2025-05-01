package edu.agh.recipe.images.service;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.client.gridfs.model.GridFSFile;
import edu.agh.recipe.images.dto.ImageDTO;
import edu.agh.recipe.images.dto.ImageMetadataDTO;
import org.bson.Document;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Service
public class DefaultImageService implements ImageService {

    private final GridFsTemplate gridFsTemplate;
    private final MongoTemplate mongoTemplate;

    public DefaultImageService(GridFsTemplate gridFsTemplate, MongoTemplate mongoTemplate) {
        this.gridFsTemplate = Objects.requireNonNull(gridFsTemplate);
        this.mongoTemplate = Objects.requireNonNull(mongoTemplate);
    }

    public String uploadImage(MultipartFile image, ImageMetadataDTO imageMetadataDTO){
        DBObject metadata = toDBObject(imageMetadataDTO);
        try (InputStream inputStream = image.getInputStream()){
            return gridFsTemplate.store(inputStream, image.getOriginalFilename(), image.getContentType(), metadata).toString();
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Couldn't upload image");
        }
    }

    public InputStreamResource getImage(String id) {
        GridFSFile file = gridFsTemplate.findOne(new Query(Criteria.where("_id").is(id)));

        if (file == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "File not found");
        }

        GridFsResource gridFsResource = gridFsTemplate.getResource(file);

        try {
            return new InputStreamResource(gridFsResource.getInputStream());
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Couldn't retrieve image");
        }
    }

    public ImageDTO getImageDataById(String id) {
        GridFSFile file = gridFsTemplate.findOne(new Query(Criteria.where("_id").is(id)));

        if (file == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "File not found");
        }

        return toDTO(file);
    }

    public List<ImageDTO> getImagesDataByIds(List<String> imageIds) {
        return imageIds.stream()
                .map(this::getImageDataById)
                .toList();
    }

    public void removeImageById(String id) {
        GridFSFile file = gridFsTemplate.findOne(new Query(Criteria.where("_id").is(id)));

        if (file == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Image not found");
        }

        gridFsTemplate.delete(Query.query(Criteria.where("_id").is(id)));
    }

    public void setImagePrimary(String id, boolean newPrimary) {
        GridFSFile file = gridFsTemplate.findOne(new Query(Criteria.where("_id").is(id)));

        if (file == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "File not found");
        }

        Update update = new Update().set("metadata.isPrimary", newPrimary);
        mongoTemplate.updateFirst(
                Query.query(Criteria.where("_id").is(id)),
                update,
                "fs.files"
        );
    }

    private DBObject toDBObject(ImageMetadataDTO imageMetadataDTO) {
        DBObject metadata = new BasicDBObject();
        metadata.put("recipeId", imageMetadataDTO.recipeId());
        metadata.put("description", imageMetadataDTO.description());
        metadata.put("isPrimary", imageMetadataDTO.isPrimary());
        metadata.put("uploadDate", imageMetadataDTO.uploadDate());
        return metadata;
    }

    private ImageDTO toDTO(GridFSFile file) {
        Document metadata = file.getMetadata();

        if (metadata == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Image metadata not found");
        }

        GridFsResource gridFsResource = new GridFsResource(file);

        return new ImageDTO(
                file.getId().asObjectId().getValue().toString(),
                gridFsResource.getFilename(),
                metadata.getString("_contentType") != null ? metadata.getString("_contentType") : "Unknown",
                metadata.getString("description") != null ? metadata.getString("description") : "Unknown",
                metadata.getBoolean("isPrimary") != null ? metadata.getBoolean("isPrimary") : false,
                metadata.getDate("uploadDate") != null ? metadata.getDate("uploadDate") : new Date()
        );
    }
}
