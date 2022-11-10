package com.rnsd.mystorage.model;

import lombok.Getter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Getter
public class DocumentSaleModel extends DocumentModel {

    @NotNull
    private final Long storageId;
    @NotEmpty
    private final List<ProductInDocumentModel> productInfo;

    public DocumentSaleModel(String documentNumber, Long storageId, List<ProductInDocumentModel> productInfo) {
        super(documentNumber);
        this.storageId = storageId;
        this.productInfo = productInfo;
    }
}
