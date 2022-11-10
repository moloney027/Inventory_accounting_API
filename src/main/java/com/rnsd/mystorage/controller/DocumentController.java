package com.rnsd.mystorage.controller;

import com.rnsd.mystorage.entity.DocumentMoving;
import com.rnsd.mystorage.entity.DocumentReceipt;
import com.rnsd.mystorage.entity.DocumentSale;
import com.rnsd.mystorage.exception.CustomException;
import com.rnsd.mystorage.model.DocumentMovingModel;
import com.rnsd.mystorage.model.DocumentReceiptModel;
import com.rnsd.mystorage.model.DocumentSaleModel;
import com.rnsd.mystorage.repository.DocumentMovingRepository;
import com.rnsd.mystorage.repository.DocumentReceiptRepository;
import com.rnsd.mystorage.repository.DocumentSaleRepository;
import com.rnsd.mystorage.service.DocumentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springdoc.api.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@AllArgsConstructor
@Tag(
        name = "Документы",
        description = "С помощью проведения документов возможно реализовать поступление, продажу и перемещение товаров"
)
@RequestMapping("/documents")
public class DocumentController {

    private final DocumentReceiptRepository documentReceiptRepository;
    private final DocumentSaleRepository documentSaleRepository;
    private final DocumentMovingRepository documentMovingRepository;
    private final DocumentService documentService;

    @Operation(
            summary = "Получить все документы \"Поступление\"",
            description = "Позволяет постранично просмотреть все документы \"Поступление\", " +
                    "проведенные для актуальных склада и товаров."
    )
    @GetMapping("/receipt")
    ResponseEntity<Page<DocumentReceipt>> getAllDocumentsReceipt(@ParameterObject Pageable pageable) {
        return ResponseEntity.ok(documentReceiptRepository.findAll(pageable));
    }

    @Operation(
            summary = "Получить все документы \"Продажа\"",
            description = "Позволяет постранично просмотреть все документы \"Продажа\", " +
                    "проведенные для актуальных склада и товаров."
    )
    @GetMapping("/sale")
    ResponseEntity<Page<DocumentSale>> getAllDocumentsSale(@ParameterObject Pageable pageable) {
        return ResponseEntity.ok(documentSaleRepository.findAll(pageable));
    }

    @Operation(
            summary = "Получить все документы \"Перемещение\"",
            description = "Позволяет постранично просмотреть все документы \"Продажа\", " +
                    "проведенные для актуальных складов и товаров."
    )
    @GetMapping("/moving")
    ResponseEntity<Page<DocumentMoving>> getAllDocumentsMoving(@ParameterObject Pageable pageable) {
        return ResponseEntity.ok(documentMovingRepository.findAll(pageable));
    }

    @Operation(
            summary = "Получить документ \"Поступление\" по ID",
            description = "Позволяет получить один документ \"Поступление\"."
    )
    @GetMapping("/receipt/{id}")
    ResponseEntity<DocumentReceipt> getDocumentReceiptById(
            @PathVariable @Parameter(description = "ID документа \"Поступление\", который нужно найти") Long id
    ) {
        return ResponseEntity.ok(documentReceiptRepository.findById(id).orElseThrow(
                () -> new CustomException("Not found document receipt with id= " + id, HttpStatus.BAD_REQUEST)
        ));
    }

    @Operation(
            summary = "Получить документ \"Продажа\" по ID",
            description = "Позволяет получить один документ \"Продажа\"."
    )
    @GetMapping("/sale/{id}")
    ResponseEntity<DocumentSale> getDocumentSaleById(
            @PathVariable @Parameter(description = "ID документа \"Продажа\", который нужно найти") Long id
    ) {
        return ResponseEntity.ok(documentSaleRepository.findById(id).orElseThrow(
                () -> new CustomException("Not found document sale with id= " + id, HttpStatus.BAD_REQUEST)
        ));
    }

    @Operation(
            summary = "Получить документ \"Перемещение\" по ID",
            description = "Позволяет получить один документ \"Перемещение\"."
    )
    @GetMapping("/moving/{id}")
    ResponseEntity<DocumentMoving> getDocumentMovingById(
            @PathVariable @Parameter(description = "ID документа \"Перемещение\", который нужно найти") Long id
    ) {
        return ResponseEntity.ok(documentMovingRepository.findById(id).orElseThrow(
                () -> new CustomException("Not found document moving with id= " + id, HttpStatus.BAD_REQUEST)
        ));
    }

    @Operation(
            summary = "Добавить новый документ \"Поступление\"",
            description = "Документ \"Поступление\" заводится при поступлении товара на склад и содержит: " +
                    "список товаров, их количество, закупочные цены, а также склад, на который поступают товары."
    )
    @PostMapping("/receipt")
    ResponseEntity<List<DocumentReceipt>> createDocumentReceipt(
            @Valid @RequestBody DocumentReceiptModel documentReceiptModel
    ) {
        return ResponseEntity.ok(documentService.createDocumentReceipt(documentReceiptModel));
    }

    @Operation(
            summary = "Добавить новый документ \"Продажа\"",
            description = "Документ \"Продажа\" заводится при продаже товара со склада и содержит: " +
                    "список товаров, их количество, цены продажи, а также склад, с которого списываются товары."
    )
    @PostMapping("/sale")
    ResponseEntity<List<DocumentSale>> createDocumentSale(
            @Valid @RequestBody DocumentSaleModel documentSaleModel
    ) {
        return ResponseEntity.ok(documentService.createDocumentSale(documentSaleModel));
    }

    @Operation(
            summary = "Добавить новый документ \"Перемещение\"",
            description = "Документ \"Перемещение\" заводится при перемещении товара между складами и содержит: " +
                    "список товаров, их количество, а также склады, между которыми перемещаются товары."
    )
    @PostMapping("/moving")
    ResponseEntity<List<DocumentMoving>> createDocumentMoving(
            @Valid @RequestBody DocumentMovingModel documentMovingModel
    ) {
        return ResponseEntity.ok(documentService.createDocumentMoving(documentMovingModel));
    }

}
