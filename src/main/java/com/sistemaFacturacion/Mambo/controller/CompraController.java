package com.sistemaFacturacion.Mambo.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sistemaFacturacion.Mambo.Service.CompraService;
import com.sistemaFacturacion.Mambo.mape.dto.CompraDTO;
import com.sistemaFacturacion.Mambo.mape.dto.CompraRequestDTO;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;


@RestController
@RequestMapping("api/compras")
@RequiredArgsConstructor
public class CompraController {
    private final CompraService compraService;

    @PostMapping("/carrito")
    public ResponseEntity<CompraDTO> guardarCompra(@RequestBody CompraRequestDTO dto) {
        String dni = SecurityContextHolder.getContext().getAuthentication().getName();
        CompraDTO compraCreada = compraService.guardarCarrito(dto, dni);
        
        return ResponseEntity.ok(compraCreada);
    }
    
}
