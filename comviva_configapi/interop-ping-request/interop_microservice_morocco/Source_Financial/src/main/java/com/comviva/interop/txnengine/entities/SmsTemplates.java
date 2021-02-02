package com.comviva.interop.txnengine.entities;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "sms_templates")
@Setter
@Getter
public class SmsTemplates implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private String id;

    @Column(name = "notification_code")
    private String notificationCode;

    @Column(name = "language_code")
    private String languageCode;

    @Column(name = "description")
    private String description;

    public SmsTemplates() {
        super();
    }

}
