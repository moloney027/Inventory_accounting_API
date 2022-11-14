package com.rnsd.mystorage.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

/**
 * Модель для описания списка товаров для документа "Перемещение"
 */
@AllArgsConstructor
@Getter
public class ProductInDocumentForMovingModel {

    @NotNull
    private Long productId;

    @Positive
    @NotNull
    private Long count;
}
