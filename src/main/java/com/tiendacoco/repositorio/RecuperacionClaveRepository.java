package com.tiendacoco.repositorio;

import com.tiendacoco.modelos.RecuperacionClave;
import com.tiendacoco.modelos.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface RecuperacionClaveRepository
        extends JpaRepository<RecuperacionClave, Integer> {

    // Este método es el que tu DAO llama:
    Optional<RecuperacionClave> findTopByUsuarioOrderByIdDesc(Usuario usuario);
}
