package com.rnsd.mystorage.controller;

import com.rnsd.mystorage.entity.Storage;
import com.rnsd.mystorage.exception.CustomException;
import com.rnsd.mystorage.repository.InventoryControlRepository;
import com.rnsd.mystorage.repository.StorageRepository;
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
@Tag(name = "Склады", description = "Взаимодействие со складами")
@RequestMapping("/storages")
public class StorageController {

    private final StorageRepository storageRepository;
    private final InventoryControlRepository inventoryControlRepository;

    @Operation(
            summary = "Получить список всех складов",
            description = "Позволяет постранично просмотреть список всех существующих складов."
    )
    @GetMapping
    ResponseEntity<Page<Storage>> getAllStorages(@ParameterObject Pageable pageable) {
        return ResponseEntity.ok(storageRepository.findAllByArchive(pageable, false));
    }

    @Operation(
            summary = "Получить склад по ID",
            description = "Позволяет получить один склад."
    )
    @GetMapping("/{id}")
    ResponseEntity<Storage> getStorageById(
            @PathVariable @Parameter(description = "ID искомого склада") Long id
    ) {
        return ResponseEntity.ok(storageRepository.findByIdAndArchive(id, false)
                .orElseThrow(() -> new CustomException("Not found storage with id= " + id, HttpStatus.BAD_REQUEST)));
    }

    @Operation(
            summary = "Добавить новый склад",
            description = "Позволяет добавить один новый склад в список складов. Возвращает этот склад."
    )
    @PostMapping
    ResponseEntity<Storage> createStorage(@Valid @RequestBody Storage newStorage) {
        return ResponseEntity.ok(storageRepository.save(newStorage));
    }

    @Operation(
            summary = "Обновить существующий склад по ID",
            description = "Позволяет обновить информацию об одном существующем складе из списка складов по его ID." +
                    "Возвращает этот склад."
    )
    @PutMapping("/{id}")
    ResponseEntity<Storage> updateStorage(
            @Valid @RequestBody Storage newStorage,
            @PathVariable @Parameter(description = "ID склада, который нужно обновить") Long id
    ) {
        return storageRepository.findById(id)
                .map(x -> {
                    x.setName(newStorage.getName());
                    return ResponseEntity.ok(storageRepository.save(x));
                })
                .orElseThrow(() -> new CustomException("Not found storage with id= " + id, HttpStatus.BAD_REQUEST));
    }

    @Operation(
            summary = "Переместить все склады в архив безвозвратно",
            description = "Позволяет архивировать все существующие склады. С архивированными складами невозможно " +
                    "будет взаимодействовать. В отличие от операции удаления операция архивирования складов не " +
                    "влечет за собой удаления связанных с этими складами документов всех типов (т.е. таких " +
                    "документов, в которых данные склады присутствовали). Таким образом история всех проведенных " +
                    "документов для архивированных складов сохраняется, но сами склады уже не смогут быть " +
                    "использованы в дальнейшем. Возвращает эти склады."
    )
    @PutMapping("/all-move-archive")
    ResponseEntity<List<Storage>> archiveAllStorages() {
        return ResponseEntity.ok(storageRepository.findAll().stream()
                .map(x -> {
                    x.setArchive(true);
                    inventoryControlRepository.deleteByStorage(x);
                    return storageRepository.save(x);
                }).collect(Collectors.toList()));
    }

    @Operation(
            summary = "Переместить один склад по ID в архив безвозвратно",
            description = "Позволяет архивировать один существующий склад по его ID. С архивированным складом " +
                    "невозможно будет взаимодействовать. В отличие от операции удаления операция архивирования " +
                    "склада не влечет за собой удаления связанных с этим складом документов всех типов (т.е. таких " +
                    "документов, в которых данный склад присутствовал). Таким образом история всех проведенных " +
                    "документов для этого склада сохраняется, но сам склад уже не может быть использован в дальнейшем." +
                    "Возвращает этот склад."
    )
    @PutMapping("move-archive/{id}")
    ResponseEntity<Storage> archiveStorage(
            @PathVariable @Parameter(description = "ID склада, который нужно архивировать") Long id
    ) {
        return storageRepository.findById(id)
                .map(x -> {
                    x.setArchive(true);
                    inventoryControlRepository.deleteByStorage(x);
                    return ResponseEntity.ok(storageRepository.save(x));
                })
                .orElseThrow(() -> new CustomException("Not found storage with id= " + id, HttpStatus.BAD_REQUEST));
    }

    @Operation(
            summary = "Удалить существующий архивированный склад по ID",
            description = "Позволяет удалить один существующий архивированный склад по его ID. Не архивированный " +
                    "склад удалить нельзя. Вместе со складом удалятся и документы всех типов, в которых данный " +
                    "склад присутствовал. Возвращается http код."
    )
    @DeleteMapping("/{id}")
    ResponseEntity<?> deleteStorage(
            @PathVariable @Parameter(description = "ID склада, который нужно удалить") Long id
    ) {
        if (storageRepository.findById(id).orElseThrow(
                () -> new CustomException("Not found storage with id= " + id, HttpStatus.BAD_REQUEST)
        ).getArchive()) {
            storageRepository.deleteById(id);
        } else {
            throw new CustomException("Unable to delete non-archived storage", HttpStatus.BAD_REQUEST);
        }
        return ResponseEntity.ok().build();
    }

    @Operation(
            summary = "Удалить все архивированные склады",
            description = "Позволяет удалить все существующие архивированные склады. Не архивированные склады " +
                    "удалить нельзя. Вместе со складами удалятся и документы всех типов, в которых данные склады " +
                    "присутствовали. Возвращается http код."
    )
    @DeleteMapping
    ResponseEntity<?> deleteAllStorages() {
        storageRepository.deleteAllById(
                storageRepository.findByArchive(true).stream().map(Storage::getId).collect(Collectors.toList())
        );
        return ResponseEntity.ok().build();
    }
}
