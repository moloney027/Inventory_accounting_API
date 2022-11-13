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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ReportBalanceProductsModel that = (ReportBalanceProductsModel) o;

        if (!article.equals(that.article)) return false;
        return name.equals(that.name);
    }

    @Override
    public int hashCode() {
        int result = article.hashCode();
        result = 31 * result + name.hashCode();
        return result;
    }
}
