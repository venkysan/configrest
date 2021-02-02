package com.comviva.interop.txnengine.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "currency")
@Setter
@Getter
public class Currency {

    @Id
    @Column(name = "country")
    private String country;

    @Column(name = "code")
    private String code;

    @Column(name = "currency")
    private String currencyName;

    @Column(name = "code_numeric")
    private String currencyCodeNumeric;

}