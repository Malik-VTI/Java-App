package com.project.ecommerceapp.service.product;
import com.project.ecommerceapp.controller.ProductController;
import com.project.ecommerceapp.dto.ProductDto;
import com.project.ecommerceapp.exceptions.ResourceException;
import com.project.ecommerceapp.mapper.ProductMapper;
import com.project.ecommerceapp.model.Category;
import com.project.ecommerceapp.model.Product;
import com.project.ecommerceapp.repository.CategoryRepository;
import com.project.ecommerceapp.repository.ProductRepository;
import com.project.ecommerceapp.request.AddProductRequest;
import com.project.ecommerceapp.request.UpdateProductRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.apache.log4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/*
    This class is a service implementation that provides logic methods for managing products.
    Using ProductRepository class to interact with database and using CategoryRepository class for retrieve the category information.
*/
@Service
@CacheConfig(cacheNames = "product")
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService{
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private static final Logger logger = Logger.getLogger(ProductServiceImpl.class);

    /*
        - Added new product
        - request : Object from AddProductRequest who contain the product details who will use.
        - Return new product / save new product.
    */
    @Override
    @CachePut(key = "#result.id")
    public Product addProduct(AddProductRequest request) {
        // check the category in the database
        // set the product if found the category
        // set new category, if we can't found category
        logger.info("Adding new product");
        Category category = Optional.ofNullable(categoryRepository.findByName(request.getCategory().getName()))
                .orElseGet(() -> {
                    logger.info("Category not found, creating new category");
                    Category newCategory = new Category(request.getCategory().getName());
                    return categoryRepository.save(newCategory);
                });
        request.setCategory(category);
        logger.info("Product added successfully");
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
    @Cacheable(key = "#id")
    public Product getProductById(Long id) {
        logger.info("Fetching product by id: " + id);
        return productRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Product not found with id: " + id);
                    throw new ResourceException("Product Not Found");
                });
    }

    /*
        - Deletes product by his id.
        - id : Param id from product selected.
        - Throw exception message if the product with selected id not found.
    */
    @Override
    @CacheEvict(key = "#id")
    public void deleteProductById(Long id) {
        logger.info("Delete product by id: " + id);
        productRepository.findById(id)
                .ifPresentOrElse(product -> {
                    productRepository.delete(product);
                    logger.info("Product deleted successfully with id: " + id);
                }, () -> {
                    logger.error("Product not found with id: " + id);
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
    @CachePut(key = "#productId")
    public Product updateProduct(UpdateProductRequest request, Long productId) {
        logger.info("Update product with id: " + productId);
        return productRepository.findById(productId)
                .map(product -> {
                    logger.info("Product found with id: " + productId);
                    return updateExistingProduct(product, request);
                })
                .map(updatedProduct -> {
                    Product savedProduct = productRepository.save(updatedProduct);
                    productRepository.flush();
                    logger.info("Product updated successfully with id: " + savedProduct.getId());
                    return savedProduct;
                })
                .orElseThrow(() -> {
                    logger.error("Product not found with id: " + productId);
                    return new ResourceException("Product Not Found");
                });
    }
    private Product updateExistingProduct(Product product, UpdateProductRequest request){
        logger.info("Updating product details for id: " + product.getId());
        product.setName(request.getName());
        product.setBrand(request.getBrand());
        product.setPrice(request.getPrice());
        product.setInventory(request.getInventory());
        product.setDescription(request.getDescription());

        logger.info("Fetching category: " + request.getCategory().getName());
        Category category = categoryRepository.findByName(request.getCategory().getName());
        if (category == null) {
            logger.error("Category not found: " + request.getCategory().getName());
            throw new ResourceException("Category not found");
        }
        product.setCategory(category);
        return product;
    }

    /*
        - Retrieves all products.
        - Return list of products.
    */
    @Override
    @Cacheable(cacheNames = "allProducts")
    public List<Product> getAllProduct() {
        logger.info("Fetching all products");
        return productRepository.findAll();
    }

    /*
        - Retrieves all products by category.
        - category : Param for category name to filter by.
        - Returns list of products with that category.
    */
    @Override
    @Cacheable(cacheNames = "productsByCategory", key = "#category")
    public List<Product> getProductsByCategory(String category) {
        logger.info("Fetching products by category: " + category);
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
