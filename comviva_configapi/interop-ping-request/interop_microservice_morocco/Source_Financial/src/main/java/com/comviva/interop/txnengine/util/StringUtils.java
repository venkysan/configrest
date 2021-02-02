package com.comviva.interop.txnengine.util;

import java.io.StringReader;
import java.math.BigDecimal;
import java.security.SecureRandom;
import java.text.MessageFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.comviva.interop.txnengine.enums.LogConstants;
import com.comviva.interop.txnengine.model.Response;
import com.thoughtworks.xstream.XStream;

public final class StringUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(StringUtils.class);
    
    private static final int TWO = 2;
    private static final char ZERO='0';
    private static final int TWELVE = 12;
    private static final int SIX = 6;
    
    private StringUtils() {
    }

    public static final String leftPad(String str, int len, char padChar) {
        StringBuilder result = new StringBuilder();
        while (result.length() < len - str.length()) {
            result.append(padChar);
        }
        return result.append(str).substring(0, len);
    }

    public static String formatMapKeyValues(Map<String, String> map) {
        StringBuilder strbuiler = new StringBuilder();
        int i = 0;
        Set<String> keys = map.keySet();
        for (String key : keys) {
            i++;
            if (map.get(key) != null) {
                strbuiler.append(key);
                strbuiler.append("=");
                strbuiler.append(map.get(key));
                if (i != keys.size())
                    strbuiler.append("&");
            }
        }
        return strbuiler.toString();
    }

    public static Map<String, String> convertXmlToMap(String xmlStr) {
        XStream xStream = new XStream();
        xStream.alias("map", java.util.Map.class);
        @SuppressWarnings("unchecked")
        HashMap<String, String> valueMap = (HashMap<String, String>) xStream.fromXML(xmlStr);
        return valueMap;
    }

    /**
     * Convert from xml string to model.
     * 
     * @param obj
     * @return String
     */
    public static Response xmlToModel(String xmlString) {
        Response response = null;
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(Response.class);
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            response = (Response) unmarshaller.unmarshal(new StringReader(formatXML(xmlString)));
        } catch (Exception e) {
        	LOGGER.error("Exception occurred while convert from xml to model : {}", e);
        }
        return response;
    }

    private static String formatXML(String xmlString) {
        return xmlString.replaceAll("(<\\?[^<]*\\?>)?", "").replaceAll("xmlns.*?(\"|\').*?(\"|\')", "")
                .replaceAll("(<)(\\w+:)(.*?>)", "$1$3").replaceAll("(</)(\\w+:)(.*?>)", "$1$3")
                .replaceAll("xsi:nil=\"true\"", "").replaceAll("xsi:nil=\"false\"", "");
    }

    public static String msgFormat(String s, Object... args) {
        return new MessageFormat(s).format(args);
    }

    public static boolean checkIsNullOrEmpty(String data) {
        boolean emptyCheck = false;
        if (data == null || data.isEmpty()) {
            emptyCheck = true;
        }
        return emptyCheck;
    }

    public static Date stringToDateFormat(String dateString, String format) {
        Date date = null;
        if (dateString.endsWith("Z")) {
            try {
                SimpleDateFormat s = new SimpleDateFormat(format, Locale.getDefault());// spec for RFC3339 with a 'Z'
                s.setTimeZone(TimeZone.getTimeZone("UTC"));
                date = s.parse(dateString);
            } catch (ParseException pe) {
                String exceptionMessage = LoggerUtil.printLog(LogConstants.STRING_TO_DATE_PARSE_EXCEPTION.getValue(), pe);
                LOGGER.info("Exception while parsing string to date..{}", exceptionMessage);
            }
        }
        return date;
    }

    public static String convertTxnAmountinISO(String amount) {
        BigDecimal bd = new BigDecimal(amount);
        bd = bd.setScale(TWO, BigDecimal.ROUND_CEILING);
        bd = bd.multiply(new BigDecimal("100"));
        int totalAmpount = bd.intValueExact();
        return leftPad(String.valueOf(totalAmpount), TWELVE, ZERO);
    }

    public static String prepareSTAN(String stan) {
        String result = "";
        if (stan.length() < SIX) {
            result = StringUtils.leftPad(stan, SIX, ZERO);
        } else {
            result = stan;
        }
        return result;
    }

    public static String prepareRecoveryReferenceNumber(String retriveRecoveryNumber) {
        String result = "";
        if (retriveRecoveryNumber.length() < TWELVE) {
            result = StringUtils.leftPad(retriveRecoveryNumber, TWELVE - retriveRecoveryNumber.length(), ZERO);
        } else {
            result = retriveRecoveryNumber;
        }
        return result;
    }
    
    public static String generateRRN() {
        SecureRandom generator = new SecureRandom();
        generator.setSeed(System.currentTimeMillis());
        long i = generator.nextInt(1000000000) % 1000000000;
        java.text.DecimalFormat f = new java.text.DecimalFormat("110000000000");
        return f.format(i);
    }
    
    public static String getMaskedData(int length, String symbol) {
        StringBuilder temp = new StringBuilder("");
        for (int i = 0; i < length; i++) {
            temp.append(symbol);
        }
        return temp.toString();
    }

}