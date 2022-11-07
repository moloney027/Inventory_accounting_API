package com.rnsd.storage.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Set;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class DocumentMoving {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String number;

    @ManyToOne
    @JoinColumn(name = "from_storage_id", referencedColumnName = "id")
    private Storage fromStorage;

    @ManyToOne
    @JoinColumn(name = "to_storage_id", referencedColumnName = "id")
    private Storage toStorage;

    @ManyToMany(mappedBy = "documentsMoving")
    private Set<Product> products;
}
