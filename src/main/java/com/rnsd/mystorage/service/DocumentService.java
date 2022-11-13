package com.rnsd.mystorage.service;

import com.rnsd.mystorage.entity.*;
import com.rnsd.mystorage.exception.CustomException;
import com.rnsd.mystorage.model.*;
import com.rnsd.mystorage.repository.*;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class DocumentService {

    private final DocumentReceiptRepository documentReceiptRepository;
    private final DocumentSaleRepository documentSaleRepository;
    private final DocumentMovingRepository documentMovingRepository;
    private final StorageRepository storageRepository;
    private final ProductRepository productRepository;
    private final InventoryControlRepository inventoryControlRepository;
    private final InventoryControlService inventoryControlService;

    @Transactional
    public List<DocumentReceipt> createDocumentReceipt(DocumentReceiptModel documentReceiptModel) {
        Long storageId = documentReceiptModel.getStorageId();
        List<DocumentReceipt> documentReceiptList = new ArrayList<>();
        String number = documentReceiptModel.getDocumentNumber();
        Storage storage = storageRepository.findByIdAndArchive(storageId, false)
                .orElseThrow(() -> new CustomException(
                        "Not found storage with id= " + storageId, HttpStatus.BAD_REQUEST
                ));
        for (ProductInDocumentModel productInDocumentModel : documentReceiptModel.getProductInfo()) {
            Product product = productRepository.findByIdAndArchive(productInDocumentModel.getProductId(), false)
                    .orElseThrow(
                            () -> new CustomException(
                                    "Not found product with id= " + productInDocumentModel.getProductId(),
                                    HttpStatus.BAD_REQUEST
                            )
                    );
            BigDecimal price = productInDocumentModel.getPrice();
            Long count = productInDocumentModel.getCount();
            product.setLastPurchasePrice(price);
            DocumentReceipt documentReceipt = new DocumentReceipt(null, number, storage, product, count, price);
            documentReceipt = documentReceiptRepository.save(documentReceipt);
            documentReceiptList.add(documentReceipt);
            inventoryControlService.recalculateCountProduct(storage, product, count);
        }
        return documentReceiptList;
    }

    @Transactional
    public List<DocumentSale> createDocumentSale(DocumentSaleModel documentSaleModel) {
        Long storageId = documentSaleModel.getStorageId();
        List<DocumentSale> documentSaleList = new ArrayList<>();
        String number = documentSaleModel.getDocumentNumber();
        Storage storage = storageRepository.findByIdAndArchive(storageId, false)
                .orElseThrow(() -> new CustomException(
                        "Not found storage with id= " + storageId, HttpStatus.BAD_REQUEST
                ));
        for (ProductInDocumentModel productInDocumentModel : documentSaleModel.getProductInfo()) {
            Product product = productRepository.findByIdAndArchive(productInDocumentModel.getProductId(), false)
                    .orElseThrow(
                            () -> new CustomException(
                                    "Not found product with id= " + productInDocumentModel.getProductId(),
                                    HttpStatus.BAD_REQUEST
                            )
                    );
            Long count = productInDocumentModel.getCount();
            if (inventoryControlRepository.findByStorageAndProduct(storage, product)
                    .map(InventoryControl::getCount).orElse(0L) < count) {
                throw new CustomException("The quantity of the product sold cannot be greater than the quantity of " +
                        "the product available", HttpStatus.BAD_REQUEST);
            }
            BigDecimal price = productInDocumentModel.getPrice();
            product.setLastSalePrice(price);
            DocumentSale documentSale = new DocumentSale(null, number, storage, product, count, price);
            documentSale = documentSaleRepository.save(documentSale);
            documentSaleList.add(documentSale);
            inventoryControlService.recalculateCountProduct(storage, product, -count);
        }
        return documentSaleList;
    }

    @Transactional
    public List<DocumentMoving> createDocumentMoving(DocumentMovingModel documentMovingModel) {
        Long fromStorageId = documentMovingModel.getFromStorageId();
        Long toStorageId = documentMovingModel.getToStorageId();
        List<DocumentMoving> documentMovingList = new ArrayList<>();
        String number = documentMovingModel.getDocumentNumber();
        Storage fromStorage = storageRepository.findByIdAndArchive(fromStorageId, false)
                .orElseThrow(() -> new CustomException(
                        "Not found from storage with id= " + fromStorageId,
                        HttpStatus.BAD_REQUEST
                ));
        Storage toStorage = storageRepository.findByIdAndArchive(toStorageId, false)
                .orElseThrow(() -> new CustomException(
                        "Not found to storage with id= " + toStorageId,
                        HttpStatus.BAD_REQUEST
                ));
        for (ProductInDocumentForMovingModel productInDocumentModel : documentMovingModel.getProductInfo()) {
            Product product = productRepository.findByIdAndArchive(productInDocumentModel.getProductId(), false)
                    .orElseThrow(() -> new CustomException(
                            "Not found product with id= " + productInDocumentModel.getProductId(),
                            HttpStatus.BAD_REQUEST
                    ));
            Long count = productInDocumentModel.getCount();
            if (inventoryControlRepository.findByStorageAndProduct(fromStorage, product)
                    .map(InventoryControl::getCount).orElse(0L) < count) {
                throw new CustomException("The quantity of product being moved cannot be greater than the quantity of " +
                        "product available in the first storage", HttpStatus.BAD_REQUEST);
            }
            DocumentMoving documentMoving = new DocumentMoving(null, number, fromStorage, toStorage, product, count);
            documentMoving = documentMovingRepository.save(documentMoving);
            documentMovingList.add(documentMoving);
            inventoryControlService.recalculateCountProduct(fromStorage, product, -count);
            inventoryControlService.recalculateCountProduct(toStorage, product, count);
        }
        return documentMovingList;
    }
}
