package com.scrip.msprestamos.controller;

import com.scrip.msprestamos.dto.SancionRequest;
import com.scrip.msprestamos.entity.Sancion;
import com.scrip.msprestamos.service.SancionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/sanciones")
@RequiredArgsConstructor
public class SancionController {

    private final SancionService sancionService;

    @PostMapping
    public ResponseEntity<?> crearSancion(@Valid @RequestBody SancionRequest request) {
        try {
            Sancion sancion = sancionService.registrarSancion(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(sancion);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error inesperado al generar la sanción: " + e.getMessage()));
        }
    }

    @GetMapping("/usuario/{usuarioId}/activas")
    public ResponseEntity<List<Sancion>> obtenerSancionesActivas(@PathVariable("usuarioId") UUID usuarioId) {
        return ResponseEntity.ok(sancionService.obtenerSancionesActivas(usuarioId));
    }

    @GetMapping
    public ResponseEntity<List<Sancion>> obtenerTodasSanciones() {
        return ResponseEntity.ok(sancionService.obtenerTodasSanciones());
    }

    @PutMapping("/{id}/pagar")
    public ResponseEntity<?> pagarSancion(@PathVariable("id") UUID id) {
        try {
            Sancion sancion = sancionService.pagarSancion(id);
            return ResponseEntity.ok(sancion);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error inesperado al registrar el pago de la sanción: " + e.getMessage()));
        }
    }
}
