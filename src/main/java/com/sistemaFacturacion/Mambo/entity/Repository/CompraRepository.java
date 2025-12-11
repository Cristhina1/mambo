package com.sistemaFacturacion.Mambo.entity.Repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sistemaFacturacion.Mambo.entity.model.Compra;
import java.util.List;


public interface CompraRepository extends JpaRepository<Compra,Long>{
    Optional<Compra> findByClienteId(Long id);
    Optional<Compra> findById(Long id);
    List<Compra> findByClienteNumeroDocumento(String documetno);
}
