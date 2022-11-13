package com.rnsd.mystorage.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.Hibernate;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Objects;

@Schema(description = "Документ \"Продажа\"")
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class DocumentSale {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Schema(accessMode = Schema.AccessMode.READ_ONLY, description = "ID документа")
    private Long id;

    @Schema(description = "Номер документа")
    private String number;

    @ManyToOne(cascade = CascadeType.ALL, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "storage_id", referencedColumnName = "id")
    private Storage storage;

    @ManyToOne(cascade = CascadeType.ALL, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "product_id", referencedColumnName = "id")
    private Product product;

    @Schema(description = "Количество товаров")
    private Long count;

    @Schema(description = "Цена продажи")
    private BigDecimal salePrice;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        DocumentSale that = (DocumentSale) o;
        return id != null && Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
