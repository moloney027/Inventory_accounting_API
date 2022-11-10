package com.rnsd.mystorage.repository;

import com.rnsd.mystorage.entity.DocumentReceipt;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface DocumentReceiptRepository extends PagingAndSortingRepository<DocumentReceipt, Long> {
}
