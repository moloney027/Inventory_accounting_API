package com.rnsd.mystorage.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Schema(description = "Отчет \"Остатки товаров на складах\"")
@AllArgsConstructor
@Getter
public class ReportBalanceProductsModel {

    @Schema(description = "Артикул")
    private String article;

    @Schema(description = "Наименование")
    private String name;

    @Schema(description = "Остаток по всем складам")
    private Long balance;
}
