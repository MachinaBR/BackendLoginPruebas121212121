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

        Map<String, Object> respuesta = new HashMap<>();

        boolean esValido = UsuarioDAO.validarUsuario(usuario, contrasena);

        if (esValido) {
            // Login correcto: reiniciar intentos fallidos
            UsuarioDAO.reiniciarIntentosFallidos(usuario);
            respuesta.put("login", true);
            respuesta.put("mensaje", "Acceso concedido");

        } else {
            // Login incorrecto: aumentar contador
            UsuarioDAO.sumarIntentoFallido(usuario);
            int intentos = UsuarioDAO.obtenerIntentosFallidos(usuario);

            respuesta.put("login", false);
            respuesta.put("mensaje", "Credenciales inválidas");

            // Si supera los 3 intentos, activar CAPTCHA
            if (intentos >= 3) {
                respuesta.put("captcha", true);
                respuesta.put("intentos", intentos);
                respuesta.put("advertencia", "Demasiados intentos fallidos. Verifica que no eres un robot.");
            } else {
                respuesta.put("intentos", intentos);
            }
        }

        return respuesta;
    }

}
