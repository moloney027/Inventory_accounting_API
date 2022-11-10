package com.rnsd.mystorage.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Schema(description = "Отчет \"Общий список товаров\"")
@AllArgsConstructor
@Getter
public class ReportProductsListModel {

    @Schema(description = "Артикул")
    private String article;

    @Schema(description = "Наименование")
    private String name;

    @Schema(description = "Цена последней закупки")
    private String lastPurchasePrice;

    @Schema(description = "Цена последней продажи")
    private String lastSalePrice;
}
