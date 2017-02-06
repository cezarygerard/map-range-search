package com.cgz

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.scheduling.annotation.EnableAsync

@SpringBootApplication
@EnableAsync
class RangeMapApplication {

    static void main(String[] args) {
        SpringApplication.run RangeMapApplication, args
    }
}
