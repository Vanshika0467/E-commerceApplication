package com.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages ="com.demo")
public class ECommerceApplication {
    
	public static void main(String[] args) {
		SpringApplication.run(ECommerceApplication.class, args);
		System.out.println("E-Commerce Application");
	}

}
