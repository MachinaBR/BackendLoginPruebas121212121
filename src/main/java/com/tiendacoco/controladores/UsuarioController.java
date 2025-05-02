// src/main/java/com/tiendacoco/controladores/UsuarioController.java
package com.tiendacoco.controladores;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api")
public class UsuarioController {

    @GetMapping("/me")
    public Map<String, Object> me(Authentication auth) {
        // Si auth==null, tu filtro no corrió o no puso el usuario en el contexto
        HashMap<String,Object> resp = new HashMap<>();
        resp.put("usuario", auth!=null ? auth.getName() : null);
        return resp;
    }
}
