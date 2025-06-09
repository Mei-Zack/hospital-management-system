package com.hims;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableTransactionManagement
public class HimsApplication {

    public static void main(String[] args) {
        SpringApplication.run(HimsApplication.class, args);
    }
}