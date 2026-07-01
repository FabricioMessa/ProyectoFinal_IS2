package com.batuhaniskr.product.product.presentation;

import com.batuhaniskr.product.product.application.ProductService;
import com.batuhaniskr.product.product.domain.ProductDTO;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ProductApiControllerTest {

    @Mock
    private ProductService productService;

    @Mock
    private Principal principal;

    @InjectMocks
    private ProductApiController controller;

    @Before
    public void setUp() {
        when(principal.getName()).thenReturn("user@test.com");
    }

    @Test
    public void getAllProducts_ShouldReturnPage() {
        // Arrange
        ProductDTO product = new ProductDTO();
        product.setId(1);
        product.setName("Test Product");
        product.setPrice(new BigDecimal("99.99"));
        product.setQuantity(10);

        Page<ProductDTO> page = new PageImpl<>(Arrays.asList(product));
        when(productService.getAllProduct(any(), eq("user@test.com"))).thenReturn(page);

        // Act
        ResponseEntity<Page<ProductDTO>> response = controller.getAllProducts(1, 5, principal);

        // Assert
        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getContent()).hasSize(1);
        assertThat(response.getBody().getContent().get(0).getName()).isEqualTo("Test Product");
    }

    @Test
    public void getProduct_ShouldReturnProduct() {
        // Arrange
        ProductDTO product = new ProductDTO();
        product.setId(1);
        product.setName("Test");
        product.setPrice(new BigDecimal("50.00"));
        product.setQuantity(5);

        when(productService.getProductById(1)).thenReturn(product);

        // Act
        ResponseEntity<ProductDTO> response = controller.getProduct(1);

        // Assert
        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        assertThat(response.getBody().getName()).isEqualTo("Test");
        assertThat(response.getBody().getPrice()).isEqualTo(new BigDecimal("50.00"));
    }

    @Test
    public void createProduct_ShouldCallService() {
        // Arrange
        ProductDTO product = new ProductDTO();
        product.setName("Nuevo Producto");
        product.setPrice(new BigDecimal("25.00"));
        product.setQuantity(3);

        // Act
        ResponseEntity<Void> response = controller.createProduct(product, principal);

        // Assert
        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        verify(productService).saveProduct(product, "user@test.com");
    }

    @Test
    public void deleteProduct_ShouldCallService() {
        // Act
        ResponseEntity<Void> response = controller.deleteProduct(1);

        // Assert
        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        verify(productService).deleteProduct(1);
    }
}
