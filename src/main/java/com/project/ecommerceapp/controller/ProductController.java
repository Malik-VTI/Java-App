package com.project.ecommerceapp.controller;

import com.project.ecommerceapp.dto.ProductDto;
import com.project.ecommerceapp.exceptions.ResourceException;
import com.project.ecommerceapp.model.Product;
import com.project.ecommerceapp.request.AddProductRequest;
import com.project.ecommerceapp.response.ApiResponse;
import com.project.ecommerceapp.service.product.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@RequiredArgsConstructor
@RestController
@RequestMapping("${api.prefix}/product")
public class ProductController {
    private final ProductService productService;

    @GetMapping("/")
    public ResponseEntity<ApiResponse> getProducts(){
        List<Product>products = productService.getAllProduct();
        List<ProductDto> dataProduct = productService.getListProductDto(products);
        if (products.isEmpty()) {
            return ResponseEntity.ok(new ApiResponse("No products available", Collections.emptyList()));
        }
        return ResponseEntity.ok(new ApiResponse("Product:", dataProduct));
    }

    @GetMapping("/id/{productId}")
    public ResponseEntity<ApiResponse> getProductById(@PathVariable Long productId){
        try {
            Product product = productService.getProductById(productId);
            ProductDto productDto = productService.getProductDto(product);
            return ResponseEntity.ok(new ApiResponse("Product: ", productDto));
        } catch (ResourceException e) {
            return ResponseEntity.status(NOT_FOUND).body(new ApiResponse("Error:", e.getMessage()));
        }
    }

    @PostMapping("/")
    public ResponseEntity<ApiResponse> addProduct(@RequestBody AddProductRequest request){
        try {
            Product newProduct = productService.addProduct(request);
            ProductDto productDto = productService.getProductDto(newProduct);
            return ResponseEntity.ok(new ApiResponse("Successfull add products", productDto));
        } catch (Exception e) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(new ApiResponse(e.getMessage(), null));
        }
    }

    @PutMapping("/{productId}/update")
    public ResponseEntity<ApiResponse> updateProduct(@RequestBody AddProductRequest request, @PathVariable Long productId){
        try {
            Product product = productService.getProductById(productId);
            ProductDto productDto = productService.getProductDto(product);
            return ResponseEntity.ok(new ApiResponse("Product updated", productDto));
        } catch (ResourceException e){
            return ResponseEntity.status(NOT_FOUND).body(new ApiResponse("Update failed", null));
        }
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<ApiResponse> deleteProduct(@PathVariable Long productId){
        try {
            productService.deleteProductById(productId);
            return ResponseEntity.ok(new ApiResponse("Message: ", "Product deleted"));
        } catch (ResourceException e) {
            return ResponseEntity.status(NOT_FOUND).body(new ApiResponse(e.getMessage(), null));
        }
    }

    @GetMapping("/brand-and-name")
    public ResponseEntity<ApiResponse> getProductsByBrandAndName(@RequestParam String brandName, @RequestParam String productName){
        try {
            List<Product> products = productService.getProductsByBrandAndName(brandName, productName);
            List<ProductDto> dataProduct = productService.getListProductDto(products);
            if (products.isEmpty()) {
                return ResponseEntity.status(NOT_FOUND).body(new ApiResponse("Product not found", null));
            }
            return ResponseEntity.ok(new ApiResponse("Data:", dataProduct));
        } catch (Exception e){
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(new ApiResponse(e.getMessage(), null));
        }
    }

    @GetMapping("/category-and-brand")
    public ResponseEntity<ApiResponse> getProductsByCategoryAndBrand(@RequestParam String category, @RequestParam String brandName){
        try {
            List<Product> products = productService.getProductsByCategoryAndBrand(category, brandName);
            List<ProductDto> dataProduct = productService.getListProductDto(products);
            if (products.isEmpty()) {
                return ResponseEntity.status(NOT_FOUND).body(new ApiResponse("Product not found", null));
            }
            return ResponseEntity.ok(new ApiResponse("Data:", dataProduct));
        } catch (Exception e){
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(new ApiResponse(e.getMessage(), null));
        }
    }

    @GetMapping("/{name}")
    public ResponseEntity<ApiResponse> getProductsByName(@PathVariable String name){
        try {
            List<Product> products = productService.getProductsByName(name);
            List<ProductDto> dataProduct = productService.getListProductDto(products);
            if (products.isEmpty()){
                return ResponseEntity.status(NOT_FOUND).body(new ApiResponse("Products with name " + name + " not found", null));
            }
            return ResponseEntity.ok(new ApiResponse("Data:", dataProduct));
        } catch (Exception e){
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(new ApiResponse(e.getMessage(), null));
        }
    }

    @GetMapping("/brand")
    public ResponseEntity<ApiResponse> getProductsByBrand(@RequestParam String brand){
        try {
            List<Product> products = productService.getProductsByBrand(brand);
            List<ProductDto> dataProduct = productService.getListProductDto(products);
            if (products.isEmpty()){
                return ResponseEntity.status(NOT_FOUND).body(new ApiResponse("Products with brand " + brand + " not found", null));
            }
            return ResponseEntity.ok(new ApiResponse("Data:", dataProduct));
        } catch (Exception e) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(new ApiResponse(e.getMessage(), null));
        }
    }

    @GetMapping("/{category}")
    public ResponseEntity<ApiResponse> getProductsByCategory(@PathVariable String category){
        try {
            List<Product> products = productService.getProductsByCategory(category);
            List<ProductDto> dataProduct = productService.getListProductDto(products);
            if (products.isEmpty()){
                return ResponseEntity.status(NOT_FOUND).body(new ApiResponse("Products with category " + category + " not found", null));
            }
            return ResponseEntity.ok(new ApiResponse("Data:", dataProduct));
        } catch (Exception e) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(new ApiResponse(e.getMessage(), null));
        }
    }

    @GetMapping("/count")
    public ResponseEntity<ApiResponse> countProductsByBrandAndName(@RequestParam String brand, @RequestParam String name){
        try {
            var productCount = productService.countProductsByBrandAndName(brand, name);
            return ResponseEntity.ok(new ApiResponse("Total products:", productCount));
        } catch (Exception e) {
            return ResponseEntity.ok(new ApiResponse(e.getMessage(), null));
        }
    }
}
