package com.appswella.wisepaise;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableDiscoveryClient
@EnableScheduling
public class ExpenseServiceApplication {
    private static final Logger logger = LoggerFactory.getLogger(ExpenseServiceApplication.class);
    
    public static void main(String[] args) {
        logger.info("Starting Expense Service...");
        try {
            ConfigurableApplicationContext context = SpringApplication.run(ExpenseServiceApplication.class, args);
            logger.info("Expense Service started successfully!");
            
            // Keep the application running
            Thread.currentThread().join();
        } catch (Exception e) {
            logger.error("Error starting Expense Service: ", e);
        }
    }
}