package com.rnsd.mystorage.model.csv;

import com.opencsv.bean.CsvBindByName;
import com.rnsd.mystorage.entity.DocumentReceipt;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * Модель для перевода документа "Поступление" в CSV формат
 */
@Getter
@NoArgsConstructor
@Setter
public class DocumentReceiptCsvModel {

    @CsvBindByName
    private Long id;

    @CsvBindByName
    private String number;

    @CsvBindByName
    private Long storageId;

    @CsvBindByName
    private Long productId;

    @CsvBindByName
    private Long count;

    @CsvBindByName
    private BigDecimal purchasePrice;

    public DocumentReceiptCsvModel(DocumentReceipt documentReceipt) {
        this.id = documentReceipt.getId();
        this.number = documentReceipt.getNumber();
        this.storageId = documentReceipt.getStorage().getId();
        this.productId = documentReceipt.getProduct().getId();
        this.count = documentReceipt.getCount();
        this.purchasePrice = documentReceipt.getPurchasePrice();
    }
}
