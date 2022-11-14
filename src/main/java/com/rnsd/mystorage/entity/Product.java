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
import javax.validation.constraints.Positive;
import java.math.BigDecimal;
import java.util.Objects;

@Schema(description = "Товар")
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Schema(accessMode = Schema.AccessMode.READ_ONLY, description = "ID товара")
    private Long id;

    @NotBlank
    @Schema(description = "Артикул")
    private String article;

    @NotBlank
    @Schema(description = "Наименование")
    private String name;

    @Positive
    @Schema(accessMode = Schema.AccessMode.READ_ONLY, description = "Цена последней закупки")
    private BigDecimal lastPurchasePrice;

    @Positive
    @Schema(accessMode = Schema.AccessMode.READ_ONLY, description = "Цена последней продажи")
    private BigDecimal lastSalePrice;

    @NotNull
    @Schema(accessMode = Schema.AccessMode.READ_ONLY, description = "Архивированность")
    private Boolean archive = false;

    public Product(Long id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Product product = (Product) o;
        return id != null && Objects.equals(id, product.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}