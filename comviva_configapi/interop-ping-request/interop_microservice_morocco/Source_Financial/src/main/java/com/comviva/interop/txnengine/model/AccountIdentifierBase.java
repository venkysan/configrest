package com.comviva.interop.txnengine.model;

import javax.annotation.Generated;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * AccountIdentifierBase
 */
@Setter
@Getter
@ToString
@Generated(value = "class io.swagger.codegen.languages.SpringCodegen", date = "2019-04-03T13:52:58.018+05:30")
public class AccountIdentifierBase {
    // Provides the account identifier type.
    public enum KeyEnum {
        msisdn("msisdn"), //
        ;

        private String value;

        KeyEnum(String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return String.valueOf(value);
        }
    }

    private KeyEnum key = null;
    private String value = null;

}