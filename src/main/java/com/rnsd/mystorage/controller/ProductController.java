package com.rnsd.mystorage.controller;

import com.rnsd.mystorage.entity.Product;
import com.rnsd.mystorage.exception.CustomException;
import com.rnsd.mystorage.repository.InventoryControlRepository;
import com.rnsd.mystorage.repository.ProductRepository;
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
import java.util.stream.Collectors;

@RestController
@AllArgsConstructor
@Tag(
        name = "Товары",
        description = "Взаимодействие с товарами, включая те товары, которые еще ни разу не поставлялись на склад"
)
@RequestMapping("/products")
public class ProductController {

    private final ProductRepository productRepository;
    private final InventoryControlRepository inventoryControlRepository;


    @Operation(
            summary = "Получить список всех товаров",
            description = "Позволяет постранично просмотреть список всех существующих товаров, " +
                    "в том числе и тех, что еще ни разу не поступали на склад."
    )
    @GetMapping
    ResponseEntity<Page<Product>> getAllProducts(@ParameterObject Pageable pageable) {
        return ResponseEntity.ok(productRepository.findAllByArchive(pageable, false));
    }

    @Operation(
            summary = "Получить товар по ID",
            description = "Позволяет получить один товар, в том числе и тот, что еще ни разу не поступал на склад."
    )
    @GetMapping("/{id}")
    ResponseEntity<Product> getProductById(
            @PathVariable @Parameter(description = "ID искомого товара") Long id
    ) {
        return ResponseEntity.ok(productRepository.findByIdAndArchive(id, false).orElseThrow(
                () -> new CustomException("Not found product with id= " + id, HttpStatus.BAD_REQUEST)
        ));
    }

    @Operation(
            summary = "Добавить новый товар",
            description = "Позволяет добавить один новый товар в список товаров (не на склад)."
    )
    @PostMapping
    ResponseEntity<Product> createProduct(@Valid @RequestBody Product newProduct) {
        return ResponseEntity.ok(productRepository.save(newProduct));
    }

    @Operation(
            summary = "Обновить существующий товар по ID",
            description = "Позволяет обновить информацию об одном существующем товаре из списка товров по его ID."
    )
    @PutMapping("/{id}")
    ResponseEntity<Product> updateProduct(
            @Valid @RequestBody Product newProduct,
            @PathVariable @Parameter(description = "ID товара, который нужно обновить") Long id
    ) {
        return productRepository.findById(id)
                .map(x -> {
                    x.setArticle(newProduct.getArticle());
                    x.setName(newProduct.getName());
                    x.setLastPurchasePrice(newProduct.getLastPurchasePrice());
                    x.setLastSalePrice(newProduct.getLastSalePrice());
                    return ResponseEntity.ok(productRepository.save(x));
                })
                .orElseThrow(() -> new CustomException("Not found product with id= " + id, HttpStatus.BAD_REQUEST));
    }

    @Operation(
            summary = "Переместить все товары в архив безвозвратно",
            description = "Позволяет архивировать все существующие товары (которых еще нет на складах и которые уже " +
                    "имеются на складах). С архивированными товарами невозможно будет взаимодействовать. В отличие " +
                    "от операции удаления операция архивирования товаров не влечет за собой удаления связанных с " +
                    "этими товарами документов всех типов (т.е. таких документов, в которых данные товары " +
                    "присутствовали). Таким образом история всех проведенных документов для архивированных товаров " +
                    "сохраняется, но сами товары уже не смогут быть использованы в дальнейшем."
    )
    @PutMapping("/all-move-archive")
    ResponseEntity<List<Product>> archiveAllProducts() {
        return ResponseEntity.ok(productRepository.findAll().stream()
                .map(x -> {
                    x.setArchive(true);
                    inventoryControlRepository.deleteByProduct(x);
                    return productRepository.save(x);
                }).collect(Collectors.toList()));
    }

    @Operation(
            summary = "Переместить один товар по ID в архив безвозвратно",
            description = "Позволяет архивировать один существующий товар (со склада или еще не поступившего ни на " +
                    "один склад) по его ID. С архивированным товаром невозможно будет взаимодействовать. В отличие " +
                    "от операции удаления операция архивирования товара не влечет за собой удаления связанных с этим " +
                    "товаром документов всех типов (т.е. таких документов, в которых данный товар присутствовал). " +
                    "Таким образом история всех проведенных документов для этого товара сохраняется, но сам товар " +
                    "уже не может быть использован в дальнейшем."
    )
    @PutMapping("/move-archive/{id}")
    ResponseEntity<Product> archiveProduct(
            @PathVariable @Parameter(description = "ID товара, который нужно архивировать") Long id
    ) {
        return productRepository.findById(id)
                .map(x -> {
                    x.setArchive(true);
                    inventoryControlRepository.deleteByProduct(x);
                    return ResponseEntity.ok(productRepository.save(x));
                })
                .orElseThrow(() -> new CustomException("Not found product with id= " + id, HttpStatus.BAD_REQUEST));
    }

    @Operation(
            summary = "Удалить существующий архивированный товар по ID",
            description = "Позволяет удалить один существующий товар из списка товров по его ID. Не " +
                    "архивированный товар удалить нельзя. Вместе с товаром удалятся и документы всех типов, " +
                    "в которых данный товар присутствовал."
    )
    @DeleteMapping("/{id}")
    ResponseEntity<?> deleteProduct(
            @PathVariable @Parameter(description = "ID товара, который нужно удалить") Long id) {
        if (productRepository.findById(id).orElseThrow(
                () -> new CustomException("Not found product with id= " + id, HttpStatus.BAD_REQUEST)).getArchive()
        ) {
            productRepository.deleteById(id);
        } else {
            throw new CustomException("Unable to delete non-archived product", HttpStatus.BAD_REQUEST);
        }
        return ResponseEntity.ok().build();
    }

    @Operation(
            summary = "Удалить все архивированные товары",
            description = "Позволяет удалить все существующие товары. Не архивированные товары удалить нельзя. " +
                    "Вместе с товарами удалятся и документы всех типов, в которых данные товары присутствовали."
    )
    @DeleteMapping
    ResponseEntity<?> deleteAllProducts() {
        productRepository.deleteAllById(
                productRepository.findByArchive(true).stream().map(Product::getId).collect(Collectors.toList())
        );
        return ResponseEntity.ok().build();
    }
}
