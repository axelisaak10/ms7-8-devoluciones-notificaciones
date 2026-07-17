package com.scrip.msuserauthregister.controller;

import com.scrip.msuserauthregister.dto.UserStatusResponse;
import com.scrip.msuserauthregister.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/internal/users")
@RequiredArgsConstructor
public class InternalUserController {

    private final UserService userService;

    @GetMapping("/{id}/status")
    public UserStatusResponse getStatus(
            @PathVariable UUID id,
            JwtAuthenticationToken authentication
    ) {
        String tokenUserId = authentication.getToken().getClaimAsString("user_id");
        boolean administrator = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch("ROLE_ADMINISTRADOR"::equals);
        boolean identityService = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch("SCOPE_identity.read"::equals);

        if (!identityService && !administrator && !id.toString().equals(tokenUserId)) {
            throw new AccessDeniedException("El token no pertenece al usuario solicitado");
        }
        return userService.findStatusById(id);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> notFound(IllegalArgumentException exception) {
        return ResponseEntity.status(404).body(Map.of("error", exception.getMessage()));
    }
}
