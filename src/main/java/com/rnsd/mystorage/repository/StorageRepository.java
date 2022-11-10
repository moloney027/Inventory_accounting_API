package com.rnsd.mystorage.repository;

import com.rnsd.mystorage.entity.Storage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StorageRepository extends PagingAndSortingRepository<Storage, Long> {

    List<Storage> findByArchive(Boolean archive);

    Page<Storage> findAllByArchive(Pageable pageable, Boolean archive);

    Optional<Storage> findByIdAndArchive(Long id, Boolean archive);

    @NonNull
    List<Storage> findAll();
}
