package com.comviva.interop.txnengine.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "statuscodes")
@IdClass(StatusCodesId.class)
@Setter
@Getter
public class StatusCodes {

    @Id
    @Column(name = "statuscode")
    private String statuscode;

    @Id
    @Column(name = "entity")
    private String entity;

    @Id
    @Column(name = "lang_code")
    private String langCode;

    @Column(name = "description")
    private String description;

    @Column(name = "mapping_code")
    private String mappingCode;

}