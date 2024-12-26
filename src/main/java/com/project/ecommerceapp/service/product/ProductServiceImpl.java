package com.project.ecommerceapp.service.product;
import com.project.ecommerceapp.dto.ProductDto;
import com.project.ecommerceapp.exceptions.ResourceException;
import com.project.ecommerceapp.mapper.ProductMapper;
import com.project.ecommerceapp.model.Category;
import com.project.ecommerceapp.model.Product;
import com.project.ecommerceapp.repository.CategoryRepository;
import com.project.ecommerceapp.repository.ProductRepository;
import com.project.ecommerceapp.request.AddProductRequest;
import com.project.ecommerceapp.request.UpdateProductRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/*
    This class is a service implementation that provides logic methods for managing products.
    Using ProductRepository class to interact with database and using CategoryRepository class for retrieve the category information.
*/
@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService{
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    /*
        - Added new product
        - request : Object from AddProductRequest who contain the product details who will use.
        - Return new product / save new product.
    */
    @Override
    public Product addProduct(AddProductRequest request) {
        // check the category in the database
        // set the product if found the category
        // set new category, if we can't found category
        Category category = Optional.ofNullable(categoryRepository.findByName(request.getCategory().getName()))
                .orElseGet(() -> {
                    Category newCategory = new Category(request.getCategory().getName());
                    return categoryRepository.save(newCategory);
                });
        request.setCategory(category);
        return productRepository.save(createProduct(request, category));
    }
    private Product createProduct (AddProductRequest request, Category category){
        return new Product(
                request.getName(),
                request.getBrand(),
                request.getPrice(),
                request.getInventory(),
                request.getDescription(),
                category
        );
    }

    /*
        - Retrieves product by his id.
        - id : Param id from the product selected to retrieve.
        - Return product with the id from the request and will throw exception message if the product id not found.
    */
    @Override
    public Product getProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResourceException("Product Not Found"));
    }

    /*
        - Deletes product by his id.
        - id : Param id from product selected.
        - Throw exception message if the product with selected id not found.
    */
    @Override
    public void deleteProductById(Long id) {
        productRepository.findById(id)
                .ifPresentOrElse(productRepository::delete, () -> {
                    throw new ResourceException("Product Not Found");
                });
    }

    /*
        - Update existing product.
        - request   : Object from AddProductRequest who contain the product details who will use.
        - productId : Param from product id selected for update.
        - Throw exception message if product with id selected not found.
    */
    @Override
    public Product updateProduct(UpdateProductRequest request, Long productId) {
        return productRepository.findById(productId)
                .map(product -> updateExistingProduct(product, request))
                .map(productRepository::save)
                .orElseThrow(() -> new ResourceException("Product Not Found"));
    }
    private Product updateExistingProduct(Product product, UpdateProductRequest request){
        product.setName(request.getName());
        product.setBrand(request.getBrand());
        product.setPrice(request.getPrice());
        product.setInventory(request.getInventory());
        product.setDescription(request.getDescription());

        Category category = categoryRepository.findByName(request.getCategory().getName());
        product.setCategory(category);
        return product;
    }

    /*
        - Retrieves all products.
        - Return list of products.
    */
    @Override
    public List<Product> getAllProduct() {
        return productRepository.findAll();
    }

    /*
        - Retrieves all products by category.
        - category : Param for category name to filter by.
        - Returns list of products with that category.
    */
    @Override
    public List<Product> getProductsByCategory(String category) {
        return productRepository.findByCategoryName(category);
    }

    /*
        - Retrieves all products by brand name.
        - brand : Param for brand name to filter by.
        - Returns list of products with that brand.
    */
    @Override
    public List<Product> getProductsByBrand(String brand) {
        return productRepository.findByBrand(brand);
    }

    /*
        - Retrieves all products by category and brand name.
        - category : Param for category name to filter by.
        - brand    : Param for brand name to filter by.
        - Returns list of products with that category and brand.
    */
    @Override
    public List<Product> getProductsByCategoryAndBrand(String category, String brand) {
        return productRepository.findByCategoryNameAndBrand(category, brand);
    }

    /*
        - Retrieves all products by his name.
        - name : Param for product name to filter by.
        - Returns list of products with that product name.
    */
    @Override
    public List<Product> getProductsByName(String name) {
        return productRepository.findByName(name);
    }

    /*
        - Retrieves all products by brand and name of product.
        - brand    : Param for brand name to filter by.
        - name     : Param for product name to filter by.
        - Returns list of products with that brand name and name of product.
    */
    @Override
    public List<Product> getProductsByBrandAndName(String brand, String name) {
        return productRepository.findByBrandAndName(brand, name);
    }

    /*
        - Counts products by brand and name of product.
        - brand    : Param for brand name to filter by.
        - name     : Param for product name to filter by.
        - Returns the count of product belonging that specific order.
    */
    @Override
    public Long countProductsByBrandAndName(String brand, String name) {
        return productRepository.countByBrandAndName(brand, name);
    }

    @Override
    public ProductDto getProductDto(Product product) {
        return ProductMapper.INSTANCE.producToProductDto(product);
    }

    @Override
    public List<ProductDto> getListProductDto(List<Product> products) {
        return ProductMapper.INSTANCE.productListToProductDto(products);
    }

}
