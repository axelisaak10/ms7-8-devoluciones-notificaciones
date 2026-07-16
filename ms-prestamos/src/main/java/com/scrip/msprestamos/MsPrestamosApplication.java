package com.scrip.msprestamos;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class MsPrestamosApplication {

    public static void main(String[] args) {
        SpringApplication.run(MsPrestamosApplication.class, args);
    }

}
