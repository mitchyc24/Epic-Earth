package com.wlu.epic_earth;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class EpicEarthApplication {

    public static void main(String[] args) {
        // Load environment variables from .env file
        Dotenv dotenv = Dotenv.load();
        System.setProperty("NASA_API_KEY", dotenv.get("NASA_API_KEY"));
        System.setProperty("DB_HOST", dotenv.get("DB_HOST"));
        System.setProperty("DB_URL", dotenv.get("DB_URL"));
        System.setProperty("DB_USER", dotenv.get("DB_USER"));
        System.setProperty("DB_PASS", dotenv.get("DB_PASS"));

        SpringApplication.run(EpicEarthApplication.class, args);
    }
}