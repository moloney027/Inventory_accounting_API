package com.rnsd.mystorage.service;

import com.rnsd.mystorage.entity.InventoryControl;
import com.rnsd.mystorage.entity.Product;
import com.rnsd.mystorage.entity.Storage;
import com.rnsd.mystorage.repository.InventoryControlRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

/**
 * Сервис для работы со складским учетом
 */
@Service
@AllArgsConstructor
public class InventoryControlService {

    private final InventoryControlRepository inventoryControlRepository;

    /**
     * Пересчитывает количество товара на складе после проведения любого документа из трех возможных (данные о
     * количестве товара используются в отчете "Остатки товаров на складах")
     */
    @Transactional
    public void recalculateCountProduct(Storage storage, Product product, Long count) {
        inventoryControlRepository.save(
                inventoryControlRepository.findByStorageAndProduct(storage, product)
                        .map(inventoryControl -> {
                            inventoryControl.setCount(inventoryControl.getCount() + count);
                            return inventoryControl;
                        })
                        .orElse(new InventoryControl(null, storage, product, count)));
    }
}
