package com.project.ecommerceapp.controller;

import com.project.ecommerceapp.dto.ProductDto;
import com.project.ecommerceapp.model.Product;
import com.project.ecommerceapp.service.product.ProductService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProductController.class)
public class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService productService;

    @Test
    void getProducts_shouldReturnEmptyList_whenNoProducts() throws Exception {
        Mockito.when(productService.getAllProduct()).thenReturn(Collections.emptyList());
        Mockito.when(productService.getListProductDto(any())).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/v1/product/"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("No products available"))
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    void getProducts_shouldReturnProductList_whenProductsExist() throws Exception {
        Product product = new Product();
        ProductDto productDto = new ProductDto();
        List<Product> products = List.of(product);
        List<ProductDto> productDtos = List.of(productDto);

        Mockito.when(productService.getAllProduct()).thenReturn(products);
        Mockito.when(productService.getListProductDto(products)).thenReturn(productDtos);

        mockMvc.perform(get("/api/v1/product/"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Product:"))
                .andExpect(jsonPath("$.data").isArray());
    }
}
