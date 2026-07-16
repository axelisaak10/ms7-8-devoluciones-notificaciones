package com.scrip.msprestamos.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

@FeignClient(name = "ms-notificaciones")
public interface NotificacionClient {

    @PostMapping("/api/v1/notificaciones")
    void enviarNotificacion(@RequestBody Map<String, Object> request);
}
