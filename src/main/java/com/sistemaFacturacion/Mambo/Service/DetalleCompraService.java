package com.sistemaFacturacion.Mambo.Service;



import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.sistemaFacturacion.Mambo.entity.Repository.DetalleCompraRepository;
import com.sistemaFacturacion.Mambo.entity.Repository.ProductoRepository;
import com.sistemaFacturacion.Mambo.entity.model.Compra;
import com.sistemaFacturacion.Mambo.entity.model.DetalleCompra;
import com.sistemaFacturacion.Mambo.mape.dto.DetalleCompraDto;
import com.sistemaFacturacion.Mambo.mape.mapeo.DetalleCompraMape;
@Service
@Transactional
public class DetalleCompraService {

    private final DetalleCompraRepository detalleCompraRepository;
    private final DetalleCompraMape detalleCompraMape;

    public DetalleCompraService(DetalleCompraRepository detalleCompraRepository,
                                 ProductoRepository productoRepository,DetalleCompraMape detalleCompraMape) {
        this.detalleCompraRepository = detalleCompraRepository;
        this.detalleCompraMape = detalleCompraMape;
    }

    // ➕ Crear detalle
    public DetalleCompraDto crearDetalle(DetalleCompraDto dto, Compra carrito) {
        DetalleCompra nuevo = detalleCompraMape.toEntity(dto,carrito);    
        DetalleCompra guardar = detalleCompraRepository.save(nuevo);
        return detalleCompraMape.toDto(guardar);
    }



}
