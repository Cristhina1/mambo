
package com.sistemaFacturacion.Mambo.entity.Repository;
import com.sistemaFacturacion.Mambo.entity.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository // 📌 Indica que esta interfaz es un repositorio de acceso a BD
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByEmail(String email);
    Optional<Usuario> findByNumeroDocumento(String numeroDocumento);
    List<Usuario> findByRolNombre(String nombreRol);
    
    @Query("SELECT u FROM Usuario u WHERE u.rol.nombre = 'VENDEDOR'")
    List<Usuario> listarVendedores();

}
