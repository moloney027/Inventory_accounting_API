package com.rnsd.mystorage.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rnsd.mystorage.MyStorageApplication;
import com.rnsd.mystorage.entity.InventoryControl;
import com.rnsd.mystorage.entity.Product;
import com.rnsd.mystorage.entity.Storage;
import com.rnsd.mystorage.model.*;
import com.rnsd.mystorage.model.security.JwtRequestModel;
import com.rnsd.mystorage.model.security.JwtResponseModel;
import com.rnsd.mystorage.repository.InventoryControlRepository;
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
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Интеграционные тесты для post методов контроллера документов
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = MyStorageApplication.class)
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application-integrationtest.properties")
@Slf4j
public class DocumentControllerPostMappingTest {

    @Autowired
    MockMvc mvc;
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    InventoryControlRepository inventoryControlRepository;
    @Autowired
    ProductRepository productRepository;

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

    /**
     * Тест метода POST (создание документа "Поступление") с проверкой того, что у поступивших на склад товаров
     * установилось верное значение цены последней закупки, указанное в документе, а таккже что произошел верный
     * пересчет количества товаров на складе, в зависимости от указанного в отчете количества поступающих товаров
     */
    @Test
    void createDocumentReceipt() throws Exception {
        ProductInDocumentModel productInDocumentModel1 =
                new ProductInDocumentModel(104L, 200L, BigDecimal.valueOf(548.36));
        ProductInDocumentModel productInDocumentModel2 =
                new ProductInDocumentModel(101L, 75L, BigDecimal.valueOf(4333.00));
        DocumentReceiptModel documentReceiptModel = new DocumentReceiptModel(
                "00001R", 201L, List.of(productInDocumentModel1, productInDocumentModel2)
        );
        Long countBefore104 = inventoryControlRepository
                .findByStorageAndProduct(new Storage(201L), new Product(104L))
                .map(InventoryControl::getCount).orElse(0L);
        Long countBefore101 = inventoryControlRepository
                .findByStorageAndProduct(new Storage(201L), new Product(101L))
                .map(InventoryControl::getCount).orElse(0L);
        mvc.perform(post("/documents/receipt")
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(documentReceiptModel)))
                .andExpect(status().isOk())
                .andDo(result -> log.info(result.getResponse().getContentAsString()));
        Long countAfter104 = inventoryControlRepository.
                findByStorageAndProduct(new Storage(201L), new Product(104L))
                .map(InventoryControl::getCount).orElseThrow();
        Long countAfter101 = inventoryControlRepository
                .findByStorageAndProduct(new Storage(201L), new Product(101L))
                .map(InventoryControl::getCount).orElseThrow();
        assertEquals(200, countAfter104 - countBefore104);
        assertEquals(75, countAfter101 - countBefore101);
        assertEquals(200, countBefore104 + productInDocumentModel1.getCount());
        assertEquals(75, countBefore101 + productInDocumentModel2.getCount());
        assertEquals(548.36, productRepository.findById(104L)
                .map(Product::getLastPurchasePrice)
                .map(BigDecimal::doubleValue).orElseThrow());
        assertEquals(4333., productRepository.findById(101L)
                .map(Product::getLastPurchasePrice)
                .map(BigDecimal::doubleValue).orElseThrow());
    }


    /**
     * Тест на недопустимость ввода отрицательного/буквенного/знакового значения для поля "Цена последней закупки"
     * в документе "Поступление"
     */
    @Test
    void wrongLastPurchasePrice() throws Exception {
        ProductInDocumentModel productInDocumentModelWithNegativePrice =
                new ProductInDocumentModel(101L, 10L, BigDecimal.valueOf(-26));
        DocumentReceiptModel documentReceiptModelWithNegativePrice = new DocumentReceiptModel(
                "000R", 201L, List.of(productInDocumentModelWithNegativePrice));
        mvc.perform(post("/documents/receipt")
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(documentReceiptModelWithNegativePrice)))
                .andExpect(status().is5xxServerError())
                .andDo(result -> log.info(result.getResponse().getContentAsString()));

        String documentReceiptModelWithNegativePrice2 = "{\n" +
                "  \"documentNumber\": \"0001\",\n" +
                "  \"storageId\": 201,\n" +
                "  \"productInfo\": [\n" +
                "    {\n" +
                "      \"productId\": 101,\n" +
                "      \"count\": 34,\n" +
                "      \"price\": \"абвгд\"\n" +
                "    }\n" +
                "  ]\n" +
                "}";
        mvc.perform(post("/documents/receipt")
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(documentReceiptModelWithNegativePrice2))
                .andExpect(status().is5xxServerError())
                .andDo(result -> log.info(result.getResponse().getContentAsString()));

        String documentReceiptModelWithNegativePrice3 = "{\n" +
                "  \"documentNumber\": \"0001\",\n" +
                "  \"storageId\": 201,\n" +
                "  \"productInfo\": [\n" +
                "    {\n" +
                "      \"productId\": 101,\n" +
                "      \"count\": 34,\n" +
                "      \"price\": 5%\n" +
                "    }\n" +
                "  ]\n" +
                "}";
        mvc.perform(post("/documents/receipt")
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(documentReceiptModelWithNegativePrice3))
                .andExpect(status().is5xxServerError())
                .andDo(result -> log.info(result.getResponse().getContentAsString()));
    }

    /**
     * Тест метода POST (создание документа "Продажа") с проверкой того, что у проданных со склада товаров
     * установилось верное значение цены последней продажи, указанное в документе, а таккже что произошел верный
     * пересчет количества товаров на складе, в зависимости от указанного в отчете количества списанных товаров
     */
    @Test
    void createDocumentSale() throws Exception {
        ProductInDocumentModel productInDocumentModelForReceipt1 =
                new ProductInDocumentModel(104L, 200L, BigDecimal.valueOf(452.11));
        ProductInDocumentModel productInDocumentModelForReceipt2 =
                new ProductInDocumentModel(101L, 75L, BigDecimal.valueOf(8297.00));
        DocumentReceiptModel documentReceiptModel = new DocumentReceiptModel(
                "00001R", 202L,
                List.of(productInDocumentModelForReceipt1, productInDocumentModelForReceipt2)
        );
        mvc.perform(post("/documents/receipt")
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(documentReceiptModel)))
                .andExpect(status().isOk())
                .andDo(result -> log.info(result.getResponse().getContentAsString()));

        ProductInDocumentModel productInDocumentModel1 =
                new ProductInDocumentModel(104L, 10L, BigDecimal.valueOf(500.00));
        ProductInDocumentModel productInDocumentModel2 =
                new ProductInDocumentModel(101L, 15L, BigDecimal.valueOf(10999.99));
        DocumentSaleModel documentSaleModel = new DocumentSaleModel(
                "00001S", 202L, List.of(productInDocumentModel1, productInDocumentModel2)
        );
        Long countBefore104 = inventoryControlRepository
                .findByStorageAndProduct(new Storage(202L), new Product(104L))
                .map(InventoryControl::getCount).orElse(0L);
        Long countBefore101 = inventoryControlRepository
                .findByStorageAndProduct(new Storage(202L), new Product(101L))
                .map(InventoryControl::getCount).orElse(0L);
        mvc.perform(post("/documents/sale")
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(documentSaleModel)))
                .andExpect(status().isOk())
                .andDo(result -> log.info(result.getResponse().getContentAsString()));
        Long countAfter104 = inventoryControlRepository
                .findByStorageAndProduct(new Storage(202L), new Product(104L))
                .map(InventoryControl::getCount).orElseThrow();
        Long countAfter101 = inventoryControlRepository
                .findByStorageAndProduct(new Storage(202L), new Product(101L))
                .map(InventoryControl::getCount).orElseThrow();
        assertEquals(10, countBefore104 - countAfter104);
        assertEquals(15, countBefore101 - countAfter101);
        assertEquals(190, countBefore104 - productInDocumentModel1.getCount());
        assertEquals(60, countBefore101 - productInDocumentModel2.getCount());
        assertEquals(452.11, productRepository.findById(104L)
                .map(Product::getLastPurchasePrice).map(BigDecimal::doubleValue).orElseThrow());
        assertEquals(8297., productRepository.findById(101L)
                .map(Product::getLastPurchasePrice).map(BigDecimal::doubleValue).orElseThrow());
        assertEquals(500., productRepository.findById(104L)
                .map(Product::getLastSalePrice).map(BigDecimal::doubleValue).orElseThrow());
        assertEquals(10999.99, productRepository.findById(101L)
                .map(Product::getLastSalePrice).map(BigDecimal::doubleValue).orElseThrow());
    }

    /**
     * Тест на недопустимость ввода отрицательного/буквенного/знакового значения для поля "Цена последней продажи"
     * в документе "Продажа"
     */
    @Test
    void wrongSalePrice() throws Exception {
        ProductInDocumentModel productInDocumentModelWithNegativePrice =
                new ProductInDocumentModel(101L, 10L, BigDecimal.valueOf(-55.65));
        DocumentSaleModel documentSaleModelWithNegativePrice = new DocumentSaleModel(
                "000S", 201L, List.of(productInDocumentModelWithNegativePrice));
        mvc.perform(post("/documents/sale")
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(documentSaleModelWithNegativePrice)))
                .andExpect(status().is5xxServerError())
                .andDo(result -> log.info(result.getResponse().getContentAsString()));

        String documentSaleModelWithNegativePrice2 = "{\n" +
                "  \"documentNumber\": \"0001\",\n" +
                "  \"storageId\": 201,\n" +
                "  \"productInfo\": [\n" +
                "    {\n" +
                "      \"productId\": 101,\n" +
                "      \"count\": 34,\n" +
                "      \"price\": \"абвгд\"\n" +
                "    }\n" +
                "  ]\n" +
                "}";
        mvc.perform(post("/documents/sale")
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(documentSaleModelWithNegativePrice2))
                .andExpect(status().is5xxServerError())
                .andDo(result -> log.info(result.getResponse().getContentAsString()));

        String documentSaleModelWithNegativePrice3 = "{\n" +
                "  \"documentNumber\": \"0001\",\n" +
                "  \"storageId\": 201,\n" +
                "  \"productInfo\": [\n" +
                "    {\n" +
                "      \"productId\": 101,\n" +
                "      \"count\": 34,\n" +
                "      \"price\": 5%\n" +
                "    }\n" +
                "  ]\n" +
                "}";
        mvc.perform(post("/documents/sale")
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(documentSaleModelWithNegativePrice3))
                .andExpect(status().is5xxServerError())
                .andDo(result -> log.info(result.getResponse().getContentAsString()));
    }

    /**
     * Тест метода POST (создание документа "Перемещение") с проверкой того, что произошел верный
     * пересчет количества товаров на складе с которого были списаны товары, в зависимости от указанного в отчете
     * количества товаров для перемещения, а также что произошел верный пересчет количества товаров на складе, на
     * который поступили перемещенные товары, в зависимости от того же значения
     */
    @Test
    void createDocumentMoving() throws Exception {
        ProductInDocumentModel productInDocumentModelForReceipt =
                new ProductInDocumentModel(102L, 55L, BigDecimal.valueOf(777.99));
        DocumentReceiptModel documentReceiptModel = new DocumentReceiptModel(
                "00001M", 203L, List.of(productInDocumentModelForReceipt)
        );
        mvc.perform(post("/documents/receipt")
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(documentReceiptModel)))
                .andExpect(status().isOk())
                .andDo(result -> log.info(result.getResponse().getContentAsString()));

        ProductInDocumentForMovingModel productInDocumentForMovingModel =
                new ProductInDocumentForMovingModel(102L, 20L);
        DocumentMovingModel documentMovingModel = new DocumentMovingModel(
                "00001M", 203L, 201L,
                List.of(productInDocumentForMovingModel)
        );
        Long countBefore203 = inventoryControlRepository
                .findByStorageAndProduct(new Storage(203L), new Product(102L))
                .map(InventoryControl::getCount).orElse(0L);
        Long countBefore201 = inventoryControlRepository
                .findByStorageAndProduct(new Storage(201L), new Product(102L))
                .map(InventoryControl::getCount).orElse(0L);
        mvc.perform(post("/documents/moving")
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(documentMovingModel)))
                .andExpect(status().isOk())
                .andDo(result -> log.info(result.getResponse().getContentAsString()));
        Long countAfter203 = inventoryControlRepository
                .findByStorageAndProduct(new Storage(203L), new Product(102L))
                .map(InventoryControl::getCount).orElseThrow();
        Long countAfter201 = inventoryControlRepository
                .findByStorageAndProduct(new Storage(201L), new Product(102L))
                .map(InventoryControl::getCount).orElseThrow();
        assertEquals(20, countBefore203 - countAfter203);
        assertEquals(20, countAfter201 - countBefore201);
        assertEquals(35, countBefore203 - productInDocumentForMovingModel.getCount());
        assertEquals(20, countBefore201 + productInDocumentForMovingModel.getCount());
        assertEquals(777.99, productRepository.findById(102L)
                .map(Product::getLastPurchasePrice).map(BigDecimal::doubleValue).orElseThrow());
    }

    /**
     * Тест на недопустимость ввода отрицательного/буквенного/знакового значения для поля "Количество товаров"
     * в документе "Перемещение"
     */
    @Test
    void wrongCountProducts() throws Exception {
        ProductInDocumentForMovingModel productInDocumentForMovingModel =
                new ProductInDocumentForMovingModel(102L, -20L);
        DocumentMovingModel documentMovingModel = new DocumentMovingModel(
                "00001M", 203L, 201L,
                List.of(productInDocumentForMovingModel)
        );
        mvc.perform(post("/documents/moving")
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(documentMovingModel)))
                .andExpect(status().is5xxServerError())
                .andDo(result -> log.info(result.getResponse().getContentAsString()));

        String documentMovingModelWithNegativePrice2 = "{\n" +
                "  \"documentNumber\": \"0001\",\n" +
                "  \"fromStorageId\": 201,\n" +
                "  \"toStorageId\": 202,\n" +
                "  \"productInfo\": [\n" +
                "    {\n" +
                "      \"productId\": 101,\n" +
                "      \"count\": \"абвгд\"\n" +
                "    }\n" +
                "  ]\n" +
                "}";
        mvc.perform(post("/documents/moving")
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(documentMovingModelWithNegativePrice2))
                .andExpect(status().is5xxServerError())
                .andDo(result -> log.info(result.getResponse().getContentAsString()));

        String documentMovingModelWithNegativePrice3 = "{\n" +
                "  \"documentNumber\": \"0001\",\n" +
                "  \"fromStorageId\": 201,\n" +
                "  \"toStorageId\": 202,\n" +
                "  \"productInfo\": [\n" +
                "    {\n" +
                "      \"productId\": 101,\n" +
                "      \"count\": \"абвгд\"\n" +
                "    }\n" +
                "  ]\n" +
                "}";
        mvc.perform(post("/documents/moving")
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(documentMovingModelWithNegativePrice3))
                .andExpect(status().is5xxServerError())
                .andDo(result -> log.info(result.getResponse().getContentAsString()));
    }
}
