package com.comviva.interop.txnengine.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.comviva.interop.txnengine.entities.SmsDelivery;
import com.comviva.interop.txnengine.enums.LogConstants;
import com.comviva.interop.txnengine.enums.SMSDeliveryStatus;
import com.comviva.interop.txnengine.exception.InteropException;

public class SMSUtility {

    private static final Logger LOGGER = LoggerFactory.getLogger(SMSUtility.class);
    private static int  idGenLen=10000000;
    private static String  idGenLenStr="00000000";

    private SMSUtility() {
        super();
    }

    public static String sendSms(String apiKey, String message, String sender, String numbers, String url) {
        final StringBuilder stringBuilder = new StringBuilder();
        // Construct data
        String apiKeyStr = "apikey=" + apiKey;
        String messageStr = "&message=" + message;
        String senderStr = "&sender=" + sender;
        String numbersStr = "&numbers=" + numbers;
        try {
            // Send data
            HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
            String data = apiKeyStr + numbersStr + messageStr + senderStr;
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Length", Integer.toString(data.length()));
            conn.getOutputStream().write(data.getBytes(StandardCharsets.UTF_8));
            final BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));

            String line;
            while ((line = rd.readLine()) != null) {
                stringBuilder.append(line);
            }
            rd.close();

        } catch (IOException e) {
            String exceptionMessage = LoggerUtil.printLogForSMS(LogConstants.OUTGOING_REQUEST_EVENT_TYPE.getValue(), numbers, e, message, LogConstants.SMS_NOTIFICATION.getValue());
			LOGGER.info("Exception in sms utility..{}", exceptionMessage);
			throw new InteropException("500", "INTEROP");
        }
        return stringBuilder.toString();
    }

    public static String generateEightDigitUniqueId() {
        SecureRandom generator = new SecureRandom();
        generator.setSeed(System.currentTimeMillis());
        int i = generator.nextInt(idGenLen) % idGenLen;
        java.text.DecimalFormat f = new java.text.DecimalFormat(idGenLenStr);
        return f.format(i);
    }

    public static SmsDelivery prepareSMSDelivery(String message, String msisdn, String languageCode, String serviceType,
            String nodeName) {
        SmsDelivery smsDelivery = new SmsDelivery();
        smsDelivery.setSmsId(SMSUtility.generateEightDigitUniqueId());
        smsDelivery.setCreatedon(new Date());
        smsDelivery.setMessage(message);
        smsDelivery.setMsisdn(msisdn);
        smsDelivery.setLanguageCode(languageCode);
        smsDelivery.setRetryCount(0);
        smsDelivery.setStatus(SMSDeliveryStatus.N.toString());
        smsDelivery.setServiceType(serviceType);
        smsDelivery.setNodeName(nodeName);
        return smsDelivery;
    }
}
