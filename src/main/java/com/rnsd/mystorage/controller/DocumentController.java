package com.rnsd.mystorage.controller;

import com.opencsv.CSVWriter;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import com.rnsd.mystorage.entity.DocumentMoving;
import com.rnsd.mystorage.entity.DocumentReceipt;
import com.rnsd.mystorage.entity.DocumentSale;
import com.rnsd.mystorage.exception.CustomException;
import com.rnsd.mystorage.model.*;
import com.rnsd.mystorage.model.csv.DocumentMovingCsvModel;
import com.rnsd.mystorage.model.csv.DocumentReceiptCsvModel;
import com.rnsd.mystorage.model.csv.DocumentSaleCsvModel;
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
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

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

    @Operation(
            summary = "Добавить новый документ \"Поступление\" в формате CSV",
            description = "Документ \"Поступление\" заводится при перемещении товара между складами и содержит: " +
                    "список товаров, их количество, а также склады, между которыми перемещаются товары."
    )
    @PostMapping(value = "/upload-csv-receipt", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ResponseEntity<List<DocumentReceipt>> uploadCsvDocumentReceipt(@RequestParam("file") MultipartFile file) {
        List<DocumentReceipt> resultDocumentsReceipt = new ArrayList<>();
        if (file.isEmpty()) {
            throw new CustomException("Please select a CSV file to upload", HttpStatus.BAD_REQUEST);
        } else {
            try (Reader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
                CsvToBean<DocumentReceiptCsvModel> csvToBean = new CsvToBeanBuilder<DocumentReceiptCsvModel>(reader)
                        .withType(DocumentReceiptCsvModel.class)
                        .withIgnoreLeadingWhiteSpace(true)
                        .build();
                List<DocumentReceiptCsvModel> documentsReceipt = csvToBean.parse();
                for (DocumentReceiptCsvModel docModel : documentsReceipt) {
                    resultDocumentsReceipt.addAll(documentService.createDocumentReceipt(new DocumentReceiptModel(
                            docModel.getNumber(),
                            docModel.getStorageId(),
                            List.of(new ProductInDocumentModel(
                                    docModel.getProductId(),
                                    docModel.getCount(),
                                    docModel.getPurchasePrice()
                            ))
                    )));
                }
            } catch (Exception ex) {
                throw new CustomException("An error occurred while processing the CSV file", HttpStatus.BAD_REQUEST);
            }
        }
        return ResponseEntity.ok(resultDocumentsReceipt);
    }

    @Operation(
            summary = "Добавить новый документ \"Продажа\" в формате CSV",
            description = "Документ \"Продажа\" заводится при перемещении товара между складами и содержит: " +
                    "список товаров, их количество, а также склады, между которыми перемещаются товары."
    )
    @PostMapping(value = "/upload-csv-sale", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ResponseEntity<List<DocumentSale>> uploadCsvDocumentSale(@RequestParam("file") MultipartFile file) {
        List<DocumentSale> resultDocumentsSale = new ArrayList<>();
        if (file.isEmpty()) {
            throw new CustomException("Please select a CSV file to upload", HttpStatus.BAD_REQUEST);
        } else {
            try (Reader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
                CsvToBean<DocumentSaleCsvModel> csvToBean = new CsvToBeanBuilder<DocumentSaleCsvModel>(reader)
                        .withType(DocumentSaleCsvModel.class)
                        .withIgnoreLeadingWhiteSpace(true)
                        .build();
                List<DocumentSaleCsvModel> documentsSale = csvToBean.parse();
                for (DocumentSaleCsvModel docModel : documentsSale) {
                    resultDocumentsSale.addAll(documentService.createDocumentSale(new DocumentSaleModel(
                            docModel.getNumber(),
                            docModel.getStorageId(),
                            List.of(new ProductInDocumentModel(
                                    docModel.getProductId(),
                                    docModel.getCount(),
                                    docModel.getSalePrice()
                            ))
                    )));
                }
            } catch (Exception ex) {
                throw new CustomException("An error occurred while processing the CSV file", HttpStatus.BAD_REQUEST);
            }
        }
        return ResponseEntity.ok(resultDocumentsSale);
    }

    @Operation(
            summary = "Добавить новый документ \"Перемещение\" в формате CSV",
            description = "Документ \"Перемещение\" заводится при перемещении товара между складами и содержит: " +
                    "список товаров, их количество, а также склады, между которыми перемещаются товары."
    )
    @PostMapping(value = "/upload-csv-moving", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ResponseEntity<List<DocumentMoving>> uploadCsvDocumentMoving(@RequestParam("file") MultipartFile file) {
        List<DocumentMoving> resultDocumentsMoving = new ArrayList<>();
        if (file.isEmpty()) {
            throw new CustomException("Please select a CSV file to upload", HttpStatus.BAD_REQUEST);
        } else {
            try (Reader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
                CsvToBean<DocumentMovingCsvModel> csvToBean = new CsvToBeanBuilder<DocumentMovingCsvModel>(reader)
                        .withType(DocumentMovingCsvModel.class)
                        .withIgnoreLeadingWhiteSpace(true)
                        .build();
                List<DocumentMovingCsvModel> documentsMoving = csvToBean.parse();
                for (DocumentMovingCsvModel docModel : documentsMoving) {
                    resultDocumentsMoving.addAll(documentService.createDocumentMoving(new DocumentMovingModel(
                            docModel.getNumber(),
                            docModel.getFromStorage(),
                            docModel.getToStorage(),
                            List.of(new ProductInDocumentForMovingModel(
                                    docModel.getProductId(),
                                    docModel.getCount()
                            ))
                    )));
                }
            } catch (Exception ex) {
                throw new CustomException("An error occurred while processing the CSV file", HttpStatus.BAD_REQUEST);
            }
        }
        return ResponseEntity.ok(resultDocumentsMoving);
    }

    @Operation(
            summary = "Получить все документы \"Поступление\" в формате CSV",
            description = "Позволяет просмотреть все документы \"Поступление\" в CSV файле, " +
                    "проведенные для актуальных склада и товаров."
    )
    @GetMapping("/export-csv-receipt")
    public void exportCsvDocumentReceipt(HttpServletResponse response) throws Exception {
        String filename = "DocumentReceipt.csv";
        response.setContentType("text/csv");
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"");
        StatefulBeanToCsv<DocumentReceiptCsvModel> writer =
                new StatefulBeanToCsvBuilder<DocumentReceiptCsvModel>
                        (response.getWriter())
                        .withQuotechar(CSVWriter.NO_QUOTE_CHARACTER)
                        .withSeparator(CSVWriter.DEFAULT_SEPARATOR)
                        .withOrderedResults(false)
                        .build();
        writer.write(StreamSupport.stream(documentReceiptRepository.findAll().spliterator(), false)
                .map(DocumentReceiptCsvModel::new)
                .collect(Collectors.toList()));
    }

    @Operation(
            summary = "Получить все документы \"Продажа\" в формате CSV",
            description = "Позволяет постранично просмотреть все документы \"Продажа\" в CSV файле, " +
                    "проведенные для актуальных склада и товаров."
    )
    @GetMapping("/export-csv-sale")
    public void exportCsvDocumentSale(HttpServletResponse response) throws Exception {
        String filename = "DocumentSale.csv";
        response.setContentType("text/csv");
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"");
        StatefulBeanToCsv<DocumentSaleCsvModel> writer =
                new StatefulBeanToCsvBuilder<DocumentSaleCsvModel>
                        (response.getWriter())
                        .withQuotechar(CSVWriter.NO_QUOTE_CHARACTER)
                        .withSeparator(CSVWriter.DEFAULT_SEPARATOR)
                        .withOrderedResults(false)
                        .build();
        writer.write(StreamSupport.stream(documentSaleRepository.findAll().spliterator(), false)
                .map(DocumentSaleCsvModel::new)
                .collect(Collectors.toList()));
    }

    @Operation(
            summary = "Получить все документы \"Перемещение\" в формате CSV",
            description = "Позволяет постранично просмотреть все документы \"Перемещение\" в CSV файле, " +
                    "проведенные для актуальных склада и товаров."
    )
    @GetMapping("/export-csv-moving")
    public void exportCsvDocumentMoving(HttpServletResponse response) throws Exception {
        String filename = "DocumentMoving.csv";
        response.setContentType("text/csv");
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"");
        StatefulBeanToCsv<DocumentMovingCsvModel> writer =
                new StatefulBeanToCsvBuilder<DocumentMovingCsvModel>
                        (response.getWriter())
                        .withQuotechar(CSVWriter.NO_QUOTE_CHARACTER)
                        .withSeparator(CSVWriter.DEFAULT_SEPARATOR)
                        .withOrderedResults(false)
                        .build();
        writer.write(StreamSupport.stream(documentMovingRepository.findAll().spliterator(), false)
                .map(DocumentMovingCsvModel::new)
                .collect(Collectors.toList()));
    }
}
