package com.rnsd.mystorage.service;

import com.rnsd.mystorage.entity.InventoryControl;
import com.rnsd.mystorage.entity.Product;
import com.rnsd.mystorage.entity.Storage;
import com.rnsd.mystorage.repository.InventoryControlRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InventoryControlServiceTest {

    @Mock InventoryControlRepository inventoryControlRepository;
    @InjectMocks InventoryControlService inventoryControlService;

    @Mock Storage storage;
    @Mock Product product;
    @Mock InventoryControl inventoryControl;

    @Captor
    ArgumentCaptor<InventoryControl> inventoryControlArgumentCaptor;

    Long oldCount = 5L;
    Long count = 20L;

    /**
     * Тест на пересчет остатка конкретного товара на конкретном складе, когда на этом складе количество уже
     * присутствующего на нем товара увеличивается
     * (т.е. результат = значение количества товара до добавления + значение количества добавленного товара)
     */
    @Test
    void recalculateCountProduct() {
        when(inventoryControlRepository.findByStorageAndProduct(storage, product))
                .thenReturn(Optional.of(inventoryControl));
        when(inventoryControl.getCount()).thenReturn(oldCount);

        inventoryControlService.recalculateCountProduct(storage, product, count);

        verify(inventoryControlRepository).save(inventoryControlArgumentCaptor.capture());
        assertEquals(inventoryControl, inventoryControlArgumentCaptor.getValue());
        verify(inventoryControl).setCount(oldCount + count);
    }

    /**
     * Тест на подсчет количества конкретного товара на конкретном складу, когда на этот склад впервые добавляется этот
     * товар (т.е. результат = значение количества добавленного товара)
     */
    @Test
    void recalculateCountNewProduct() {
        when(inventoryControlRepository.findByStorageAndProduct(storage, product))
                .thenReturn(Optional.empty());

        inventoryControlService.recalculateCountProduct(storage, product, count);

        verify(inventoryControlRepository).save(inventoryControlArgumentCaptor.capture());
        InventoryControl ic = inventoryControlArgumentCaptor.getValue();
        assertNull(ic.getId());
        assertEquals(storage, ic.getStorage());
        assertEquals(product, ic.getProduct());
        assertEquals(count, ic.getCount());
    }
}