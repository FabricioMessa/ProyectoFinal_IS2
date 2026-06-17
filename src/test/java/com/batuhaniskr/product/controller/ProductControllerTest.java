package com.batuhaniskr.product.controller;

import com.batuhaniskr.product.model.Product;
import com.batuhaniskr.product.service.ProductService;
import com.batuhaniskr.product.service.UserService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductController.class)
public class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService productService;

    @MockBean
    private UserService userService; // Requerido para levantar el contexto de seguridad

    @Test
    @WithMockUser(username = "samir@test.com")
    public void testGetAllProduct_Success() throws Exception {
        // 1. ARRANGE (Preparar los datos de simulación)
        Product p1 = new Product();
        p1.setName("Laptop");
        Product p2 = new Product();
        p2.setName("Mouse");
        List<Product> mockList = Arrays.asList(p1, p2);

        Mockito.when(productService.getAllProduct("samir@test.com")).thenReturn(mockList);

        // 2. ACT & 3. ASSERT (Ejecutar y Verificar el resultado HTTP)
        mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].name").value("Laptop"))
                .andExpect(jsonPath("$[1].name").value("Mouse"));
    }

    @Test
    @WithMockUser(username = "samir@test.com")
    public void testGetProductById_Success() throws Exception {
        // 1. ARRANGE
        Product product = new Product();
        product.setId(1);
        product.setName("Teclado Mek");

        Mockito.when(productService.getProductById(1)).thenReturn(product);

        // 2. ACT & 3. ASSERT
        mockMvc.perform(get("/api/products/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Teclado Mek"));
    }
}