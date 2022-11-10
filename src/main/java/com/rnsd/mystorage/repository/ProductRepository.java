package com.rnsd.mystorage.repository;

import com.rnsd.mystorage.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends PagingAndSortingRepository<Product, Long> {

    List<Product> findByArchive(Boolean archive);

    List<Product> findByArchiveAndName(Boolean archive, String nameProduct);

    Page<Product> findAllByArchive(Pageable pageable, Boolean archive);

    Optional<Product> findByIdAndArchive(Long id, Boolean archive);

    @NonNull
    List<Product> findAll();
}
