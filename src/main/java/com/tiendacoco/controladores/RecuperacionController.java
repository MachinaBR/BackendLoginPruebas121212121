package com.tiendacoco.controladores;

import com.tiendacoco.dao.UsuarioDAO;
import com.tiendacoco.modelos.Usuario;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * Controlador REST para recuperación de contraseña.
 */
@RestController
@RequestMapping("/api")
public class RecuperacionController {

    @PostMapping("/recuperar")
    public Map<String, Object> recuperarClave(@RequestParam String email) {
        Map<String, Object> respuesta = new HashMap<>();

        // Buscar usuario por email
        Usuario usuario = UsuarioDAO.obtenerUsuarioPorEmail(email);

        if (usuario == null) {
            respuesta.put("recuperado", false);
            respuesta.put("mensaje", "No se encontró un usuario con ese email.");
            return respuesta;
        }

        // Generar clave provisional
        String claveTemporal = generarClaveAleatoria(8); // longitud 8
        String hash = BCrypt.hashpw(claveTemporal, BCrypt.gensalt());
        LocalDateTime expiracion = LocalDateTime.now().plusMinutes(30);

        // Guardar clave en base de datos
        boolean guardado = UsuarioDAO.guardarClaveTemporal(usuario.getId(), hash, expiracion);

        if (guardado) {
            respuesta.put("recuperado", true);
            respuesta.put("mensaje", "Se generó una contraseña temporal.");
            respuesta.put("clave_temporal_simulada", claveTemporal); // solo para pruebas
        } else {
            respuesta.put("recuperado", false);
            respuesta.put("mensaje", "Error al guardar la clave temporal.");
        }

        return respuesta;
    }

    // Método auxiliar para generar clave aleatoria
    private String generarClaveAleatoria(int longitud) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder sb = new StringBuilder();
        Random rnd = new Random();
        for (int i = 0; i < longitud; i++) {
            sb.append(chars.charAt(rnd.nextInt(chars.length())));
        }
        return sb.toString();
    }


    @PostMapping("/validar-temporal")
    public Map<String, Object> validarClaveTemporal(
            @RequestParam String email,
            @RequestParam String clave) {

        Map<String, Object> respuesta = new HashMap<>();

        // Validar clave temporal con lógica de DAO
        boolean esValida = UsuarioDAO.validarClaveTemporal(email, clave);

        if (esValida) {
            // Marcarla como usada inmediatamente después de verificar
            UsuarioDAO.marcarClaveComoUsada(email);
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

        // Verificar que la clave temporal sea válida
        boolean esValida = UsuarioDAO.validarClaveTemporal(email, claveTemporal);

        if (!esValida) {
            respuesta.put("cambiada", false);
            respuesta.put("mensaje", "La clave temporal no es válida o ya expiró.");
            return respuesta;
        }

        // Encriptar la nueva contraseña
        String nuevaHash = BCrypt.hashpw(nuevaContrasena, BCrypt.gensalt());

        // Actualizar la contraseña en la tabla usuarios
        boolean actualizada = UsuarioDAO.actualizarContrasenaDefinitiva(email, nuevaHash);

        if (actualizada) {
            // Marcar la clave temporal como usada
            UsuarioDAO.marcarClaveComoUsada(email);

            respuesta.put("cambiada", true);
            respuesta.put("mensaje", "Contraseña actualizada exitosamente.");
        } else {
            respuesta.put("cambiada", false);
            respuesta.put("mensaje", "Error al actualizar la contraseña.");
        }

        return respuesta;
    }


}
