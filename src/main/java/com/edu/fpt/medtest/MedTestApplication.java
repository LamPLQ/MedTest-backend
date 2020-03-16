package com.edu.fpt.medtest;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class MedTestApplication {

    public static void main(String[] args) {
        SpringApplication.run(MedTestApplication.class, args);
        System.out.println("You can start now!");
    }

}
