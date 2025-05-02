package com.tiendacoco.controladores;

import com.tiendacoco.dao.UsuarioDAO;
import com.tiendacoco.modelos.Usuario;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@RestController
@RequestMapping("/api")
public class RecuperacionController {

    @Autowired
    private UsuarioDAO usuarioDAO;

    @PostMapping("/recuperar")
    public Map<String, Object> recuperarClave(@RequestParam String email) {
        Map<String, Object> respuesta = new HashMap<>();

        Usuario usuario = usuarioDAO.obtenerUsuarioPorEmail(email);

        if (usuario == null) {
            respuesta.put("recuperado", false);
            respuesta.put("mensaje", "No se encontró un usuario con ese email.");
            return respuesta;
        }

        String claveTemporal = generarClaveAleatoria(8);
        String hash = BCrypt.hashpw(claveTemporal, BCrypt.gensalt());
        LocalDateTime expiracion = LocalDateTime.now().plusMinutes(30);

        boolean guardado = usuarioDAO.guardarClaveTemporal(usuario.getId(), hash, expiracion);

        if (guardado) {
            respuesta.put("recuperado", true);
            respuesta.put("mensaje", "Se generó una contraseña temporal.");
            respuesta.put("clave_temporal_simulada", claveTemporal); // Solo para pruebas
        } else {
            respuesta.put("recuperado", false);
            respuesta.put("mensaje", "Error al guardar la clave temporal.");
        }

        return respuesta;
    }

    @PostMapping("/validar-temporal")
    public Map<String, Object> validarClaveTemporal(
            @RequestParam String email,
            @RequestParam String clave) {

        Map<String, Object> respuesta = new HashMap<>();
        boolean esValida = usuarioDAO.validarClaveTemporal(email, clave);

        if (esValida) {
            usuarioDAO.marcarClaveComoUsada(email);
            respuesta.put("valida", true);
            respuesta.put("mensaje", "La contraseña temporal es válida. Puedes cambiarla ahora.");
        } else {
            respuesta.put("valida", false);
            respuesta.put("mensaje", "Clave inválida, expirada o ya utilizada.");
        }

        return respuesta;
    }

    @PostMapping("/cambiar-contrasena")
    public Map<String, Object> cambiarContrasena(
            @RequestParam String email,
            @RequestParam String claveTemporal,
            @RequestParam String nuevaContrasena) {

        Map<String, Object> respuesta = new HashMap<>();
        boolean esValida = usuarioDAO.validarClaveTemporal(email, claveTemporal);

        if (!esValida) {
            respuesta.put("cambiada", false);
            respuesta.put("mensaje", "La clave temporal no es válida o ya expiró.");
            return respuesta;
        }

        String nuevaHash = BCrypt.hashpw(nuevaContrasena, BCrypt.gensalt());
        boolean actualizada = usuarioDAO.actualizarContrasenaDefinitiva(email, nuevaHash);

        if (actualizada) {
            usuarioDAO.marcarClaveComoUsada(email);
            respuesta.put("cambiada", true);
            respuesta.put("mensaje", "Contraseña actualizada exitosamente.");
        } else {
            respuesta.put("cambiada", false);
            respuesta.put("mensaje", "Error al actualizar la contraseña.");
        }

        return respuesta;
    }

    private String generarClaveAleatoria(int longitud) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder sb = new StringBuilder();
        Random rnd = new Random();
        for (int i = 0; i < longitud; i++) {
            sb.append(chars.charAt(rnd.nextInt(chars.length())));
        }
        return sb.toString();
    }
}
