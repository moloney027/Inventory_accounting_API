package com.rnsd.mystorage.repository;

import com.rnsd.mystorage.entity.InventoryControl;
import com.rnsd.mystorage.entity.Product;
import com.rnsd.mystorage.entity.Storage;
import org.springframework.data.repository.PagingAndSortingRepository;

import javax.transaction.Transactional;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Optional;

public interface InventoryControlRepository extends PagingAndSortingRepository<InventoryControl, Long> {

    Optional<InventoryControl> findByStorageAndProduct(Storage storage, Product product);

    @Transactional
    void deleteByProduct(Product product);

    @Transactional
    void deleteByStorage(Storage storage);

    @NotNull
    List<InventoryControl> findAll();

    List<InventoryControl> findAllByStorage(Storage storage);
}
