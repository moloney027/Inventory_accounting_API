package com.rnsd.mystorage.model;

import com.opencsv.bean.CsvBindByName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Schema(description = "Отчет \"Остатки товаров на складах\"")
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class ReportBalanceProductsModel {

    @Schema(description = "Артикул")
    @CsvBindByName
    private String article;

    @Schema(description = "Наименование")
    @CsvBindByName
    private String name;

    @Schema(description = "Остаток по всем складам")
    @CsvBindByName
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
