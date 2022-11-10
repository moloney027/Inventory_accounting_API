package com.rnsd.mystorage.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;

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

    private Long count;
}
