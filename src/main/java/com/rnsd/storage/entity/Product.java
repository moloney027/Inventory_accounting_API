package com.rnsd.storage.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Set;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String article;
    private String name;
    private BigDecimal lastPurchasePrice;
    private BigDecimal lastSalePrice;

    @ManyToOne
    private Storage storage;

    @ManyToMany
    private Set<DocumentReceipt> documentsReceipt;

    @ManyToMany
    private Set<DocumentSale> documentsSale;

    @ManyToMany
    private Set<DocumentMoving> documentsMoving;
}