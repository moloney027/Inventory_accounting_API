package com.rnsd.mystorage.controller;

import com.rnsd.mystorage.MyStorageApplication;
import com.rnsd.mystorage.model.security.JwtRequestModel;
import com.rnsd.mystorage.model.security.JwtResponseModel;
import com.rnsd.mystorage.service.security.AuthService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = MyStorageApplication.class)
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application-integrationtest.properties")
@Slf4j
class DocumentControllerGetMappingTest {

    @Autowired
    MockMvc mvc;

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
    void getAllDocumentsReceipt() throws Exception {
        mvc.perform(get("/documents/receipt").header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andDo(result -> log.info(result.getResponse().getContentAsString()));
    }

    @Test
    void getAllDocumentsSale() throws Exception {
        mvc.perform(get("/documents/sale").header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andDo(result -> log.info(result.getResponse().getContentAsString()));
    }

    @Test
    void getAllDocumentsMoving() throws Exception {
        mvc.perform(get("/documents/moving").header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andDo(result -> log.info(result.getResponse().getContentAsString()));
    }

    @Test
    void getDocumentReceiptById() throws Exception {
        mvc.perform(get("/documents/receipt/{id}", "303").header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.number", is("00003R")))
                .andDo(result -> log.info(result.getResponse().getContentAsString()));
    }

    @Test
    void getDocumentSaleById() throws Exception {
        mvc.perform(get("/documents/sale/{id}", "401").header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.number", is("00001S")))
                .andDo(result -> log.info(result.getResponse().getContentAsString()));
    }

    @Test
    void getDocumentMovingById() throws Exception {
        mvc.perform(get("/documents/moving/{id}", "504").header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.number", is("00004M")))
                .andDo(result -> log.info(result.getResponse().getContentAsString()));
    }


}