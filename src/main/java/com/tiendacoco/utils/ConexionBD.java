package com.tiendacoco.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Clase responsable de establecer la conexión con la base de datos MySQL.
 * Esta clase se usa en toda la aplicación para obtener conexiones activas.
 */
public class ConexionBD {

    // URL de conexión a la base de datos (ajustar si usas otro puerto o servidor)
    private static final String URL = "jdbc:mysql://localhost:3306/tiendacoco";

    // Nombre de usuario de MySQL (por defecto suele ser 'root')
    private static final String USUARIO = "root";

    // Contraseña del usuario MySQL (si no tienes, deja vacío "")
    private static final String CONTRASENA = "12345";

    /**
     * Método que devuelve una conexión activa a la base de datos.
     * @return Objeto Connection para ejecutar consultas SQL
     * @throws SQLException si ocurre un error al conectarse
     */
    public static Connection obtenerConexion() throws SQLException {
        // Usa DriverManager para establecer conexión con los parámetros definidos
        return DriverManager.getConnection(URL, USUARIO, CONTRASENA);
    }
}
