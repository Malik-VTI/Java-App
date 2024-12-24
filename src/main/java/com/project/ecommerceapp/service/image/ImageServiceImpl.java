package com.project.ecommerceapp.service.image;

import com.project.ecommerceapp.dto.ImageDto;
import com.project.ecommerceapp.exceptions.ResourceException;
import com.project.ecommerceapp.model.Image;
import com.project.ecommerceapp.model.Product;
import com.project.ecommerceapp.repository.ImageRepository;
import com.project.ecommerceapp.service.product.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.sql.rowset.serial.SerialBlob;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/*
    This class is a service implementation that provides methods for managing images associated with products.
    Using ImageRepository class to interact with the image database and the ProductService class to retrieve product information.
*/
@Service
@RequiredArgsConstructor
public class ImageServiceImpl implements ImageService{
    private final ImageRepository imageRepository;
    private final ProductService productService;

    /*
        - Retrieves image by id
        - id : The id of the image to retrieve
        - Returns image with specified id and will throw exception if image not found
    */
    @Override
    public Image getImageById(Long id) {
        return imageRepository.findById(id)
                .orElseThrow(() -> new ResourceException("No image found with id: " + id));
    }

    /*
        - Delete image by his id
        - id : The id of the image to retrieve and will deleted
        - Will throw exception if image not found
    */
    @Override
    public void deleteImageById(Long id) {
        imageRepository.findById(id).ifPresentOrElse(imageRepository::delete, () -> {
            throw new ResourceException("No image found with id: "+ id);
        });
    }

    /*
        - Save list of image from the product
        - productId : id from product who associated with the images.
        - file : A list of MultipartFile objects representing the images to save.
        - Return a list of ImageDto objects representing the saved images.
        - Throw error exception if an error appears while saving the images.
    */
    @Override
    public List<ImageDto> saveImages(Long productId, List<MultipartFile> files) {
        Product product = productService.getProductById(productId);

        List<ImageDto> saveImagedDto = new ArrayList<>();
        for (MultipartFile file : files) {
            try {
                Image image = new Image();
                image.setFileName(file.getOriginalFilename());
                image.setFileType(file.getContentType());
                image.setImage(new SerialBlob(file.getBytes()));
                image.setProduct(product);

                String buildDownloadUrl = "api/v1/images/image/download";
                String downloadUrl = buildDownloadUrl+image.getId();
                image.setDownloadUrl(downloadUrl);
                Image savedImage = imageRepository.save(image);

                savedImage.setDownloadUrl(buildDownloadUrl+savedImage.getId());
                imageRepository.save(savedImage);

                ImageDto imageDto = new ImageDto();
                imageDto.setId(savedImage.getId());
                imageDto.setFileName(savedImage.getFileName());
                imageDto.setDownloadUrl(savedImage.getDownloadUrl());
                saveImagedDto.add(imageDto);
            } catch (IOException | SQLException e) {
                throw new RuntimeException(e.getMessage());
            }
        }
        return saveImagedDto;
    }

    /*
        - Updating existing image.
        - file : Updated image file.
        - imageId : The ID of the image to update.
        - Throw error exception if errors appear while updating images.
    */
    @Override
    public void updateImage(MultipartFile file, Long imageId) {
        Image image = getImageById(imageId);
        try {
            image.setFileName(file.getOriginalFilename());
            image.setFileType(file.getContentType());
            image.setImage(new SerialBlob(file.getBytes()));
            imageRepository.save(image);
        } catch (IOException | SQLException e){
            throw new RuntimeException(e.getMessage());
        }
    }
}
