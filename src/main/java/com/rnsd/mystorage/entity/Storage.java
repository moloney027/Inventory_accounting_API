package com.rnsd.mystorage.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.Hibernate;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Objects;

@Schema(description = "Склад")
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Storage {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Schema(accessMode = Schema.AccessMode.READ_ONLY, description = "ID склада")
    private Long id;

    @NotBlank
    @Schema(description = "Наименование")
    private String name;

    @NotNull
    @Schema(accessMode = Schema.AccessMode.READ_ONLY, description = "Архивированность")
    private Boolean archive = false;

    public Storage(Long id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Storage storage = (Storage) o;
        return id != null && Objects.equals(id, storage.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
