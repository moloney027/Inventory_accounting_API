package com.rnsd.mystorage.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rnsd.mystorage.MyStorageApplication;
import com.rnsd.mystorage.entity.Product;
import com.rnsd.mystorage.model.security.JwtRequestModel;
import com.rnsd.mystorage.model.security.JwtResponseModel;
import com.rnsd.mystorage.repository.ProductRepository;
import com.rnsd.mystorage.service.security.AuthService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = MyStorageApplication.class)
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application-integrationtest.properties")
@Slf4j
class ProductControllerTest {

    @Autowired
    ProductRepository productRepository;
    @Autowired
    MockMvc mvc;
    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    AuthService authService;

    private String  accessToken;

    @BeforeEach
    void setUp() {
        JwtRequestModel jwtRequestModel = new JwtRequestModel();
        jwtRequestModel.setLogin("admin");
        jwtRequestModel.setPassword("admin");
        JwtResponseModel login = authService.login(jwtRequestModel);
        accessToken = login.getAccessToken();
    }

    @Test
    void getAllProducts() throws Exception {
        mvc.perform(get("/products").header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andDo(result -> log.info(result.getResponse().getContentAsString()));
    }

    @Test
    void getProductById() throws Exception {
        Product productForGet = new Product(
                null, "0001", "test product",
                null, null, false
        );
        productForGet = productRepository.save(productForGet);
        mvc.perform(get("/products/{id}", productForGet.getId())
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(productForGet.getId().intValue())))
                .andExpect(jsonPath("$.article", is("0001")))
                .andExpect(jsonPath("$.name", is("test product")))
                .andExpect(jsonPath("$.lastPurchasePrice", nullValue()))
                .andExpect(jsonPath("$.lastSalePrice", nullValue()))
                .andExpect(jsonPath("$.archive", is(false)))
                .andDo(result -> log.info(result.getResponse().getContentAsString()));

        Product productForGet1 = new Product(
                null, "0001", "test product",
                null, null, true
        );
        productForGet1 = productRepository.save(productForGet1);
        mvc.perform(get("/products/{id}", productForGet1.getId()))
                .andExpect(status().is4xxClientError())
                .andDo(result -> log.info(result.getResponse().getContentAsString()));
    }

    @Test
    void createProduct() throws Exception {
        Product product = new Product(
                null, "0001", "New test product",
                null, null, false
        );
        mvc.perform(post("/products")
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(product)))
                .andExpect(status().isOk())
                .andDo(result -> {
                    log.info(result.getResponse().getContentAsString());
                    Product value = objectMapper.readValue(result.getResponse().getContentAsString(), Product.class);
                    productRepository.deleteById(value.getId());
                });

        Product productWithNullArticle = new Product(
                null, null, "New test product",
                null, null, false
        );
        mvc.perform(post("/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productWithNullArticle)))
                .andExpect(status().is4xxClientError())
                .andDo(result -> log.info(result.getResponse().getContentAsString()));
    }

    @Test
    void updateProduct() throws Exception {
        Product productForUpdate = new Product(
                null, "0001", "test product",
                null, null, false
        );
        productForUpdate = productRepository.save(productForUpdate);
        Product product = new Product(
                null, "Update article", "Update product",
                null, null, false
        );
        mvc.perform(put("/products/{id}", productForUpdate.getId())
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(product)))
                .andExpect(status().isOk())
                .andDo(result -> log.info(result.getResponse().getContentAsString()));
    }

    @Test
    void archiveProduct() throws Exception {
        Product productForArchive = new Product(
                null, "005", "test product 5",
                null, null, false
        );
        productForArchive = productRepository.save(productForArchive);
        mvc.perform(put("/products/move-archive/{id}", productForArchive.getId())
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.archive", is(true)))
                .andDo(result -> {
                    log.info(result.getResponse().getContentAsString());
                    Product value = objectMapper.readValue(result.getResponse().getContentAsString(), Product.class);
                    productRepository.deleteById(value.getId());
                });
    }

    @Test
    void deleteProduct() throws Exception {
        Product productForDelete = new Product(
                null, "006", "test product 6",
                null, null, true
        );
        productForDelete = productRepository.save(productForDelete);
        mvc.perform(delete("/products/{id}", productForDelete.getId())
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andDo(result -> log.info(result.getResponse().getContentAsString()));
    }

    @Test
    void checkPriceProduct() throws Exception {
        Product productWithNegativePrice = new Product(
                null, "007", "test product 7",
                BigDecimal.valueOf(-50.776), null, false
        );
        mvc.perform(post("/products")
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productWithNegativePrice)))
                .andExpect(status().is4xxClientError())
                .andDo(result -> log.info(result.getResponse().getContentAsString()));
    }
}