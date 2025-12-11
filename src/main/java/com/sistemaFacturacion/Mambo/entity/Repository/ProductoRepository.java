package com.sistemaFacturacion.Mambo.entity.Repository;

import com.sistemaFacturacion.Mambo.entity.model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long> {

    List<Producto> findByNombreContainingIgnoreCase(String nombre);
    List<Producto> findByCategoriaId(Long categoriaId);
    // Contar productos con stock menor a 5
@Query("SELECT COUNT(p) FROM Producto p WHERE p.stock < 5")
Long contarBajoStock();
    Optional<Producto> findById(Long id);
}
