package com.sistemaFacturacion.Mambo.entity.Repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.sistemaFacturacion.Mambo.entity.model.Compra;
import com.sistemaFacturacion.Mambo.entity.model.TipoEstado;

import java.util.List;


public interface CompraRepository extends JpaRepository<Compra,Long>{
    Optional<Compra> findByClienteId(Long id);
    Optional<Compra> findById(Long id);
    List<Compra> findByClienteNumeroDocumento(String documetno);
    @Query("SELECT COALESCE(SUM(c.total), 0) FROM Compra c WHERE DATE(c.fechaCreacion) = CURRENT_DATE")
Double sumarVentasHoy();

Long countByEstadoPago(TipoEstado estado);
@Query(value = "SELECT DATE(fecha_creacion) as fecha, SUM(total) as total " +
               "FROM compra " +
               "WHERE fecha_creacion >= CURDATE() - INTERVAL 6 DAY " +
               "GROUP BY DATE(fecha_creacion) " +
               "ORDER BY fecha ASC", nativeQuery = true)
List<Object[]> obtenerVentasUltimos7Dias();
}
