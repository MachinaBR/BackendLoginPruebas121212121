package com.tiendacoco.dao;

import com.tiendacoco.modelos.Usuario;
import com.tiendacoco.repositorio.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mindrot.jbcrypt.BCrypt;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UsuarioDAOTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private UsuarioDAO usuarioDAO;

    @Test
    void usuarioNoExiste_devuelveFalse() {
        when(usuarioRepository.findByNombreUsuario("pepito"))
                .thenReturn(Optional.empty());

        assertFalse(usuarioDAO.validarUsuario("pepito","cualquiera"));
    }

    @Test
    void usuarioExisteYContrasenaCorrecta_devuelveTrue() {
        String raw = "miContraseñaDePrueba";
        // generamos hash válido para raw
        String hash = BCrypt.hashpw(raw, BCrypt.gensalt());

        Usuario user = new Usuario();
        user.setNombreUsuario("pepito");
        user.setContrasena(hash);

        when(usuarioRepository.findByNombreUsuario("pepito"))
                .thenReturn(Optional.of(user));

        // ahora checkpw(raw, hash) → true
        assertTrue(usuarioDAO.validarUsuario("pepito", raw));
    }
}
