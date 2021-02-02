package com.comviva.interop.txnengine.entities;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "sms_delivery")
@Setter
@Getter
public class SmsDelivery {

    @Id
    // @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "sms_id")
    private String smsId;

    @Column(name = "msisdn")
    private String msisdn;

    @Column(name = "message")
    private String message;

    @Column(name = "language_code")
    private String languageCode;

    @Column(name = "status")
    private String status;

    @Column(name = "delivered_on")
    private Date deliveredOn;

    @Column(name = "retry_count")
    private int retryCount;

    @Column(name = "service_type")
    private String serviceType;

    @Column(name = "txn_id")
    private String txnId;

    @Column(name = "txn_status")
    private String txnStatus;

    @Column(name = "transfer_date")
    private Date transferDate;

    @Column(name = "sender")
    private String sender;

    @Column(name = "reciever")
    private String reciever;

    @Column(name = "created_on")
    private Date createdon;

    @Column(name = "node_name")
    private String nodeName;

    public SmsDelivery() {
        super();
    }

}
