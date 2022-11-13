package com.rnsd.mystorage.service;

import com.rnsd.mystorage.entity.*;
import com.rnsd.mystorage.model.*;
import com.rnsd.mystorage.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DocumentServiceTest {

    @Mock StorageRepository storageRepository;
    @Mock ProductRepository productRepository;
    @Mock DocumentReceiptRepository documentReceiptRepository;
    @Mock DocumentSaleRepository documentSaleRepository;
    @Mock DocumentMovingRepository documentMovingRepository;
    @Mock InventoryControlRepository inventoryControlRepository;
    @Mock InventoryControlService inventoryControlService;
    @InjectMocks DocumentService documentService;

    @Mock
    Storage storage;
    @Mock
    Storage secondStorage;
    @Mock
    Product product;
    @Mock
    InventoryControl inventoryControl;

    String documentNumber = "#123456";
    Long storageId = 1L;
    Long secondStorageId = 2L;
    Long productId = 3L;
    Long count = 5L;
    Long inventoryControlCount = 20L;
    BigDecimal price = BigDecimal.TEN;

    @Captor
    ArgumentCaptor<DocumentReceipt> documentReceiptArgumentCaptor;
    @Captor
    ArgumentCaptor<DocumentSale> documentSaleArgumentCaptor;
    @Captor
    ArgumentCaptor<DocumentMoving> documentMovingArgumentCaptor;

    @BeforeEach
    public void setUp() {
        when(storageRepository.findByIdAndArchive(storageId, false)).thenReturn(Optional.of(storage));
        when(productRepository.findByIdAndArchive(productId, false)).thenReturn(Optional.of(product));
    }

    /**
     * Тест на успешное создание документа "Поступление" с последущим выполнением пересчета цены последней закупки и
     * пересчета (подсчета) количства товара на складе, на который они поступают
     */
    @Test
    void createDocumentReceipt() {
        DocumentReceiptModel documentReceiptModel = mock(DocumentReceiptModel.class);
        when(documentReceiptModel.getDocumentNumber()).thenReturn(documentNumber);
        when(documentReceiptModel.getStorageId()).thenReturn(storageId);
        ProductInDocumentModel productInDocumentModel = mock(ProductInDocumentModel.class);
        when(documentReceiptModel.getProductInfo()).thenReturn(List.of(productInDocumentModel));
        when(productInDocumentModel.getProductId()).thenReturn(productId);
        when(productInDocumentModel.getPrice()).thenReturn(price);
        when(productInDocumentModel.getCount()).thenReturn(count);

        DocumentReceipt afterSaveDocumentReceipt = mock(DocumentReceipt.class);
        when(documentReceiptRepository.save(documentReceiptArgumentCaptor.capture()))
                .thenReturn(afterSaveDocumentReceipt);

        List<DocumentReceipt> documentsReceipt = documentService.createDocumentReceipt(documentReceiptModel);
        DocumentReceipt forSaveDocumentReceipt = documentReceiptArgumentCaptor.getValue();

        assertNull(forSaveDocumentReceipt.getId());
        assertEquals(documentNumber, forSaveDocumentReceipt.getNumber());
        assertEquals(storage, forSaveDocumentReceipt.getStorage());
        assertEquals(product, forSaveDocumentReceipt.getProduct());
        assertEquals(count, forSaveDocumentReceipt.getCount());
        assertEquals(price, forSaveDocumentReceipt.getPurchasePrice());

        verify(product).setLastPurchasePrice(price);
        verify(inventoryControlService).recalculateCountProduct(storage, product, count);

        assertEquals(1, documentsReceipt.size());
        assertEquals(afterSaveDocumentReceipt, documentsReceipt.get(0));
    }

    /**
     * Тест на успешное создание документа "Продажа" с последущим выполнением пересчета цены последней продажи и
     * пересчета количества товара на складе, с которого списывают товары
     */
    @Test
    void createDocumentSale() {
        DocumentSaleModel documentSaleModel = mock(DocumentSaleModel.class);
        when(documentSaleModel.getDocumentNumber()).thenReturn(documentNumber);
        when(documentSaleModel.getStorageId()).thenReturn(storageId);
        ProductInDocumentModel productInDocumentModel = mock(ProductInDocumentModel.class);
        when(documentSaleModel.getProductInfo()).thenReturn(List.of(productInDocumentModel));
        when(productInDocumentModel.getProductId()).thenReturn(productId);
        when(productInDocumentModel.getCount()).thenReturn(count);
        when(inventoryControlRepository.findByStorageAndProduct(storage, product))
                .thenReturn(Optional.of(inventoryControl));
        when(inventoryControl.getCount()).thenReturn(inventoryControlCount);
        when(productInDocumentModel.getPrice()).thenReturn(price);

        DocumentSale afterSaveDocumentSale = mock(DocumentSale.class);
        when(documentSaleRepository.save(documentSaleArgumentCaptor.capture())).thenReturn(afterSaveDocumentSale);

        List<DocumentSale> documentsSales = documentService.createDocumentSale(documentSaleModel);
        DocumentSale forSaveDocumentSale = documentSaleArgumentCaptor.getValue();

        assertNull(forSaveDocumentSale.getId());
        assertEquals(documentNumber, forSaveDocumentSale.getNumber());
        assertEquals(storage, forSaveDocumentSale.getStorage());
        assertEquals(product, forSaveDocumentSale.getProduct());
        assertEquals(count, forSaveDocumentSale.getCount());
        assertEquals(price, forSaveDocumentSale.getSalePrice());

        verify(product).setLastSalePrice(price);
        verify(inventoryControlService).recalculateCountProduct(storage, product, -count);

        assertEquals(1, documentsSales.size());
        assertEquals(afterSaveDocumentSale, documentsSales.get(0));
    }

    /**
     * Тест на успешное создание документа "Перемещение" с последущим выполнением пересчета количества товара на складе,
     * с которого списывают товары и пересчета количства товара на складе, на который они поступают
     */
    @Test
    void createDocumentMoving() {
        when(storageRepository.findByIdAndArchive(secondStorageId, false))
                .thenReturn(Optional.of(secondStorage));
        DocumentMovingModel documentMovingModel = mock(DocumentMovingModel.class);
        when(documentMovingModel.getDocumentNumber()).thenReturn(documentNumber);
        when(documentMovingModel.getFromStorageId()).thenReturn(storageId);
        when(documentMovingModel.getToStorageId()).thenReturn(secondStorageId);
        ProductInDocumentForMovingModel productInDocumentForMovingModel =
                mock(ProductInDocumentForMovingModel.class);
        when(documentMovingModel.getProductInfo()).thenReturn(List.of(productInDocumentForMovingModel));
        when(productInDocumentForMovingModel.getProductId()).thenReturn(productId);
        when(productInDocumentForMovingModel.getCount()).thenReturn(count);
        when(inventoryControlRepository.findByStorageAndProduct(storage, product))
                .thenReturn(Optional.of(inventoryControl));
        when(inventoryControl.getCount()).thenReturn(inventoryControlCount);

        DocumentMoving afterSaveDocumentMoving = mock(DocumentMoving.class);
        when(documentMovingRepository.save(documentMovingArgumentCaptor.capture())).thenReturn(afterSaveDocumentMoving);

        List<DocumentMoving> documentsMoving = documentService.createDocumentMoving(documentMovingModel);
        DocumentMoving forSaveDocumentMoving = documentMovingArgumentCaptor.getValue();

        assertNull(forSaveDocumentMoving.getId());
        assertEquals(documentNumber, forSaveDocumentMoving.getNumber());
        assertEquals(storage, forSaveDocumentMoving.getFromStorage());
        assertEquals(secondStorage, forSaveDocumentMoving.getToStorage());
        assertEquals(product, forSaveDocumentMoving.getProduct());
        assertEquals(count, forSaveDocumentMoving.getCount());

        verify(inventoryControlService).recalculateCountProduct(storage, product, -count);
        verify(inventoryControlService).recalculateCountProduct(secondStorage, product, count);

        assertEquals(1, documentsMoving.size());
        assertEquals(afterSaveDocumentMoving, documentsMoving.get(0));
    }
}