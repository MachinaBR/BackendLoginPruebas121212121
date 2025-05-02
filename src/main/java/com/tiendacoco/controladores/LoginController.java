package com.tiendacoco.controladores;

import com.tiendacoco.dao.UsuarioDAO;
import com.tiendacoco.utils.CaptchaValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class LoginController {

    @Autowired
    private UsuarioDAO usuarioDAO;

    @PostMapping("/login")
    public Map<String, Object> login(
            @RequestParam String usuario,
            @RequestParam String contrasena,
            @RequestParam(required = false) String captchaToken
    ) {
        Map<String, Object> respuesta = new HashMap<>();

        int intentos = usuarioDAO.obtenerIntentosFallidos(usuario);

        if (intentos >= 3) {
            if (captchaToken == null || !CaptchaValidator.validarCaptcha(captchaToken)) {
                respuesta.put("login", false);
                respuesta.put("captchaValido", false);
                respuesta.put("mensaje", "Captcha inválido o faltante.");
                respuesta.put("captcha", true);
                respuesta.put("intentos", intentos);
                return respuesta;
            }
        }

        boolean esValido = usuarioDAO.validarUsuario(usuario, contrasena);

        if (esValido) {
            usuarioDAO.reiniciarIntentosFallidos(usuario);
            respuesta.put("login", true);
            respuesta.put("mensaje", "Acceso concedido");
        } else {
            usuarioDAO.sumarIntentoFallido(usuario);
            intentos = usuarioDAO.obtenerIntentosFallidos(usuario);
            respuesta.put("login", false);
            respuesta.put("mensaje", "Credenciales inválidas");
            respuesta.put("intentos", intentos);

            if (intentos >= 3) {
                respuesta.put("captcha", true);
                respuesta.put("advertencia", "Demasiados intentos fallidos. Verifica que no eres un robot.");
            }
        }

        return respuesta;
    }
}
