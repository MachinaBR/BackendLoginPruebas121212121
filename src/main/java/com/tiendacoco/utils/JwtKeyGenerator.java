package com.tiendacoco.utils;

import io.jsonwebtoken.io.Encoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.SignatureAlgorithm;

import java.security.Key;

public class JwtKeyGenerator {
    public static void main(String[] args) {
        // Genera una Key para HS256 (256 bits mínimo)
        Key key = Keys.secretKeyFor(SignatureAlgorithm.HS256);
        // Codifica la clave en Base64 para que puedas ponerla en properties
        String base64Key = Encoders.BASE64.encode(key.getEncoded());
        System.out.println("jwt.secret=" + base64Key);
    }
}
