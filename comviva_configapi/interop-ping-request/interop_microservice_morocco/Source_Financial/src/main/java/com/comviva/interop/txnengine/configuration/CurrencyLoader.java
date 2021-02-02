package com.comviva.interop.txnengine.configuration;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.comviva.interop.txnengine.entities.Currency;
import com.comviva.interop.txnengine.repositories.CurrencyRepository;

@Component
public class CurrencyLoader {

    @Autowired
    private CurrencyRepository currencyRepository;
    
    private Map<String, String> currencyCodes = new HashMap<>();
    
	public Map<String, String> getCurrencyCodes() {
		return currencyCodes;
	}

    public String getCurrencyByCode(String code) {
        if (null != currencyCodes.get(code)) {
            return currencyCodes.get(code);
        } else {
            List<Currency> currency = currencyRepository.getCurrencyByCode(code);
            if (null != currency && !currency.isEmpty()) {
                currencyCodes.put(code, currency.get(0).getCurrencyCodeNumeric());
                return currency.get(0).getCurrencyCodeNumeric();
            }
        }
        return null;
    }
}