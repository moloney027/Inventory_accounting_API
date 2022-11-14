package com.rnsd.mystorage.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import javax.validation.constraints.Positive;

/**
 * Организация складского учета. Сюда добавляются результаты проведения всех документов. С поступлением нового
 * документа для некоторого склада и товара(ов), нужные значения в соответствующей этим складу и товару(ам) записи
 * в этой таблице обновятся
 */
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class InventoryControl {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne(cascade = CascadeType.ALL)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "storage_id", referencedColumnName = "id")
    private Storage storage;

    @ManyToOne(cascade = CascadeType.ALL)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "product_id", referencedColumnName = "id")
    private Product product;

    @Positive
    private Long count;
}
