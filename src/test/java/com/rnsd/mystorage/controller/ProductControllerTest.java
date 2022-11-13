package com.rnsd.mystorage.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rnsd.mystorage.MyStorageApplication;
import com.rnsd.mystorage.entity.Product;
import com.rnsd.mystorage.repository.ProductRepository;
import lombok.extern.slf4j.Slf4j;
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

    @Test
    void getAllProducts() throws Exception {
        mvc.perform(get("/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(3)))
                .andExpect(jsonPath("$.content[0].id", is(101)))
                .andExpect(jsonPath("$.content[0].article", is("001")))
                .andExpect(jsonPath("$.content[0].name", is("test product 1")))
                .andExpect(jsonPath("$.content[0].lastPurchasePrice", nullValue()))
                .andExpect(jsonPath("$.content[0].lastSalePrice", nullValue()))
                .andExpect(jsonPath("$.content[0].archive", is(false)))
                .andExpect(jsonPath("$.content[1].id", is(102)))
                .andExpect(jsonPath("$.content[1].article", is("002")))
                .andExpect(jsonPath("$.content[1].name", is("test product 2")))
                .andExpect(jsonPath("$.content[1].lastPurchasePrice", nullValue()))
                .andExpect(jsonPath("$.content[1].lastSalePrice", nullValue()))
                .andExpect(jsonPath("$.content[1].archive", is(false)))
                .andDo(result -> log.info(result.getResponse().getContentAsString()));
    }

    @Test
    void getProductById() throws Exception {
        mvc.perform(get("/products/{id}", "101"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(101)))
                .andExpect(jsonPath("$.article", is("001")))
                .andExpect(jsonPath("$.name", is("test product 1")))
                .andExpect(jsonPath("$.lastPurchasePrice", nullValue()))
                .andExpect(jsonPath("$.lastSalePrice", nullValue()))
                .andExpect(jsonPath("$.archive", is(false)))
                .andDo(result -> log.info(result.getResponse().getContentAsString()));

        mvc.perform(get("/products/{id}", "202"))
                .andExpect(status().is4xxClientError())
                .andDo(result -> log.info(result.getResponse().getContentAsString()));
    }

    @Test
    void createProduct() throws Exception {
        Product product = new Product(
                null, "100500", "New test product",
                null, null, false
        );
        mvc.perform(post("/products")
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
        Product product = new Product(
                null, "Update article", "Update product",
                null, null, false
        );
        mvc.perform(put("/products/{id}", "104")
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
        mvc.perform(put("/products/move-archive/{id}", productForArchive.getId()))
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
        mvc.perform(delete("/products/{id}", productForDelete.getId()))
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
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productWithNegativePrice)))
                .andExpect(status().is4xxClientError())
                .andDo(result -> log.info(result.getResponse().getContentAsString()));
    }
}