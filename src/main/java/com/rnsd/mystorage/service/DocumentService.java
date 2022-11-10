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
    private final InventoryControlService inventoryControlService;

    @Transactional
    public List<DocumentReceipt> createDocumentReceipt(DocumentReceiptModel documentReceiptModel) {
        List<DocumentReceipt> documentsReceipt = new ArrayList<>();
        String number = documentReceiptModel.getDocumentNumber();
        Storage storage = storageRepository.findByIdAndArchive(documentReceiptModel.getStorageId(), false)
                .orElseThrow(() -> new CustomException(
                        "Not found storage with id= " + documentReceiptModel.getStorageId(), HttpStatus.BAD_REQUEST
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
            documentReceiptRepository.save(documentReceipt);
            documentsReceipt.add(documentReceipt);
            inventoryControlService.recalculateCountProduct(storage, product, count);
        }
        return documentsReceipt;
    }

    @Transactional
    public List<DocumentSale> createDocumentSale(DocumentSaleModel documentSaleModel) {
        List<DocumentSale> documentsSale = new ArrayList<>();
        String number = documentSaleModel.getDocumentNumber();
        Storage storage = storageRepository.findByIdAndArchive(documentSaleModel.getStorageId(), false)
                .orElseThrow(() -> new CustomException(
                        "Not found storage with id= " + documentSaleModel.getStorageId(), HttpStatus.BAD_REQUEST
                ));
        for (ProductInDocumentModel productInDocumentModel : documentSaleModel.getProductInfo()) {
            Product product = productRepository.findByIdAndArchive(productInDocumentModel.getProductId(), false)
                    .orElseThrow(
                            () -> new CustomException(
                                    "Not found product with id= " + productInDocumentModel.getProductId(),
                                    HttpStatus.BAD_REQUEST
                            )
                    );
            BigDecimal price = productInDocumentModel.getPrice();
            Long count = productInDocumentModel.getCount();
            product.setLastSalePrice(price);
            DocumentSale documentSale = new DocumentSale(null, number, storage, product, count, price);
            documentSaleRepository.save(documentSale);
            documentsSale.add(documentSale);
            inventoryControlService.recalculateCountProduct(storage, product, -count);
        }
        return documentsSale;
    }

    @Transactional
    public List<DocumentMoving> createDocumentMoving(DocumentMovingModel documentMovingModel) {
        List<DocumentMoving> documentsMoving = new ArrayList<>();
        String number = documentMovingModel.getDocumentNumber();
        Storage fromStorage = storageRepository.findByIdAndArchive(documentMovingModel.getFromStorageId(), false)
                .orElseThrow(() -> new CustomException(
                        "Not found from storage with id= " + documentMovingModel.getFromStorageId(),
                        HttpStatus.BAD_REQUEST
                ));
        Storage toStorage = storageRepository.findByIdAndArchive(documentMovingModel.getToStorageId(), false)
                .orElseThrow(() -> new CustomException(
                        "Not found to storage with id= " + documentMovingModel.getToStorageId(),
                        HttpStatus.BAD_REQUEST
                ));
        for (ProductInDocumentForMovingModel productInDocumentModel : documentMovingModel.getProductInfo()) {
            Product product = productRepository.findByIdAndArchive(productInDocumentModel.getProductId(), false)
                    .orElseThrow(() -> new CustomException(
                            "Not found product with id= " + productInDocumentModel.getProductId(),
                            HttpStatus.BAD_REQUEST
                    ));
            Long count = productInDocumentModel.getCount();
            DocumentMoving documentMoving = new DocumentMoving(null, number, fromStorage, toStorage, product, count);
            documentMovingRepository.save(documentMoving);
            documentsMoving.add(documentMoving);
            inventoryControlService.recalculateCountProduct(fromStorage, product, -count);
            inventoryControlService.recalculateCountProduct(toStorage, product, count);
        }
        return documentsMoving;
    }
}
