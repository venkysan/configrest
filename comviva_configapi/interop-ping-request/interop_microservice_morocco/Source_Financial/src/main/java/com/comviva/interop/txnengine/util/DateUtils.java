package com.comviva.interop.txnengine.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public final class DateUtils {

    public final static String TRANSMISSION_DATE_TIME = "YYMMDDhhmm";
    public final static String TRANSACTION_LOCAL_DATE_TIME = "YYMMDDhhmmss";
    public final static String EXCHANGE_DATE = "MMDD";
    public final static String SETTLEMENT_DATE = "YYMMDD";

    private DateUtils() {
    }

    public static String getTransmissionDateTime(Date date) {
        DateFormat format = new SimpleDateFormat(TRANSMISSION_DATE_TIME);
        return format.format(date);
    }

    public static Date getTransmissionDateTime(String strDate) {
        DateFormat df = new SimpleDateFormat(TRANSMISSION_DATE_TIME);
        try {
            return df.parse(strDate);
        } catch (ParseException e) {
            return new Date();
        }
    }

    public static String getTransactionLocalDateTime(Date date) {
        DateFormat format = new SimpleDateFormat(TRANSACTION_LOCAL_DATE_TIME);
        return format.format(date);
    }

    public static Date getTransactionLocalDateTime(String strDate) {
        DateFormat df = new SimpleDateFormat(TRANSACTION_LOCAL_DATE_TIME);
        try {
            return df.parse(strDate);
        } catch (ParseException e) {
            return new Date();
        }
    }

    public static String getExchangeDate(Date date) {
        DateFormat format = new SimpleDateFormat(EXCHANGE_DATE);
        return format.format(date);
    }

    public static Date getExchangeDate(String strDate) {
        DateFormat df = new SimpleDateFormat(EXCHANGE_DATE);
        try {
            return df.parse(strDate);
        } catch (ParseException e) {
            return new Date();
        }
    }

    public static String getSettlementDate(Date date) {
        DateFormat format = new SimpleDateFormat(SETTLEMENT_DATE);
        return format.format(date);
    }

    public static Date getSettlementDate(String strDate) {
        DateFormat df = new SimpleDateFormat(SETTLEMENT_DATE);
        try {
            return df.parse(strDate);
        } catch (ParseException e) {
            return new Date();
        }
    }

}