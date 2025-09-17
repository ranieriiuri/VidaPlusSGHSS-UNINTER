package com.vidaplus.sghss_backend.controller;

import com.vidaplus.sghss_backend.model.AuditLog;
import com.vidaplus.sghss_backend.repository.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/audit")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AuditLogController {

    private final AuditLogRepository auditLogRepository;

    @GetMapping
    public List<AuditLog> listarTodos() {
        return auditLogRepository.findAll();
    }

    @GetMapping("/usuario/{id}")
    public List<AuditLog> porUsuario(@PathVariable Long id) {
        return auditLogRepository.findByUsuarioId(id);
    }
}
