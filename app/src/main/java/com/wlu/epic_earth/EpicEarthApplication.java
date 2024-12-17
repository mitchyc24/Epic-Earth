package com.wlu.epic_earth;

import io.github.cdimascio.dotenv.Dotenv;

import java.util.logging.Logger;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class EpicEarthApplication {

    private static final Logger logger = Logger.getLogger(EpicEarthApplication.class.getName());


    public static void main(String[] args) {
        // Load environment variables from .env file
        Dotenv dotenv = Dotenv.load();
        System.setProperty("NASA_API_KEY", dotenv.get("NASA_API_KEY"));
        System.setProperty("DB_HOST", dotenv.get("DB_HOST"));
        System.setProperty("DB_URL", dotenv.get("DB_URL"));
        System.setProperty("DB_USER", dotenv.get("DB_USER"));
        System.setProperty("DB_PASS", dotenv.get("DB_PASS"));

        logger.info("Starting Epic Earth application with the following environment variables:");
        logger.info("NASA_API_KEY: " + System.getProperty("NASA_API_KEY"));
        logger.info("DB_HOST: " + System.getProperty("DB_HOST"));
        logger.info("DB_URL: " + System.getProperty("DB_URL"));
        logger.info("DB_USER: " + System.getProperty("DB_USER"));
        logger.info("DB_PASS: " + System.getProperty("DB_PASS"));

        SpringApplication.run(EpicEarthApplication.class, args);
    }
}