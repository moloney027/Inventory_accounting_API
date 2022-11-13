package com.rnsd.mystorage.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rnsd.mystorage.MyStorageApplication;
import com.rnsd.mystorage.entity.Storage;
import com.rnsd.mystorage.model.security.JwtRequestModel;
import com.rnsd.mystorage.model.security.JwtResponseModel;
import com.rnsd.mystorage.repository.StorageRepository;
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

    @Autowired
    AuthService authService;

    private String accessToken;

    @BeforeEach
    void setUp() {
        JwtRequestModel jwtRequestModel = new JwtRequestModel();
        jwtRequestModel.setLogin("admin");
        jwtRequestModel.setPassword("admin");
        JwtResponseModel login = authService.login(jwtRequestModel);
        accessToken = login.getAccessToken();
    }

    @Test
    void getAllStorages() throws Exception {
        mvc.perform(get("/storages").header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andDo(result -> log.info(result.getResponse().getContentAsString()));
    }

    @Test
    void getStorageById() throws Exception {
        Storage storageForGet = new Storage(null, "test storage", false);
        storageForGet = storageRepository.save(storageForGet);
        mvc.perform(get("/storages/{id}", storageForGet.getId())
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(storageForGet.getId().intValue())))
                .andExpect(jsonPath("$.name", is("test storage")))
                .andExpect(jsonPath("$.archive", is(false)))
                .andDo(result -> log.info(result.getResponse().getContentAsString()));

        Storage storageForGet1 = new Storage(null, "test storage", true);
        storageForGet1 = storageRepository.save(storageForGet1);
        mvc.perform(get("/products/{id}", storageForGet1.getId())
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().is4xxClientError())
                .andDo(result -> log.info(result.getResponse().getContentAsString()));
    }

    @Test
    void createStorage() throws Exception {
        Storage storage = new Storage(null, "test storage 5", false);
        mvc.perform(post("/storages")
                        .header("Authorization", "Bearer " + accessToken)
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
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(storageWithNullName)))
                .andExpect(status().is4xxClientError())
                .andDo(result -> log.info(result.getResponse().getContentAsString()));
    }

    @Test
    void updateStorage() throws Exception {
        Storage storage = new Storage(null, "update storage", false);
        Storage storageForUpdate = new Storage(null, "test storage", false);
        storageForUpdate = storageRepository.save(storageForUpdate);
        mvc.perform(put("/storages/{id}", storageForUpdate.getId())
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(storage)))
                .andExpect(status().isOk())
                .andDo(result -> log.info(result.getResponse().getContentAsString()));
    }

    @Test
    void archiveStorage() throws Exception {
        Storage storageForArchive = new Storage(null, "test storage 6", false);
        storageForArchive = storageRepository.save(storageForArchive);
        mvc.perform(put("/storages/move-archive/{id}", storageForArchive.getId())
                        .header("Authorization", "Bearer " + accessToken))
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
        mvc.perform(delete("/storages/{id}", storageForDelete.getId())
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andDo(result -> log.info(result.getResponse().getContentAsString()));

        Storage storageNotDelete = new Storage(null, "test storage 7", false);
        storageNotDelete = storageRepository.save(storageNotDelete);
        mvc.perform(delete("/storages/{id}", storageNotDelete.getId())
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().is4xxClientError())
                .andDo(result -> log.info(result.getResponse().getContentAsString()));
        storageRepository.deleteById(storageNotDelete.getId());
    }
}