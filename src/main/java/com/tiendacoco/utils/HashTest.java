package com.tiendacoco.utils;

import org.mindrot.jbcrypt.BCrypt;

/**
 * Clase de utilidad para generar un hash con BCrypt
 * y usarlo en la base de datos para pruebas.
 */
public class HashTest {
    public static void main(String[] args) {
        String contrasena = "12345";
        String hash = BCrypt.hashpw(contrasena, BCrypt.gensalt());

        System.out.println("Contraseña original: " + contrasena);
        System.out.println("Hash generado: " + hash);
    }
}
