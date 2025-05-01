package com.tiendacoco.controladores;

import com.tiendacoco.dao.UsuarioDAO;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Controlador REST que maneja el inicio de sesión.
 * Recibe credenciales por POST y responde con un JSON indicando el resultado.
 */
@RestController
@RequestMapping("/api")
public class LoginController {

    /**
     * Endpoint POST para validar credenciales de usuario.
     * @param usuario Nombre del usuario recibido desde el frontend
     * @param contrasena Contraseña recibida desde el frontend
     * @return JSON con el resultado del login
     */
    @PostMapping("/login")
    public Map<String, Object> login(
            @RequestParam String usuario,
            @RequestParam String contrasena) {

        boolean esValido = UsuarioDAO.validarUsuario(usuario, contrasena);

        // Construcción de respuesta JSON
        Map<String, Object> respuesta = new HashMap<>();
        respuesta.put("login", esValido);
        respuesta.put("mensaje", esValido ? "Acceso concedido" : "Credenciales inválidas");

        return respuesta;
    }
}
