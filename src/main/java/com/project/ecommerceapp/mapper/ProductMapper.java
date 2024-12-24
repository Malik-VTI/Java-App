package com.project.ecommerceapp.mapper;

import com.project.ecommerceapp.dto.ProductDto;
import com.project.ecommerceapp.model.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface ProductMapper {

    ProductMapper INSTANCE = Mappers.getMapper(ProductMapper.class);

    @Mapping(source = "category.name", target = "category")
    ProductDto producToProductDto(Product product);

    List<ProductDto> productlistToProductDto(List<Product> products);
}
