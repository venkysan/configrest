package com.comviva.interop.txnengine.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "languages")
@Setter
@Getter
public class Languages {

    @Column(name = "language")
    private String language;

    @Id
    @Column(name = "langCode")
    private String langCode;

}