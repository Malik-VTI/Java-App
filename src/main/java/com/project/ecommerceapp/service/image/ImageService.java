package com.project.ecommerceapp.service.image;

import com.project.ecommerceapp.dto.ImageDto;
import com.project.ecommerceapp.model.Image;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

public interface ImageService {
    Image getImageById(Long id);
    void deleteImageById(Long id);
    List<ImageDto> saveImages(Long productId, List<MultipartFile> files);
    void updateImage (MultipartFile file, Long imageId);
}