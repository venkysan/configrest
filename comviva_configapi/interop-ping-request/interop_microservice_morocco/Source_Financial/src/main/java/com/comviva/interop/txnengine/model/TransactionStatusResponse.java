package com.comviva.interop.txnengine.model;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Generated;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.swagger.annotations.ApiModel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
@ApiModel(description = "success")
@Generated(value = "class io.swagger.codegen.languages.SpringCodegen", date = "2019-03-05T10:52:25.503+05:30")
public class TransactionStatusResponse {

    private String code = null;
    private String mappedCode = null;
    private String message = null;
    private List<TransactionData> data = new ArrayList<>();
    
    public String convertToJSON(TransactionStatusResponse transactionStatusResponse) throws JsonProcessingException {
        return new ObjectMapper().writer(new DefaultPrettyPrinter()).writeValueAsString(transactionStatusResponse);
    }

}