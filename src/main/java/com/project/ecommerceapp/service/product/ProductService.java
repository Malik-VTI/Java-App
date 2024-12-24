package com.project.ecommerceapp.service.product;

import com.project.ecommerceapp.dto.ProductDto;
import com.project.ecommerceapp.model.Product;
import com.project.ecommerceapp.request.AddProductRequest;
import com.project.ecommerceapp.request.UpdateProductRequest;

import java.util.List;

public interface ProductService {
    Product addProduct(AddProductRequest product);
    Product getProductById(Long id);
    void deleteProductById(Long id);
    Product updateProduct(UpdateProductRequest product, Long productId);
    List<Product> getAllProduct();
    List<Product> getProductsByCategory(String  category);
    List<Product> getProductsByBrand(String brand);
    List<Product> getProductsByCategoryAndBrand(String category, String brand);
    List<Product> getProductsByName(String name);
    List<Product> getProductsByBrandAndName(String brand, String name);
    Long countProductsByBrandAndName(String brand, String name);

    ProductDto getProductDto(Product product);
    List<ProductDto> getListProductDto(List<Product> products);
}
