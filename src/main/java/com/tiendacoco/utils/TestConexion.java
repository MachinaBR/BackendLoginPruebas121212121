package com.tiendacoco.utils;

import java.sql.Connection;

/**
 * Clase de prueba para verificar que la conexión a la base de datos funciona correctamente.
 */
public class TestConexion {

    public static void main(String[] args) {
        try (Connection conn = ConexionBD.obtenerConexion()) {
            if (conn != null && !conn.isClosed()) {
                System.out.println("✅ Conexión exitosa a la base de datos.");
            } else {
                System.out.println("⚠️ La conexión está cerrada o no fue establecida.");
            }
        } catch (Exception e) {
            System.out.println("❌ Error al conectar: " + e.getMessage());
        }
    }
}