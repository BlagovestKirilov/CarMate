package com.carmate;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class CarMateApplication {

    public static void main(String[] args) {
        SpringApplication.run(CarMateApplication.class, args);
    }

}
