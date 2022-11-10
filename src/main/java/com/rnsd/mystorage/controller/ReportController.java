package com.rnsd.mystorage.controller;

import com.rnsd.mystorage.entity.InventoryControl;
import com.rnsd.mystorage.entity.Product;
import com.rnsd.mystorage.entity.Storage;
import com.rnsd.mystorage.model.ReportBalanceProductsModel;
import com.rnsd.mystorage.model.ReportProductsListModel;
import com.rnsd.mystorage.repository.InventoryControlRepository;
import com.rnsd.mystorage.repository.ProductRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@AllArgsConstructor
@Tag(
        name = "Отчеты",
        description = "На основе результатов проведенных документов имеется возможность построить отчеты о товарах " +
                "и их количестве на складах"
)
@RequestMapping("/reports")
public class ReportController {

    private final InventoryControlRepository inventoryControlRepository;
    private final ProductRepository productRepository;

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
        List<Product> products = nameProduct == null || nameProduct.isEmpty() ?
                productRepository.findByArchive(false) :
                productRepository.findByArchiveAndName(false, nameProduct);
        return ResponseEntity.ok(
                products.stream().map(
                        product -> new ReportProductsListModel(
                                product.getArticle(),
                                product.getName(),
                                product.getLastPurchasePrice() == null ? "" : product.getLastPurchasePrice().toString(),
                                product.getLastSalePrice().toString()
                        )
                ).collect(Collectors.toList())
        );
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
        Storage filterStorage = new Storage(storageId);
        List<InventoryControl> inventoryControls = storageId == null ?
                inventoryControlRepository.findAll() :
                inventoryControlRepository.findAllByStorage(filterStorage);
        return ResponseEntity.ok(
                inventoryControls.stream().map(
                        inventoryControl -> new ReportBalanceProductsModel(
                                inventoryControl.getProduct().getArticle(),
                                inventoryControl.getProduct().getName(),
                                inventoryControl.getCount()
                        )
                ).collect(Collectors.toList())
        );
    }

}
