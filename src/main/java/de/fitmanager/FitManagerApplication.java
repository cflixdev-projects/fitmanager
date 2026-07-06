package de.fitmanager;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

// startklasse. mvn spring-boot:run, dann localhost:8080
@SpringBootApplication
public class FitManagerApplication {

    public static void main(String[] args) {
        SpringApplication.run(FitManagerApplication.class, args);
    }

}
