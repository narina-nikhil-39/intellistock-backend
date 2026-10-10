package com.nikhil.IntelliStock.service;

import com.nikhil.IntelliStock.dto.ProductRequestDTO;
import com.nikhil.IntelliStock.dto.ProductResponseDTO;
import com.nikhil.IntelliStock.entity.Product;
import com.nikhil.IntelliStock.exception.ResourceNotFoundException;
import com.nikhil.IntelliStock.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    private Product testProduct;

    @BeforeEach
    void setUp() {
        testProduct = new Product(1L, "TEST-SKU", "Widget A", "Electronics", 20, 8, 10);
    }

    @Test
    @DisplayName("Should create a product with default category and initial stock")
    void createProduct_MissingCategory_UsesGeneralAndInitialStock() {
        ProductRequestDTO request = new ProductRequestDTO();
        request.setSku("NEW-SKU");
        request.setName("New Product");
        request.setInitialStock(12);
        request.setSafetyStockLevel(5);

        when(productRepository.existsBySku("NEW-SKU")).thenReturn(false);
        when(productRepository.save(any(Product.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ProductResponseDTO response = productService.createProduct(request);

        assertEquals("NEW-SKU", response.getSku());
        assertEquals("General", response.getCategory());
        assertEquals(12, response.getInitialStock());
        assertEquals(12, response.getCurrentStock());
        verify(productRepository).save(argThat(product ->
                "General".equals(product.getCategory())
                        && product.getInitialStock() == 12
                        && product.getCurrentStock() == 12
        ));
    }

    @Test
    @DisplayName("Should reject products with an existing SKU")
    void createProduct_DuplicateSku_ThrowsIllegalArgumentException() {
        ProductRequestDTO request = new ProductRequestDTO();
        request.setSku("TEST-SKU");

        when(productRepository.existsBySku("TEST-SKU")).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> productService.createProduct(request));
        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    @DisplayName("Should recommend replenishment for products at or below safety stock")
    void mapToResponse_AtSafetyStock_RecommendsQuantityToDoubleSafetyLevel() {
        testProduct.setCurrentStock(10);

        ProductResponseDTO response = productService.mapToResponse(testProduct);

        assertTrue(response.getIsReorderRecommended());
        assertEquals(10, response.getRecommendedReorderQuantity());
    }

    @Test
    @DisplayName("Should not recommend replenishment for end-of-life products")
    void mapToResponse_EndOfLife_DoesNotRecommendReorder() {
        testProduct.setCategory("End of Life");
        testProduct.setCurrentStock(0);

        ProductResponseDTO response = productService.mapToResponse(testProduct);

        assertFalse(response.getIsReorderRecommended());
        assertEquals(0, response.getRecommendedReorderQuantity());
    }

    @Test
    @DisplayName("Should fail to mark a missing product as end of life")
    void setProductEndOfLife_MissingProduct_ThrowsNotFound() {
        when(productRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> productService.setProductEndOfLife(99L));
    }
}
