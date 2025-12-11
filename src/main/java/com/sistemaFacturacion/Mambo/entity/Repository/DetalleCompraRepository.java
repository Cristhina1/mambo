package com.sistemaFacturacion.Mambo.entity.Repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sistemaFacturacion.Mambo.entity.model.DetalleCompra;

public interface DetalleCompraRepository extends JpaRepository<DetalleCompra, Long> {
    List<DetalleCompra> findByCompraId(Long id);
}
