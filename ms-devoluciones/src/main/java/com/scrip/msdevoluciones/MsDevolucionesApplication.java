package com.scrip.msdevoluciones;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class MsDevolucionesApplication {

    public static void main(String[] args) {
        SpringApplication.run(MsDevolucionesApplication.class, args);
    }

}
