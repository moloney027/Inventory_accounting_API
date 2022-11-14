package com.rnsd.mystorage.model.csv;


import com.opencsv.bean.CsvBindByName;
import com.rnsd.mystorage.entity.DocumentSale;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * Модель для перевода документа "Продажа" в CSV формат
 */
@Getter
@NoArgsConstructor
@Setter
public class DocumentSaleCsvModel {

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
    private BigDecimal salePrice;

    public DocumentSaleCsvModel(DocumentSale documentSale) {
        this.id = documentSale.getId();
        this.number = documentSale.getNumber();
        this.storageId = documentSale.getStorage().getId();
        this.productId = documentSale.getProduct().getId();
        this.count = documentSale.getCount();
        this.salePrice = documentSale.getSalePrice();
    }
}
