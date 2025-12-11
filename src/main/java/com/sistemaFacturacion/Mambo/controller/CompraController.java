package com.sistemaFacturacion.Mambo.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sistemaFacturacion.Mambo.Service.CompraService;
import com.sistemaFacturacion.Mambo.mape.dto.CompraDTO;
import com.sistemaFacturacion.Mambo.mape.dto.CompraRequestDTO;

import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("api/compras")
@RequiredArgsConstructor
public class CompraController {
    private final CompraService compraService;

    @PostMapping("/carrito")
    public ResponseEntity<CompraDTO> guardarCompra(@RequestBody CompraRequestDTO dto) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String usuarioLogueadoDni = auth.getName();

        boolean esPersonal = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN") || a.getAuthority().equals("ROLE_VENDEDOR"));

        String dniClienteReal;

        if (esPersonal) {
            String dniFormulario = dto.getDestinatario().getNumDocumento();

            dniClienteReal = dniFormulario;

        } else {
            dniClienteReal = usuarioLogueadoDni;
        }

        CompraDTO compraCreada = compraService.guardarCarrito(dto, dniClienteReal);

        return ResponseEntity.ok(compraCreada);
    }

    @GetMapping("/mis-compras")
    public ResponseEntity<List<CompraDTO>> verMiHistorial(Authentication authentication) {
        String dniUsuario = authentication.getName();

        // Ahora recibimos DTOs, no Entidades
        List<CompraDTO> historial = compraService.obtenerHistorialPorDni(dniUsuario);

        if (historial.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(historial);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CompraDTO> obtenerCompraPorId(@PathVariable Long id) {
        return compraService.obtenerCompraPorId(id)
                .map(dto -> ResponseEntity.ok(dto))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public List<CompraDTO> obtenerCompras(){
        return compraService.listarCarritos();
    }

    @PutMapping("/{id}/entregar")
public ResponseEntity<?> marcarComoEntregado(
        @PathVariable Long id, 
        @RequestBody Map<String, String> payload // Recibimos un JSON simple { "password": "..." }
) {
    try {
        // Obtenemos quién está intentando hacer esto
        String dniUsuario = SecurityContextHolder.getContext().getAuthentication().getName();
        String password = payload.get("password");

        if (password == null || password.isEmpty()) {
            return ResponseEntity.badRequest().body("La contraseña es obligatoria");
        }

        CompraDTO compraActualizada = compraService.confirmarEntrega(id, password, dniUsuario);
        return ResponseEntity.ok(compraActualizada);

    } catch (RuntimeException e) {
        // Devolvemos 403 o 400 si la contraseña falla
        return ResponseEntity.status(403).body(e.getMessage());
    }
}
}
