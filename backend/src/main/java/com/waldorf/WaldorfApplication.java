package com.waldorf;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class WaldorfApplication {
    public static void main(String[] args) {
        SpringApplication.run(WaldorfApplication.class, args);
    }
}
