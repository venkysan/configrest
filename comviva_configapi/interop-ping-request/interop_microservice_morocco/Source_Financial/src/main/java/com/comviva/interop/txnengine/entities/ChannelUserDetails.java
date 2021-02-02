package com.comviva.interop.txnengine.entities;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "CHANNEL_USER_DETAILS")
@Setter
@Getter
public class ChannelUserDetails {

    @Id
    @Column(name = "USER_ID")
    private String userId;

    @Column(name = "MSISDN")
    private String msisdn;

    @Column(name = "CHANNEL_USER_CODE")
    private String channelUserCode;

    @Column(name = "DESCRIPTION")
    private String description;

    @Column(name = "TYPE")
    private String type;

    @Column(name = "OPTIONAL")
    private String optional;

    @Column(name = "CREATED_DATE")
    private Date createdDate;

    @Column(name = "UPDATED_DATE")
    private Date updatedDate;

    @Column(name = "COUNTRY_ID")
    private String countryId;
}