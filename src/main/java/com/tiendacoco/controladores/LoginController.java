// src/main/java/com/tiendacoco/controladores/LoginController.java
package com.tiendacoco.controladores;

import com.tiendacoco.dao.UsuarioDAO;
import com.tiendacoco.utils.CaptchaValidator;
import com.tiendacoco.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class LoginController {

    @Autowired
    private UsuarioDAO usuarioDAO;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/login")
    public Map<String, Object> login(
            @RequestParam String usuario,
            @RequestParam String contrasena,
            @RequestParam(required = false) String captchaToken
    ) {
        Map<String, Object> respuesta = new HashMap<>();

        // 1) Contamos los intentos fallidos
        int intentos = usuarioDAO.obtenerIntentosFallidos(usuario);

        // 2) Si ya hubo ≥3 intentos y no pasó el CAPTCHA, rechazamos
        if (intentos >= 3 && (captchaToken == null || !CaptchaValidator.validarCaptcha(captchaToken))) {
            respuesta.put("login", false);
            respuesta.put("captcha", true);
            respuesta.put("mensaje", "Captcha inválido o faltante.");
            respuesta.put("intentos", intentos);
            return respuesta;
        }

        // 3) Validamos credenciales
        boolean esValido = usuarioDAO.validarUsuario(usuario, contrasena);
        if (!esValido) {
            // a) Credenciales inválidas → sumamos intento
            usuarioDAO.sumarIntentoFallido(usuario);
            intentos = usuarioDAO.obtenerIntentosFallidos(usuario);

            respuesta.put("login", false);
            respuesta.put("mensaje", "Credenciales inválidas");
            respuesta.put("intentos", intentos);

            if (intentos >= 3) {
                respuesta.put("captcha", true);
            }
            return respuesta;
        }

        // 4) Login exitoso → reiniciamos contador y generamos token
        usuarioDAO.reiniciarIntentosFallidos(usuario);
        String token = jwtUtil.generateToken(usuario);

        respuesta.put("login", true);
        respuesta.put("mensaje", "Acceso concedido");
        respuesta.put("token", token);
        return respuesta;
    }
}
