package com.scrip.msdevoluciones.client;

import com.scrip.msdevoluciones.dto.PrestamoDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@FeignClient(name = "ms-prestamos")
public interface PrestamoClient {

    @GetMapping("/api/v1/prestamos/{id}")
    PrestamoDto obtenerPrestamoPorId(@PathVariable("id") UUID id);

    @PutMapping("/api/v1/prestamos/{id}/devolver")
    void marcarPrestamoComoDevuelto(@PathVariable("id") UUID id);

    @PostMapping("/api/v1/sanciones")
    void crearSancion(@RequestBody Map<String, Object> request);
}
