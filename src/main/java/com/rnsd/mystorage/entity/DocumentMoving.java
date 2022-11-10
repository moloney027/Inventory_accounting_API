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
import java.util.Objects;

@Schema(description = "Документ \"Перемещение\"")
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class DocumentMoving {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Schema(accessMode = Schema.AccessMode.READ_ONLY, description = "ID документа")
    private Long id;

    @Schema(description = "Номер документа")
    private String number;

    @ManyToOne(cascade = CascadeType.ALL)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "from_storage_id", referencedColumnName = "id")
    private Storage fromStorage;

    @ManyToOne(cascade = CascadeType.ALL)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "to_storage_id", referencedColumnName = "id")
    private Storage toStorage;

    @ManyToOne
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "product_id", referencedColumnName = "id")
    private Product product;

    @Schema(description = "Количество товаров")
    private Long count;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        DocumentMoving that = (DocumentMoving) o;
        return id != null && Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
