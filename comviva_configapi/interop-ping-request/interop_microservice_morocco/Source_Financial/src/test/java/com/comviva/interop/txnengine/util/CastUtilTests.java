package com.comviva.interop.txnengine.util;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public class CastUtilTests {

    @Test
    public void getLanguageWithCountryCodeTest() {
        assertThat(CastUtils.getLanguageWithCountryCode(CastUtils.ENGLISH_LANGUAGE_CODE), is(notNullValue()));
        assertThat(CastUtils.getLanguageWithCountryCode(CastUtils.ENGLISH_LANGUAGE_CODE), is("en_US"));
        assertThat(CastUtils.getLanguageWithCountryCode(CastUtils.FRANCE_LANGUAGE_CODE), is(notNullValue()));
        assertThat(CastUtils.getLanguageWithCountryCode(CastUtils.FRANCE_LANGUAGE_CODE), is("fr_FR"));
        assertThat(CastUtils.getLanguageWithCountryCode("ar"), is("ar"));
    }
}
