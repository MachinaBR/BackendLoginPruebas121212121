package com.tiendacoco.controladores;

import com.tiendacoco.dao.UsuarioDAO;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class RegistroController {

    @Autowired
    private UsuarioDAO usuarioDAO; // ✅ Inyección correcta

    @PostMapping("/registro")
    public Map<String, Object> registrarUsuario(
            @RequestParam String usuario,
            @RequestParam String contrasena,
            @RequestParam String email,
            @RequestParam(defaultValue = "usuario") String rol) {

        Map<String, Object> respuesta = new HashMap<>();

        if (usuarioDAO.usuarioExiste(usuario)) { // ✅ Correcto ahora
            respuesta.put("registro", false);
            respuesta.put("mensaje", "El nombre de usuario ya existe.");
            return respuesta;
        }

        String hash = BCrypt.hashpw(contrasena, BCrypt.gensalt());
        boolean registrado = usuarioDAO.registrarUsuario(usuario, hash, email, rol);

        if (registrado) {
            respuesta.put("registro", true);
            respuesta.put("mensaje", "Usuario registrado exitosamente.");
        } else {
            respuesta.put("registro", false);
            respuesta.put("mensaje", "Error al registrar el usuario.");
        }

        return respuesta;
    }

    @GetMapping("/usuario-existe")
    public Map<String, Object> verificarUsuario(@RequestParam String usuario) {
        Map<String, Object> respuesta = new HashMap<>();
        respuesta.put("existe", usuarioDAO.usuarioExiste(usuario)); // ✅ Corregido también
        return respuesta;
    }
}
