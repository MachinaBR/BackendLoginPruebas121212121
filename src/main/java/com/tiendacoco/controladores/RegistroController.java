package com.tiendacoco.controladores;

import com.tiendacoco.dao.UsuarioDAO;
import org.springframework.web.bind.annotation.*;
import org.mindrot.jbcrypt.BCrypt;

import java.util.HashMap;
import java.util.Map;

/**
 * Controlador REST para registrar nuevos usuarios.
 */
@RestController
@RequestMapping("/api")
public class RegistroController {

    @PostMapping("/registro")
    public Map<String, Object> registrarUsuario(
            @RequestParam String usuario,
            @RequestParam String contrasena,
            @RequestParam String email,
            @RequestParam(required = false, defaultValue = "usuario") String rol) {

        Map<String, Object> respuesta = new HashMap<>();

        // Verifica si ya existe el usuario
        if (UsuarioDAO.usuarioExiste(usuario)) {
            respuesta.put("registro", false);
            respuesta.put("mensaje", "El nombre de usuario ya existe.");
            return respuesta;
        }

        // Encripta la contraseña
        String hash = BCrypt.hashpw(contrasena, BCrypt.gensalt());

        // Intenta registrar al usuario
        boolean registrado = UsuarioDAO.registrarUsuario(usuario, hash, email, rol);

        if (registrado) {
            respuesta.put("registro", true);
            respuesta.put("mensaje", "Usuario registrado exitosamente.");
        } else {
            respuesta.put("registro", false);
            respuesta.put("mensaje", "Error al registrar el usuario.");
        }

        return respuesta;
    }
}
