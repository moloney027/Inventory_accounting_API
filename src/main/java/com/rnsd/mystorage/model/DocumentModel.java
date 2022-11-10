package com.rnsd.mystorage.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

import javax.validation.constraints.NotBlank;

@AllArgsConstructor
@Getter
public abstract class DocumentModel {

    @NotBlank
    private final String documentNumber;
}
