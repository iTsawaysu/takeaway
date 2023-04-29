package com.sun.takeaway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * @author sun
 */
@SpringBootApplication
@EnableTransactionManagement
public class TakeawayApplication {
    public static void main(String[] args) {
        SpringApplication.run(TakeawayApplication.class, args);
    }
}
