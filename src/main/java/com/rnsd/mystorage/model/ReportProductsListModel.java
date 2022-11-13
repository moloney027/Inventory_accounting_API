package com.rnsd.mystorage.model;

import com.opencsv.bean.CsvBindByName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Schema(description = "Отчет \"Общий список товаров\"")
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class ReportProductsListModel {

    @Schema(description = "Артикул")
    @CsvBindByName
    private String article;

    @Schema(description = "Наименование")
    @CsvBindByName
    private String name;

    @Schema(description = "Цена последней закупки")
    @CsvBindByName
    private String lastPurchasePrice;

    @Schema(description = "Цена последней продажи")
    @CsvBindByName
    private String lastSalePrice;
}
