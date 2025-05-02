package com.tiendacoco.dao;

import com.tiendacoco.modelos.Usuario;
import com.tiendacoco.modelos.RecuperacionClave;
import com.tiendacoco.repositorio.UsuarioRepository;
import com.tiendacoco.repositorio.RecuperacionClaveRepository;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class UsuarioDAO {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private RecuperacionClaveRepository recuperacionRepo;

    /** Valida un login comparando la contraseña en texto plano con el hash guardado. */
    public boolean validarUsuario(String nombreUsuario, String contrasena) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findByNombreUsuario(nombreUsuario);
        if (usuarioOpt.isPresent()) {
            String hashBD = usuarioOpt.get().getContrasena();
            return BCrypt.checkpw(contrasena, hashBD);
        }
        return false;
    }

    /** Comprueba si ya existe un usuario con ese nombre. */
    public boolean usuarioExiste(String nombreUsuario) {
        return usuarioRepository.existsByNombreUsuario(nombreUsuario);
    }

    /** Inserta un nuevo usuario con contraseña hasheada. */
    public boolean registrarUsuario(String nombreUsuario, String contrasenaHash, String email, String rol) {
        try {
            Usuario nuevo = new Usuario();
            nuevo.setNombreUsuario(nombreUsuario);
            nuevo.setContrasena(contrasenaHash);
            nuevo.setEmail(email);
            nuevo.setRol(rol);
            usuarioRepository.save(nuevo);
            return true;
        } catch (Exception e) {
            System.out.println("❌ Error al registrar usuario: " + e.getMessage());
            return false;
        }
    }

    /** Incrementa en 1 el contador de intentos fallidos. */
    public void sumarIntentoFallido(String nombreUsuario) {
        usuarioRepository.findByNombreUsuario(nombreUsuario).ifPresent(u -> {
            u.setIntentosFallidos(u.getIntentosFallidos() + 1);
            usuarioRepository.save(u);
        });
    }

    /** Pone a cero el contador de intentos fallidos. */
    public void reiniciarIntentosFallidos(String nombreUsuario) {
        usuarioRepository.findByNombreUsuario(nombreUsuario).ifPresent(u -> {
            u.setIntentosFallidos(0);
            usuarioRepository.save(u);
        });
    }

    /** Recupera el número de intentos fallidos de un usuario. */
    public int obtenerIntentosFallidos(String nombreUsuario) {
        return usuarioRepository.findByNombreUsuario(nombreUsuario)
                .map(Usuario::getIntentosFallidos)
                .orElse(0);
    }

    /** Busca un usuario por su email. */
    public Usuario obtenerUsuarioPorEmail(String email) {
        return usuarioRepository.findByEmail(email).orElse(null);
    }

    /**
     * Crea y guarda una clave temporal para recuperación de contraseña.
     * @param usuarioId       Id numérico del usuario
     * @param claveTemporalHash  Hash de la clave generada
     * @param expiracion      Fecha de expiración
     */
    public boolean guardarClaveTemporal(int usuarioId, String claveTemporalHash, LocalDateTime expiracion) {
        try {
            Optional<Usuario> userOpt = usuarioRepository.findById(usuarioId);
            if (userOpt.isEmpty()) return false;
            RecuperacionClave rc = new RecuperacionClave();
            rc.setUsuario(userOpt.get());
            rc.setClaveTemporal(claveTemporalHash);
            rc.setFechaExpiracion(expiracion);
            rc.setUtilizada(false);
            recuperacionRepo.save(rc);
            return true;
        } catch (Exception e) {
            System.out.println("❌ Error al guardar clave temporal: " + e.getMessage());
            return false;
        }
    }

    /**
     * Valida que la clave temporal coincida, no haya expirado y no se haya usado.
     */
    public boolean validarClaveTemporal(String email, String claveIngresada) {
        Usuario u = obtenerUsuarioPorEmail(email);
        if (u == null) return false;
        Optional<RecuperacionClave> rcOpt = recuperacionRepo.findTopByUsuarioOrderByIdDesc(u);
        if (rcOpt.isEmpty()) return false;
        RecuperacionClave rc = rcOpt.get();
        boolean coincide   = BCrypt.checkpw(claveIngresada, rc.getClaveTemporal());
        boolean noExpirada = LocalDateTime.now().isBefore(rc.getFechaExpiracion());
        return coincide && noExpirada && !rc.isUtilizada();
    }

    /** Marca la última clave temporal como usada. */
    public void marcarClaveComoUsada(String email) {
        Usuario u = obtenerUsuarioPorEmail(email);
        if (u == null) return;
        recuperacionRepo.findTopByUsuarioOrderByIdDesc(u).ifPresent(rc -> {
            rc.setUtilizada(true);
            recuperacionRepo.save(rc);
        });
    }

    /** Actualiza la contraseña definitiva del usuario. */
    public boolean actualizarContrasenaDefinitiva(String email, String nuevaContrasenaHash) {
        Optional<Usuario> userOpt = usuarioRepository.findByEmail(email);
        if (userOpt.isPresent()) {
            Usuario u = userOpt.get();
            u.setContrasena(nuevaContrasenaHash);
            usuarioRepository.save(u);
            return true;
        }
        return false;
    }
}
