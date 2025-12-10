package com.sistemaFacturacion.Mambo.mape.mapeo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.sistemaFacturacion.Mambo.entity.Repository.ProductoRepository;
import com.sistemaFacturacion.Mambo.entity.model.Compra;
import com.sistemaFacturacion.Mambo.entity.model.DetalleCompra;
import com.sistemaFacturacion.Mambo.entity.model.Producto;
import com.sistemaFacturacion.Mambo.mape.dto.DetalleCompraDto;

@Component
public class DetalleCompraMape {

    @Autowired
    private ProductoRepository productoRepository;


    public DetalleCompra toEntity(DetalleCompraDto dto,Compra compra) {

        DetalleCompra detalle = new DetalleCompra();
        detalle.setCantidad(dto.getCantidad());
        detalle.setPrecioUnitario(dto.getPrecioUnitario());
        Producto producto = productoRepository.findById(dto.getProductoId())
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));
        detalle.setProducto(producto);
        detalle.setCompra(compra);
        detalle.setSubTotal(dto.getPrecioUnitario() * dto.getCantidad());
        return detalle;
    }

    public DetalleCompraDto toDto(DetalleCompra entity) {
        DetalleCompraDto dto = new DetalleCompraDto();
        dto.setId(entity.getId());
        dto.setCantidad(entity.getCantidad());
        dto.setPrecioUnitario(entity.getPrecioUnitario());
        dto.setSubtotal(entity.getSubTotal());
        dto.setProductoId(entity.getProducto().getId());
        dto.setNombreProducto(entity.getProducto().getNombre());
        return dto;
    }
}

