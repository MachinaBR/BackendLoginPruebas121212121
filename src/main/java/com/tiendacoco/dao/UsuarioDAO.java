package com.tiendacoco.dao;

import com.tiendacoco.modelos.Usuario;
import com.tiendacoco.utils.ConexionBD;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.time.LocalDateTime;

/**
 * Clase encargada de acceder a los datos de la tabla 'usuarios'.
 * Aquí se implementa la validación del usuario con contraseñas encriptadas usando BCrypt.
 */
public class UsuarioDAO {

    /**
     * Método que valida si un usuario existe con el nombre y contraseña proporcionados.
     * @param nombreUsuario Nombre ingresado por el usuario.
     * @param contrasena Contraseña ingresada por el usuario (sin encriptar).
     * @return true si las credenciales son válidas, false en caso contrario.
     */
    public static boolean validarUsuario(String nombreUsuario, String contrasena) {
        String sql = "SELECT * FROM usuarios WHERE nombre_usuario = ?";

        try (Connection conn = ConexionBD.obtenerConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, nombreUsuario);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String hashBD = rs.getString("contrasena");
                return BCrypt.checkpw(contrasena, hashBD); // Compara contraseña con hash
            }

        } catch (Exception e) {
            System.out.println("❌ Error al validar usuario: " + e.getMessage());
        }

        return false;
    }

    // Método para verificar si el nombre de usuario ya existe en la base de datos
    public static boolean usuarioExiste(String nombreUsuario) {
        System.out.println("🔎 Verificando existencia de: '" + nombreUsuario + "'");
        String sql = "SELECT COUNT(*) FROM usuarios WHERE nombre_usuario = ?";
        try (Connection conn = ConexionBD.obtenerConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, nombreUsuario);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0; // Si el contador es mayor que cero, ya existe
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    // Método para registrar un nuevo usuario con contraseña encriptada
    public static boolean registrarUsuario(String nombreUsuario, String contrasenaHash, String email, String rol) {
        String sql = "INSERT INTO usuarios (nombre_usuario, contrasena, email, rol) VALUES (?, ?, ?, ?)";
        try (Connection conn = ConexionBD.obtenerConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, nombreUsuario);
            stmt.setString(2, contrasenaHash);
            stmt.setString(3, email);
            stmt.setString(4, rol);

            return stmt.executeUpdate() > 0; // true si se insertó correctamente
        } catch (Exception e) {
            System.out.println("❌ Error al registrar usuario: " + e.getMessage());
        }
        return false;
    }

    public static boolean guardarClaveTemporal(int usuarioId, String claveTemporalHash, LocalDateTime expiracion) {
        String sql = "INSERT INTO recuperacion_claves (usuario_id, clave_temporal, fecha_expiracion) VALUES (?, ?, ?)";

        try (Connection conn = ConexionBD.obtenerConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, usuarioId);
            stmt.setString(2, claveTemporalHash);
            stmt.setTimestamp(3, Timestamp.valueOf(expiracion));

            return stmt.executeUpdate() > 0;

        } catch (Exception e) {
            System.out.println("❌ Error al guardar clave temporal: " + e.getMessage());
        }

        return false;
    }

    public static Usuario obtenerUsuarioPorEmail(String email) {
        String sql = "SELECT * FROM usuarios WHERE email = ?";
        try (Connection conn = ConexionBD.obtenerConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Usuario usuario = new Usuario();
                usuario.setId(rs.getInt("id"));
                usuario.setNombreUsuario(rs.getString("nombre_usuario"));
                usuario.setEmail(rs.getString("email"));
                usuario.setContrasena(rs.getString("contrasena"));
                usuario.setRol(rs.getString("rol"));
                return usuario;
            }

        } catch (Exception e) {
            System.out.println("❌ Error al buscar usuario por email: " + e.getMessage());
        }
        return null;
    }

    public static boolean validarClaveTemporal(String email, String claveIngresada) {
        String sql = """
        SELECT rc.clave_temporal, rc.fecha_expiracion, rc.utilizada
        FROM recuperacion_claves rc
        JOIN usuarios u ON rc.usuario_id = u.id
        WHERE u.email = ?
        ORDER BY rc.id DESC
        LIMIT 1
    """;

        try (Connection conn = ConexionBD.obtenerConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String hashGuardado = rs.getString("clave_temporal");
                LocalDateTime expiracion = rs.getTimestamp("fecha_expiracion").toLocalDateTime();
                boolean yaUsada = rs.getBoolean("utilizada");

                boolean coincide = BCrypt.checkpw(claveIngresada, hashGuardado);
                boolean noExpirada = LocalDateTime.now().isBefore(expiracion);

                return coincide && noExpirada && !yaUsada;
            }

        } catch (Exception e) {
            System.out.println("❌ Error al validar clave temporal: " + e.getMessage());
        }

        return false;
    }

    public static void marcarClaveComoUsada(String email) {
        String sql = """
        UPDATE recuperacion_claves rc
        JOIN usuarios u ON rc.usuario_id = u.id
        SET rc.utilizada = TRUE
        WHERE u.email = ?
        ORDER BY rc.id DESC
        LIMIT 1
    """;

        try (Connection conn = ConexionBD.obtenerConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, email);
            stmt.executeUpdate();

        } catch (Exception e) {
            System.out.println("❌ Error al marcar clave como usada: " + e.getMessage());
        }
    }

    public static boolean actualizarContrasenaDefinitiva(String email, String nuevaContrasenaHash) {
        String sql = "UPDATE usuarios SET contrasena = ? WHERE email = ?";

        try (Connection conn = ConexionBD.obtenerConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, nuevaContrasenaHash);
            stmt.setString(2, email);

            return stmt.executeUpdate() > 0;

        } catch (Exception e) {
            System.out.println("❌ Error al actualizar la contraseña: " + e.getMessage());
        }

        return false;
    }

    public static void sumarIntentoFallido(String nombreUsuario) {
        String sql = "UPDATE usuarios SET intentos_fallidos = intentos_fallidos + 1 WHERE nombre_usuario = ?";

        try (Connection conn = ConexionBD.obtenerConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, nombreUsuario);
            stmt.executeUpdate();

        } catch (Exception e) {
            System.out.println("❌ Error al sumar intento fallido: " + e.getMessage());
        }
    }

    public static void reiniciarIntentosFallidos(String nombreUsuario) {
        String sql = "UPDATE usuarios SET intentos_fallidos = 0 WHERE nombre_usuario = ?";

        try (Connection conn = ConexionBD.obtenerConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, nombreUsuario);
            stmt.executeUpdate();

        } catch (Exception e) {
            System.out.println("❌ Error al reiniciar intentos fallidos: " + e.getMessage());
        }
    }

    public static int obtenerIntentosFallidos(String nombreUsuario) {
        String sql = "SELECT intentos_fallidos FROM usuarios WHERE nombre_usuario = ?";

        try (Connection conn = ConexionBD.obtenerConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, nombreUsuario);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("intentos_fallidos");
            }

        } catch (Exception e) {
            System.out.println("❌ Error al obtener intentos fallidos: " + e.getMessage());
        }

        return 0;
    }


}


