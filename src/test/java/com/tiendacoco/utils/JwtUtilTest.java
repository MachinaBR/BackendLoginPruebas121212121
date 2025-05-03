package com.tiendacoco.utils;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(properties = {
        "jwt.secret=MiClaveDePruebaMuyLarga1234567890",
        "jwt.expirationMs=3600000"
})
class JwtUtilTest {

    @Autowired
    private JwtUtil jwtUtil;

    @Test
    void generateAndValidateToken() {
        String username = "usuarioDePrueba";
        String token = jwtUtil.generateToken(username);

        assertNotNull(token, "El token no debe ser null");
        assertTrue(jwtUtil.validateToken(token), "El token debería ser válido");
        assertEquals(username, jwtUtil.getUsernameFromToken(token),
                "El username extraído debe coincidir");
    }

    @Test
    void invalidTokenFailsValidation() {
        assertFalse(jwtUtil.validateToken("este.no.es.un.token"),
                "Un token mal formado debe fallar validación");
    }
}
