package com.rnsd.mystorage.controller;

import com.rnsd.mystorage.MyStorageApplication;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.hasSize;
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

    @Test
    void getAllDocumentsReceipt() throws Exception {
        mvc.perform(get("/documents/receipt"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(4)))
                .andExpect(jsonPath("$.content[0].number", is("00001R")))
                .andExpect(jsonPath("$.content[1].number", is("00002R")))
                .andExpect(jsonPath("$.content[2].number", is("00003R")))
                .andExpect(jsonPath("$.content[3].number", is("00004R")))
                .andDo(result -> log.info(result.getResponse().getContentAsString()));
    }

    @Test
    void getAllDocumentsSale() throws Exception {
        mvc.perform(get("/documents/sale"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(4)))
                .andExpect(jsonPath("$.content[0].number", is("00001S")))
                .andExpect(jsonPath("$.content[1].number", is("00002S")))
                .andExpect(jsonPath("$.content[2].number", is("00003S")))
                .andExpect(jsonPath("$.content[3].number", is("00004S")))
                .andDo(result -> log.info(result.getResponse().getContentAsString()));
    }

    @Test
    void getAllDocumentsMoving() throws Exception {
        mvc.perform(get("/documents/moving"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(4)))
                .andExpect(jsonPath("$.content[0].number", is("00001M")))
                .andExpect(jsonPath("$.content[1].number", is("00002M")))
                .andExpect(jsonPath("$.content[2].number", is("00003M")))
                .andExpect(jsonPath("$.content[3].number", is("00004M")))
                .andDo(result -> log.info(result.getResponse().getContentAsString()));
    }

    @Test
    void getDocumentReceiptById() throws Exception {
        mvc.perform(get("/documents/receipt/{id}", "303"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.number", is("00003R")))
                .andDo(result -> log.info(result.getResponse().getContentAsString()));
    }

    @Test
    void getDocumentSaleById() throws Exception {
        mvc.perform(get("/documents/sale/{id}", "401"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.number", is("00001S")))
                .andDo(result -> log.info(result.getResponse().getContentAsString()));
    }

    @Test
    void getDocumentMovingById() throws Exception {
        mvc.perform(get("/documents/moving/{id}", "504"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.number", is("00004M")))
                .andDo(result -> log.info(result.getResponse().getContentAsString()));
    }


}