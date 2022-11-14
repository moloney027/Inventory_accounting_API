package com.rnsd.mystorage.model;

import lombok.Getter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * Модель для документа "Перемещение"
 */
@Getter
public class DocumentMovingModel extends DocumentModel {

    @NotNull
    private final Long fromStorageId;

    @NotNull
    private final Long toStorageId;

    @NotEmpty
    private final List<ProductInDocumentForMovingModel> productInfo;

    public DocumentMovingModel(String documentNumber, Long fromStorageId, Long toStorageId,
                               List<ProductInDocumentForMovingModel> productInfo) {
        super(documentNumber);
        this.fromStorageId = fromStorageId;
        this.toStorageId = toStorageId;
        this.productInfo = productInfo;
    }
}
