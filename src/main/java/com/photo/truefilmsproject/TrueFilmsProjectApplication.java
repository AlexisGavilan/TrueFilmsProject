package com.photo.truefilmsproject;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class TrueFilmsProjectApplication {
    public static void main(String[] args) {
        SpringApplication.run(TrueFilmsProjectApplication.class, args);
    }
}
