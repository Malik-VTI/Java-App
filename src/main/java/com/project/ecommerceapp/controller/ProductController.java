package com.project.ecommerceapp.controller;

import com.project.ecommerceapp.dto.ProductDto;
import com.project.ecommerceapp.exceptions.ResourceException;
import com.project.ecommerceapp.model.Product;
import com.project.ecommerceapp.request.AddProductRequest;
import com.project.ecommerceapp.response.ApiResponse;
import com.project.ecommerceapp.service.product.ProductService;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.ThreadContext;
import org.slf4j.MDC;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Collections;
import java.util.List;
import datadog.trace.api.CorrelationIdentifier;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@RequiredArgsConstructor
@RestController
@RequestMapping("${api.prefix}/product")
public class ProductController {
    private final ProductService productService;
    private static final Logger logger = LoggerFactory.getLogger(ProductController.class);

    @GetMapping("/")
    public ResponseEntity<ApiResponse> getProducts(){
        List<Product>products = productService.getAllProduct();
        List<ProductDto> dataProduct = productService.getListProductDto(products);
        if (products.isEmpty()) {
            return ResponseEntity.ok(new ApiResponse("No products available", Collections.emptyList()));
        }
        try {
            MDC.put("dd.trace_id", CorrelationIdentifier.getTraceId());
            MDC.put("dd.span_id", CorrelationIdentifier.getSpanId());

            logger.info("Getting all products");
        } finally {
            MDC.remove("dd.trace_id");
            MDC.remove("dd.span_id");
        }
        return ResponseEntity.ok(new ApiResponse("Product:", dataProduct));
    }

    @GetMapping("/id/{productId}")
    public ResponseEntity<ApiResponse> getProductById(@PathVariable Long productId){
        try {
            Product product = productService.getProductById(productId);
            ProductDto productDto = productService.getProductDto(product);
            try {
                MDC.put("dd.trace_id", CorrelationIdentifier.getTraceId());
                MDC.put("dd.span_id", CorrelationIdentifier.getSpanId());

                logger.info("Getting product by id");

            } finally {
                MDC.remove("dd.trace_id");
                MDC.remove("dd.span_id");
            }
            return ResponseEntity.ok(new ApiResponse("Product: ", productDto));
        } catch (ResourceException e) {
            try {
                MDC.put("dd.trace_id", CorrelationIdentifier.getTraceId());
                MDC.put("dd.span_id", CorrelationIdentifier.getSpanId());

                logger.info("Product not found with id: " + productId);

            } finally {
                MDC.remove("dd.trace_id");
                MDC.remove("dd.span_id");
            }
            return ResponseEntity.status(NOT_FOUND).body(new ApiResponse("Error:", e.getMessage()));
        }
    }

    @PostMapping("/")
    public ResponseEntity<ApiResponse> addProduct(@RequestBody AddProductRequest request){
        logger.info("Endpoint for add new product");
        try {
            Product newProduct = productService.addProduct(request);
            ProductDto productDto = productService.getProductDto(newProduct);
            logger.info("Add new product", request);
            return ResponseEntity.ok(new ApiResponse("Successfull add products", productDto));
        } catch (Exception e) {
            logger.info("Can't create new product");
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(new ApiResponse(e.getMessage(), null));
        }
    }

    @PutMapping("/id/{productId}/update")
    public ResponseEntity<ApiResponse> updateProduct(@RequestBody AddProductRequest request, @PathVariable Long productId){
        logger.info("Endpoint for update product by id");
        try {
            Product product = productService.getProductById(productId);
            ProductDto productDto = productService.getProductDto(product);
            logger.info("Update product by id");
            return ResponseEntity.ok(new ApiResponse("Product updated", productDto));
        } catch (ResourceException e){
            return ResponseEntity.status(NOT_FOUND).body(new ApiResponse("Update failed", null));
        }
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<ApiResponse> deleteProduct(@PathVariable Long productId){
        logger.info("Delete product by id");
        try {
            productService.deleteProductById(productId);
            logger.info("Product with id: " + productId + " deleted");
            return ResponseEntity.ok(new ApiResponse("Message: ", "Product deleted"));
        } catch (ResourceException e) {
            return ResponseEntity.status(NOT_FOUND).body(new ApiResponse(e.getMessage(), null));
        }
    }

    @GetMapping("/brand-and-name")
    public ResponseEntity<ApiResponse> getProductsByBrandAndName(@RequestParam String brandName, @RequestParam String productName){
        logger.info("Get product by brand name and product name");
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
