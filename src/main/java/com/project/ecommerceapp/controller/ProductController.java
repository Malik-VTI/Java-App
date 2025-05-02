package com.project.ecommerceapp.controller;

import com.project.ecommerceapp.dto.ProductDto;
import com.project.ecommerceapp.exceptions.ResourceException;
import com.project.ecommerceapp.model.Product;
import com.project.ecommerceapp.request.AddProductRequest;
import com.project.ecommerceapp.response.ApiResponse;
import com.project.ecommerceapp.service.product.ProductService;
import datadog.trace.api.CorrelationIdentifier;
import lombok.RequiredArgsConstructor;
import org.apache.log4j.Logger;
import org.apache.logging.log4j.ThreadContext;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
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
    private static final Logger logger = Logger.getLogger(ProductController.class);

    @GetMapping("/")
    public ResponseEntity<ApiResponse> getProducts(){
        try {
            ThreadContext.put("dd.trace_id", CorrelationIdentifier.getTraceId());
            ThreadContext.put("dd.span_id", CorrelationIdentifier.getSpanId());
            logger.info("Fetching all products");
        } finally {
            ThreadContext.remove("dd.trace_id");
            ThreadContext.remove("dd.span_id");
        }
        List<Product>products = productService.getAllProduct();
        List<ProductDto> dataProduct = productService.getListProductDto(products);
        if (products.isEmpty()) {
            try {
                ThreadContext.put("dd.trace_id", CorrelationIdentifier.getTraceId());
                ThreadContext.put("dd.span_id", CorrelationIdentifier.getSpanId());
                logger.info("No products available");
            } finally {
                ThreadContext.remove("dd.trace_id");
                ThreadContext.remove("dd.span_id");
            }
            return ResponseEntity.ok(new ApiResponse("No products available", Collections.emptyList()));
        }
        try {
            ThreadContext.put("dd.trace_id", CorrelationIdentifier.getTraceId());
            ThreadContext.put("dd.span_id", CorrelationIdentifier.getSpanId());
            logger.info("Getting all products");
        } finally {
            ThreadContext.remove("dd.trace_id");
            ThreadContext.remove("dd.span_id");
        }
        return ResponseEntity.ok(new ApiResponse("Product:", dataProduct));
    }

    @GetMapping("/id/{productId}")
    public ResponseEntity<ApiResponse> getProductById(@PathVariable Long productId){
        try {
            ThreadContext.put("dd.trace_id", CorrelationIdentifier.getTraceId());
            ThreadContext.put("dd.span_id", CorrelationIdentifier.getSpanId());
            logger.info("Fetching product by id: " + productId);
        } finally {
            ThreadContext.remove("dd.trace_id");
            ThreadContext.remove("dd.span_id");
        }
        try {
            Product product = productService.getProductById(productId);
            ProductDto productDto = productService.getProductDto(product);
            return ResponseEntity.ok(new ApiResponse("Product: ", productDto));
        } catch (ResourceException e) {
            try {
                ThreadContext.put("dd.trace_id", CorrelationIdentifier.getTraceId());
                ThreadContext.put("dd.span_id", CorrelationIdentifier.getSpanId());
                logger.error("Product not found with id: " + productId, e);
            } finally {
                ThreadContext.remove("dd.trace_id");
                ThreadContext.remove("dd.span_id");
            }
            return ResponseEntity.status(NOT_FOUND).body(new ApiResponse("Error:", e.getMessage()));
        }
    }

    @PostMapping("/")
    public ResponseEntity<ApiResponse> addProduct(@RequestBody AddProductRequest request){
        logger.info("Adding new product");
        try {
            Product newProduct = productService.addProduct(request);
            ProductDto productDto = productService.getProductDto(newProduct);
            return ResponseEntity.ok(new ApiResponse("Successfull add products", productDto));
        } catch (Exception e) {
            logger.error("Failed to add new product", e);
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(new ApiResponse(e.getMessage(), null));
        }
    }

    @PutMapping("/id/{productId}/update")
    public ResponseEntity<ApiResponse> updateProduct(@RequestBody AddProductRequest request, @PathVariable Long productId){
        logger.info("Updating product by id: " + productId);
        try {
            Product product = productService.getProductById(productId);
            ProductDto productDto = productService.getProductDto(product);
            return ResponseEntity.ok(new ApiResponse("Product updated", productDto));
        } catch (ResourceException e){
            logger.error("Failed to update product with id: " + productId, e);
            return ResponseEntity.status(NOT_FOUND).body(new ApiResponse("Update failed", null));
        }
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<ApiResponse> deleteProduct(@PathVariable Long productId){
        logger.info("Delete product by id: " + productId);
        try {
            productService.deleteProductById(productId);
            return ResponseEntity.ok(new ApiResponse("Message: ", "Product deleted"));
        } catch (ResourceException e) {
            logger.error("Failed to delete product with id: " + productId, e);
            return ResponseEntity.status(NOT_FOUND).body(new ApiResponse(e.getMessage(), null));
        }
    }

    @GetMapping("/brand-and-name")
    public ResponseEntity<ApiResponse> getProductsByBrandAndName(@RequestParam String brandName, @RequestParam String productName){
        logger.info("Fetching products by brand: " + brandName + " and name: " + productName);
        try {
            List<Product> products = productService.getProductsByBrandAndName(brandName, productName);
            List<ProductDto> dataProduct = productService.getListProductDto(products);
            if (products.isEmpty()) {
                logger.info("No products found for brand: " + brandName + " and name: " + productName);
                return ResponseEntity.status(NOT_FOUND).body(new ApiResponse("Product not found", null));
            }
            return ResponseEntity.ok(new ApiResponse("Data:", dataProduct));
        } catch (Exception e){
            logger.error("Failed to fetch products by brand and name", e);
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(new ApiResponse(e.getMessage(), null));
        }
    }

    @GetMapping("/category-and-brand")
    public ResponseEntity<ApiResponse> getProductsByCategoryAndBrand(@RequestParam String category, @RequestParam String brandName){
        logger.info("Get product by category dan brand name");
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

    @GetMapping("/name")
    public ResponseEntity<ApiResponse> getProductsByName(@RequestParam String name){
        logger.info("Get product by name");
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
        logger.info("Get product by brand");
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

    @GetMapping("/category")
    public ResponseEntity<ApiResponse> getProductsByCategory(@RequestParam String category){
        logger.info("Get product by category");
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
