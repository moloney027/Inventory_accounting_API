package com.rnsd.mystorage.model;

import lombok.Getter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * Модель для документа "Поступление"
 */
@Getter
public class DocumentReceiptModel extends DocumentModel {

    @NotNull
    private final Long storageId;

    @NotEmpty
    private final List<ProductInDocumentModel> productInfo;

    public DocumentReceiptModel(String documentNumber, Long storageId, List<ProductInDocumentModel> productInfo) {
        super(documentNumber);
        this.storageId = storageId;
        this.productInfo = productInfo;
    }
}
