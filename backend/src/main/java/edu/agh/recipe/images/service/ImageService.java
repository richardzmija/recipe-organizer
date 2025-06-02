package edu.agh.recipe.images.service;

import edu.agh.recipe.images.dto.ImageDTO;
import edu.agh.recipe.images.dto.ImageMetadataDTO;
import org.springframework.core.io.InputStreamResource;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ImageService {
    String uploadImage(MultipartFile image, ImageMetadataDTO imageMetadataDTO);
    InputStreamResource getImage(String id);
    ImageDTO getImageDataById(String id);
    List<ImageDTO> getImagesDataByIds(List<String> imageIds);
    void removeImageById(String id);
    void setImagePrimary(String id, boolean newPrimary);
}
