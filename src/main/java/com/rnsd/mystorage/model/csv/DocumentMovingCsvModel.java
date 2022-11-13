package com.rnsd.mystorage.model.csv;

import com.opencsv.bean.CsvBindByName;
import com.rnsd.mystorage.entity.DocumentMoving;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@NoArgsConstructor
@Setter
public class DocumentMovingCsvModel {

    @CsvBindByName
    private Long id;

    @CsvBindByName
    private String number;

    @CsvBindByName
    private Long fromStorage;

    @CsvBindByName
    private Long toStorage;

    @CsvBindByName
    private Long productId;

    @CsvBindByName
    private Long count;

    public DocumentMovingCsvModel(DocumentMoving documentMoving) {
        this.id = documentMoving.getId();
        this.number = documentMoving.getNumber();
        this.fromStorage = documentMoving.getFromStorage().getId();
        this.toStorage = documentMoving.getToStorage().getId();
        this.productId = documentMoving.getProduct().getId();
        this.count = documentMoving.getCount();
    }
}
