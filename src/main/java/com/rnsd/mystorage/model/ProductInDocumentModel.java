package com.rnsd.mystorage.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.math.BigDecimal;

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
