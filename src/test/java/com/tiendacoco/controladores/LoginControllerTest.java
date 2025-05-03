package com.tiendacoco.controladores;

import com.tiendacoco.controladores.LoginController;
import com.tiendacoco.dao.UsuarioDAO;
import com.tiendacoco.utils.JwtUtil;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(LoginController.class)
// <-- desactiva filtros de Spring Security en MockMvc
@AutoConfigureMockMvc(addFilters = false)
class LoginControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private UsuarioDAO usuarioDAO;

    @MockBean
    private JwtUtil jwtUtil;

    @Test
    void credencialesInvalidas_devuelveLoginFalse() throws Exception {
        // dado que validarUsuario(...) devuelve false
        Mockito.when(usuarioDAO.validarUsuario("u", "p")).thenReturn(false);

        mvc.perform(post("/api/login")
                        .param("usuario","u")
                        .param("contrasena","p")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.login").value(false));
    }

    @Test
    void loginValido_devuelveToken() throws Exception {
        // dado que validarUsuario(...) devuelve true
        Mockito.when(usuarioDAO.validarUsuario("u", "p")).thenReturn(true);
        // y que jwtUtil.generateToken(...) devuelve un "fake-jwt-token"
        Mockito.when(jwtUtil.generateToken("u")).thenReturn("fake-jwt-token");

        mvc.perform(post("/api/login")
                        .param("usuario","u")
                        .param("contrasena","p")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("fake-jwt-token"));
    }
}
