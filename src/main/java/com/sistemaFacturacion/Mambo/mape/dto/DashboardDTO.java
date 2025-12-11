package com.sistemaFacturacion.Mambo.mape.dto;

import lombok.Data;
import java.util.List;
import java.util.Map;

@Data
public class DashboardDTO {

    private Double ventasHoy;
    private Long pedidosPendientes;
    private Long productosBajoStock;
    private Long totalClientes;

    private List<Double> ventasSemana; 
    private List<String> diasSemana;   
    private Map<String, Long> productosPorCategoria;
}