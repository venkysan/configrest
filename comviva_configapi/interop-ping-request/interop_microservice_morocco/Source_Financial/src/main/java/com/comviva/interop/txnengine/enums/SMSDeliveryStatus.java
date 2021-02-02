package com.comviva.interop.txnengine.enums;

public enum SMSDeliveryStatus {

    N("Not Delivered"),//
    Y("Delivered"), //
    U("Delivery Initiated"),//
    ;

    private String delivaryStatus;

    private SMSDeliveryStatus(String delivaryStatus) {
        this.delivaryStatus = delivaryStatus;
    }

    public String getDelivaryStatus() {
        return delivaryStatus;
    }
}
