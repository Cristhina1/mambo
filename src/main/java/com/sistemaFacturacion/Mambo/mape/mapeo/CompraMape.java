package com.sistemaFacturacion.Mambo.mape.mapeo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.sistemaFacturacion.Mambo.entity.model.Compra;
import com.sistemaFacturacion.Mambo.entity.model.Destinatario;
import com.sistemaFacturacion.Mambo.entity.model.Envio;
import com.sistemaFacturacion.Mambo.entity.model.pago;
import com.sistemaFacturacion.Mambo.mape.dto.CompraDTO;
import com.sistemaFacturacion.Mambo.mape.dto.DestinatarioDTO;
import com.sistemaFacturacion.Mambo.mape.dto.EnvioDTO;
import com.sistemaFacturacion.Mambo.mape.dto.PagoDTO;

@Component
public class CompraMape {
    @Autowired
    EnvioMape envioMape;

    @Autowired
    DestinatarioMape destinatarioMape;

    @Autowired
    PagoMape pagoMape;

    @Autowired
    DetalleCompraMape detalleCompraMape;


     public Envio toEnvioEntity(EnvioDTO dto) {
        return envioMape.toEntity(dto);
    }

    public Destinatario toDestinatarioEntity(DestinatarioDTO dto){
        return destinatarioMape.toEntity(dto);
    }
    public pago toPagoEntity(PagoDTO dto) {
        return pagoMape.toEntity(dto);
    }


    public CompraDTO toDto(Compra compra){
        
        CompraDTO dto = new CompraDTO();
        dto.setId(compra.getId());
        dto.setContactoDestinatario(compra.getDestinatario().getTelefono());
        dto.setTipoPago(compra.getPago().getTipoPago().name());
        dto.setTotal(compra.getTotal());
        dto.setNombreDestinario(compra.getDestinatario().getNombres()+" "+compra.getDestinatario().getApellidos());
        dto.setEstado(compra.getEstadoPago().name());
        dto.setTipoEnvio(compra.getEnvio().getTipoEnvio().name());
        dto.setTipoComprobante(compra.getTipoComprobante().name());
        dto.setFechaCreacion(compra.getFechaCreacion().toString());
        dto.setDetalles(
                compra.getDetalles().stream()
                        .map(detalleCompraMape::toDto)
                        .toList()
        );
        return dto;
    }
}
