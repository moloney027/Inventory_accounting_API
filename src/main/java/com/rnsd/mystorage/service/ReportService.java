package com.rnsd.mystorage.service;

import com.rnsd.mystorage.entity.InventoryControl;
import com.rnsd.mystorage.entity.Product;
import com.rnsd.mystorage.entity.Storage;
import com.rnsd.mystorage.model.ReportBalanceProductsModel;
import com.rnsd.mystorage.model.ReportProductsListModel;
import com.rnsd.mystorage.repository.InventoryControlRepository;
import com.rnsd.mystorage.repository.ProductRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Сервис для работы с отчетами
 */
@Service
@AllArgsConstructor
public class ReportService {

    private final InventoryControlRepository inventoryControlRepository;
    private final ProductRepository productRepository;

    /**
     * Создает отчет "Общий список товаров"
     */
    @Transactional
    public List<ReportProductsListModel> createProductsListReport(String nameProduct) {
        List<Product> products = nameProduct == null || nameProduct.isEmpty() ?
                productRepository.findByArchive(false) :
                productRepository.findByArchiveAndName(false, nameProduct);
        return products.stream().map(
                product -> new ReportProductsListModel(
                        product.getArticle(),
                        product.getName(),
                        product.getLastPurchasePrice() == null ? "" : product.getLastPurchasePrice().toString(),
                        product.getLastSalePrice() == null ? "" : product.getLastSalePrice().toString()
                )
        ).collect(Collectors.toList()
        );
    }

    /**
     * Создает отчет "Остатки товаров на складах"
     */
    @Transactional
    public List<ReportBalanceProductsModel> createBalanceProductsReport(Long storageId) {
        Storage filterStorage = new Storage(storageId);
        List<InventoryControl> inventoryControls = storageId == null ?
                inventoryControlRepository.findAll() :
                inventoryControlRepository.findAllByStorage(filterStorage);

        return inventoryControls.stream().map(
                        inventoryControl -> new ReportBalanceProductsModel(
                                inventoryControl.getProduct().getArticle(),
                                inventoryControl.getProduct().getName(),
                                inventoryControl.getCount()
                        )
                ).collect(
                        Collectors.groupingBy(Function.identity(),
                                Collectors.summingLong(ReportBalanceProductsModel::getBalance))
                ).entrySet().stream()
                .map(reportBalanceProductsModelLongEntry -> new ReportBalanceProductsModel(
                        reportBalanceProductsModelLongEntry.getKey().getArticle(),
                        reportBalanceProductsModelLongEntry.getKey().getName(),
                        reportBalanceProductsModelLongEntry.getValue())
                )
                .collect(Collectors.toList());
    }
}
