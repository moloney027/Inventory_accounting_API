package com.rnsd.mystorage.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.math.BigDecimal;

/**
 * Модель для описания списка товаров для документов "Поступление" и "Продажа"
 */
@AllArgsConstructor
@Getter
public class ProductInDocumentModel {

    @NotNull
    private Long productId;

    @Positive
    @NotNull
    private Long count;

    @Positive
    @NotNull
    private BigDecimal price;
}
