package com.wowraid.jobspoon;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class JobSpoonApplication {

    public static void main(String[] args) {
        SpringApplication.run(JobSpoonApplication.class, args);
    }

}
