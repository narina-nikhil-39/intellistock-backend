package com.nikhil.IntelliStock.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nikhil.IntelliStock.dto.ProductRequestDTO;
import com.nikhil.IntelliStock.service.ProductService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ProductSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ProductService productService;

    @Test
    @DisplayName("Unauthenticated request to create product must return 401 Unauthorized")
    void createProduct_Unauthenticated_ReturnsUnauthorized() throws Exception {
        ProductRequestDTO request = new ProductRequestDTO();
        request.setSku("UNAUTH-SKU");
        request.setName("Denied Product");
        request.setCategory("Test");
        request.setInitialStock(10);
        request.setSafetyStockLevel(5);

        mockMvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "staff_user", roles = {"STAFF"})
    @DisplayName("Staff user attempting to delete a product must return 403 Forbidden")
    void deleteProduct_AsStaff_ReturnsForbidden() throws Exception {
        mockMvc.perform(delete("/api/products/1"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "staff_user", roles = {"STAFF"})
    @DisplayName("Staff user can read inventory without administrative privileges")
    void getAllProducts_AsStaff_ReturnsOk() throws Exception {
        mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk());
    }
}
