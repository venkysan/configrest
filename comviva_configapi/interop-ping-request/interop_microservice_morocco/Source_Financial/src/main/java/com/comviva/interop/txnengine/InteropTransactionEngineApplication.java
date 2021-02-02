package com.comviva.interop.txnengine;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;

import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SpringBootApplication
@EnableSwagger2
@EnableAsync
@ComponentScan(basePackages = "com.comviva.interop.txnengine")
public class InteropTransactionEngineApplication {

	public static void main(String[] args) {
		SpringApplication.run(InteropTransactionEngineApplication.class, args);
	}

}