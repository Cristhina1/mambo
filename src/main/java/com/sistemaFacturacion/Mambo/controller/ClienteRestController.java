package com.sistemaFacturacion.Mambo.controller;

import com.sistemaFacturacion.Mambo.Service.ClienteService;
import com.sistemaFacturacion.Mambo.entity.model.cliente;
import com.sistemaFacturacion.Mambo.mape.dto.ClienteDTO;
import com.sistemaFacturacion.Mambo.mape.mapeo.ClienteMape;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/clientes")
//@CrossOrigin(origins = "http://localhost:4200") 
public class ClienteRestController {
    
    private final ClienteService clienteService;
    private final ClienteMape clienteMape;

    public ClienteRestController(ClienteService clienteService, ClienteMape clienteMape) {
        this.clienteService = clienteService;
        this.clienteMape = clienteMape;
    }

    // Listar todos
    @GetMapping
    public List<ClienteDTO> listar() {
        return clienteService.listarClientes();
    }

    // Obtener por ID
    @GetMapping("/buscar/{id}")
    public Optional<ClienteDTO> obtener(@PathVariable Long id) {
        return clienteService.obtenerPorId(id);
    }

    // ✅ CORREGIDO: Usamos @ModelAttribute para aceptar FormData desde Angular
    @PutMapping("/actualizar/{id}")
    public ResponseEntity<ClienteDTO> actualizar(@PathVariable Long id, @ModelAttribute ClienteDTO c) {
        ClienteDTO actualizado = clienteService.actualizarCliente(id, c);
        return ResponseEntity.ok(actualizado);
    }

    // Eliminar
    @DeleteMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id) {
        clienteService.eliminarCliente(id);
        return "Cliente eliminado con ID: " + id;
    }

    // ✅ CORREGIDO: Convertimos la entidad 'cliente' a 'ClienteDTO' antes de devolver
    @PostMapping
    public ResponseEntity<ClienteDTO> create(@ModelAttribute ClienteDTO clienteDTO) {
        // 1. El servicio crea y devuelve la Entidad
        cliente nuevoCliente = clienteService.crearCliente(clienteDTO);
        
        // 2. Convertimos la Entidad a DTO para que coincida con el return del método
        ClienteDTO dtoRespuesta = clienteMape.toDto(nuevoCliente);
        
        return ResponseEntity.ok(dtoRespuesta);
    }
}