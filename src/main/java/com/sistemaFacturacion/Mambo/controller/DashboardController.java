package com.sistemaFacturacion.Mambo.controller;

import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sistemaFacturacion.Mambo.entity.Repository.ClienteRepository;
import com.sistemaFacturacion.Mambo.entity.Repository.CompraRepository;
import com.sistemaFacturacion.Mambo.entity.Repository.ProductoRepository;
import com.sistemaFacturacion.Mambo.entity.model.TipoEstado;
import com.sistemaFacturacion.Mambo.mape.dto.DashboardDTO;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final CompraRepository compraRepository;
    private final ProductoRepository productoRepository;
    private final ClienteRepository clienteRepository;

    @GetMapping("/resumen")
    public ResponseEntity<DashboardDTO> obtenerResumen() {
    DashboardDTO dto = new DashboardDTO();

    // 1. CARGAR KPIs (Igual que antes)
    dto.setVentasHoy(compraRepository.sumarVentasHoy());
    dto.setPedidosPendientes(compraRepository.countByEstadoPago(TipoEstado.PENDIENTE));
    dto.setProductosBajoStock(productoRepository.contarBajoStock());
    dto.setTotalClientes(clienteRepository.count());

    // 2. PROCESAR GRÁFICO DE VENTAS (Últimos 7 días)
    // A. Inicializamos un mapa con los últimos 7 días en 0.0 para que no queden huecos
    Map<LocalDate, Double> mapaVentas = new LinkedHashMap<>();
    LocalDate hoy = LocalDate.now();
    
    for (int i = 6; i >= 0; i--) {
        mapaVentas.put(hoy.minusDays(i), 0.0);
    }

    // B. Traemos la data real de la BD
    List<Object[]> resultados = compraRepository.obtenerVentasUltimos7Dias();

    // C. Llenamos el mapa con los datos reales
    for (Object[] fila : resultados) {
        // Dependiendo del driver MySQL, la fecha puede ser java.sql.Date o String
        LocalDate fechaVenta = java.sql.Date.valueOf(fila[0].toString()).toLocalDate();
        Double totalVenta = Double.valueOf(fila[1].toString());
        
        mapaVentas.put(fechaVenta, totalVenta);
    }
    // D. Separamos en dos listas para el DTO
    List<String> etiquetasDias = new ArrayList<>();
    List<Double> valoresVentas = new ArrayList<>();

    // Usamos Locale para que los días salgan en español (Lun, Mar...)
    Locale español = new Locale("es", "ES");

    for (Map.Entry<LocalDate, Double> entrada : mapaVentas.entrySet()) {
        // Convertimos fecha a nombre de día (ej: "lunes") y tomamos las primeras 3 letras
        String nombreDia = entrada.getKey().getDayOfWeek()
                .getDisplayName(TextStyle.SHORT, español); 
        
        // Capitalizamos (lun -> Lun)
        nombreDia = nombreDia.substring(0, 1).toUpperCase() + nombreDia.substring(1);

        etiquetasDias.add(nombreDia);
        valoresVentas.add(entrada.getValue());
    }

    dto.setDiasSemana(etiquetasDias);
    dto.setVentasSemana(valoresVentas);

    // 3. DATOS DE CATEGORÍAS (Para la dona)
    // Aquí podrías agregar otra consulta similar si quieres que sea real
    // Por ahora dejamos el simulado o implementas un logic similar con productoRepository
    Map<String, Long> categoriasFake = Map.of("Polos", 15L, "Pantalones", 8L, "Accesorios", 4L);
    dto.setProductosPorCategoria(categoriasFake);

    return ResponseEntity.ok(dto);
}}
