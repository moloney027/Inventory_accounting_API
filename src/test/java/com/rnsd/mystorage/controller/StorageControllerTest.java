package com.rnsd.mystorage.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rnsd.mystorage.MyStorageApplication;
import com.rnsd.mystorage.entity.Storage;
import com.rnsd.mystorage.repository.StorageRepository;
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

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = MyStorageApplication.class)
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application-integrationtest.properties")
@Slf4j
class StorageControllerTest {

    @Autowired
    StorageRepository storageRepository;
    @Autowired
    MockMvc mvc;
    @Autowired
    ObjectMapper objectMapper;

    @Test
    void getAllStorages() throws Exception {
        mvc.perform(get("/storages"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(3)))
                .andExpect(jsonPath("$.content[0].id", is(201)))
                .andExpect(jsonPath("$.content[0].name", is("test storage 1")))
                .andExpect(jsonPath("$.content[0].archive", is(false)))
                .andExpect(jsonPath("$.content[1].id", is(202)))
                .andExpect(jsonPath("$.content[1].name", is("test storage 2")))
                .andExpect(jsonPath("$.content[1].archive", is(false)))
                .andDo(result -> log.info(result.getResponse().getContentAsString()));
    }

    @Test
    void getStorageById() throws Exception {
        mvc.perform(get("/storages/{id}", "201"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(201)))
                .andExpect(jsonPath("$.name", is("test storage 1")))
                .andExpect(jsonPath("$.archive", is(false)))
                .andDo(result -> log.info(result.getResponse().getContentAsString()));

        mvc.perform(get("/products/{id}", "204"))
                .andExpect(status().is4xxClientError())
                .andDo(result -> log.info(result.getResponse().getContentAsString()));
    }

    @Test
    void createStorage() throws Exception {
        Storage storage = new Storage(null, "test storage 5", false);
        mvc.perform(post("/storages")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(storage)))
                .andExpect(status().isOk())
                .andDo(result -> {
                    log.info(result.getResponse().getContentAsString());
                    Storage value = objectMapper.readValue(result.getResponse().getContentAsString(), Storage.class);
                    storageRepository.deleteById(value.getId());
                });

        Storage storageWithNullName = new Storage(null, null, false);
        mvc.perform(post("/storages")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(storageWithNullName)))
                .andExpect(status().is4xxClientError())
                .andDo(result -> log.info(result.getResponse().getContentAsString()));
    }

    @Test
    void updateStorage() throws Exception {
        Storage storage = new Storage(null, "update storage", false);
        mvc.perform(put("/storages/{id}", "203")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(storage)))
                .andExpect(status().isOk())
                .andDo(result -> log.info(result.getResponse().getContentAsString()));
    }

    @Test
    void archiveStorage() throws Exception {
        Storage storageForArchive = new Storage(null, "test storage 6", false);
        storageForArchive = storageRepository.save(storageForArchive);
        mvc.perform(put("/storages/move-archive/{id}", storageForArchive.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.archive", is(true)))
                .andDo(result -> {
                    log.info(result.getResponse().getContentAsString());
                    Storage value = objectMapper.readValue(result.getResponse().getContentAsString(), Storage.class);
                    storageRepository.deleteById(value.getId());
                });
    }

    @Test
    void deleteStorage() throws Exception {
        Storage storageForDelete = new Storage(null, "test storage 7", true);
        storageForDelete = storageRepository.save(storageForDelete);
        mvc.perform(delete("/storages/{id}", storageForDelete.getId()))
                .andExpect(status().isOk())
                .andDo(result -> log.info(result.getResponse().getContentAsString()));

        Storage storageNotDelete = new Storage(null, "test storage 7", false);
        storageNotDelete = storageRepository.save(storageNotDelete);
        mvc.perform(delete("/storages/{id}", storageNotDelete.getId()))
                .andExpect(status().is4xxClientError())
                .andDo(result -> log.info(result.getResponse().getContentAsString()));
        storageRepository.deleteById(storageNotDelete.getId());
    }
}