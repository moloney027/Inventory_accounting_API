package com.rnsd.mystorage.controller;

import com.opencsv.CSVWriter;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import com.rnsd.mystorage.model.ReportBalanceProductsModel;
import com.rnsd.mystorage.model.ReportProductsListModel;
import com.rnsd.mystorage.service.ReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

@RestController
@AllArgsConstructor
@Tag(
        name = "Отчеты",
        description = "На основе результатов проведенных документов имеется возможность построить отчеты о товарах " +
                "и их количестве на складах"
)
@RequestMapping("/reports")
public class ReportController {

    private final ReportService reportService;

    @Operation(
            summary = "Сформировать отчет \"Общий список товаров\"",
            description = "Отчет содержит: артикул товара, наименование товара, цены закупки и продажи товара. " +
                    "Данный отчет будет содержать информацию о всех имеющихся товарах, включая те, которые уже " +
                    "поступили на склад и те, которые еще ни разу не были отправлены на склад, т.е. не имеющие ни " +
                    "цену закупки, ни цену продажи. В отчете возможно наложить фильтр по имени товара."
    )
    @GetMapping("/products-list")
    ResponseEntity<List<ReportProductsListModel>> getProductsListReport(
            @RequestParam(required = false)
            @Parameter(description = "Название товара, по которому будут отфильтрованы данные") String nameProduct
    ) {
        return ResponseEntity.ok(reportService.createProductsListReport(nameProduct));
    }

    @Operation(
            summary = "Сформировать отчет \"Остатки товаров на складах\"",
            description = "Отчет содержит: артикул товара, наименование товара, остаток товара по всем складам. " +
                    "Данный отчет будет содержать данные по результатам выполненных действий с товарами и складами. " +
                    "Пока ни на одном складу нет ни одного товара данный отчет будет пустым. Поэтому для построения " +
                    "отчета \"Остатки товаров на складах\" необходимо после создания товаров и складов первым шагом " +
                    "провести хотя бы 1 документ \"Поступление\" с одним или несколькими товарами и складом. После " +
                    "проведения некоторого количества документов в отчете будет хранится информация, рассчитанная " +
                    "на основании всех документов. В отчете возможно наложить фильтр по складу."
    )
    @GetMapping("/balance-products-in-storages")
    ResponseEntity<List<ReportBalanceProductsModel>> getBalanceProductsReport(
            @RequestParam(required = false)
            @Parameter(description = "ID склада, по которому будут отфильтрованы данные") Long storageId
    ) {
        return ResponseEntity.ok(reportService.createBalanceProductsReport(storageId));
    }

    @Operation(
            summary = "Сформировать отчет \"Общий список товаров\" в формате CSV",
            description = "Отчет содержит: артикул товара, наименование товара, цены закупки и продажи товара. " +
                    "Данный отчет будет содержать информацию о всех имеющихся товарах, включая те, которые уже " +
                    "поступили на склад и те, которые еще ни разу не были отправлены на склад, т.е. не имеющие ни " +
                    "цену закупки, ни цену продажи. В отчете возможно наложить фильтр по имени товара."
    )
    @GetMapping("/export-csv-report-products-list")
    public void exportCsvReportProductsList(
            @RequestParam(required = false)
            @Parameter(description = "Название товара, по которому будут отфильтрованы данные") String nameProduct,
            HttpServletResponse response
    ) throws Exception {
        String filename = "ReportProductsList.csv";
        response.setContentType("text/csv");
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"");
        StatefulBeanToCsv<ReportProductsListModel> writer =
                new StatefulBeanToCsvBuilder<ReportProductsListModel>
                        (response.getWriter())
                        .withQuotechar(CSVWriter.NO_QUOTE_CHARACTER)
                        .withSeparator(CSVWriter.DEFAULT_SEPARATOR)
                        .withOrderedResults(false)
                        .build();
        writer.write(reportService.createProductsListReport(nameProduct));
    }

    @Operation(
            summary = "Сформировать отчет \"Остатки товаров на складах\" в формате CSV",
            description = "Отчет содержит: артикул товара, наименование товара, остаток товара по всем складам. " +
                    "Данный отчет будет содержать данные по результатам выполненных действий с товарами и складами. " +
                    "Пока ни на одном складу нет ни одного товара данный отчет будет пустым. Поэтому для построения " +
                    "отчета \"Остатки товаров на складах\" необходимо после создания товаров и складов первым шагом " +
                    "провести хотя бы 1 документ \"Поступление\" с одним или несколькими товарами и складом. После " +
                    "проведения некоторого количества документов в отчете будет хранится информация, рассчитанная " +
                    "на основании всех документов. В отчете возможно наложить фильтр по складу."
    )
    @GetMapping("/export-csv-report-balance-products-in-storages")
    public void exportCsvReportBalanceProductsInStorages(
            @RequestParam(required = false)
            @Parameter(description = "ID склада, по которому будут отфильтрованы данные") Long storageId,
            HttpServletResponse response
    ) throws Exception {
        String filename = "ReportBalanceProductsInStorages.csv";
        response.setContentType("text/csv");
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"");
        StatefulBeanToCsv<ReportBalanceProductsModel> writer =
                new StatefulBeanToCsvBuilder<ReportBalanceProductsModel>
                        (response.getWriter())
                        .withQuotechar(CSVWriter.NO_QUOTE_CHARACTER)
                        .withSeparator(CSVWriter.DEFAULT_SEPARATOR)
                        .withOrderedResults(false)
                        .build();
        writer.write(reportService.createBalanceProductsReport(storageId));
    }

}
