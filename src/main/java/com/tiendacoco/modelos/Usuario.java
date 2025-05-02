package com.tiendacoco.modelos;

import jakarta.persistence.*;

@Entity
@Table(name = "usuarios")  // Nombre de la tabla en tu base de datos
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "nombre_usuario", nullable = false, unique = true)
    private String nombreUsuario;

    @Column(nullable = false)
    private String contrasena;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String rol;

    @Column(name = "intentos_fallidos")
    private int intentosFallidos = 0;

    // Getters y Setters
    public int getId() { return id; }

    public void setId(int id) { this.id = id; }

    public String getNombreUsuario() { return nombreUsuario; }

    public void setNombreUsuario(String nombreUsuario) { this.nombreUsuario = nombreUsuario; }

    public String getContrasena() { return contrasena; }

    public void setContrasena(String contrasena) { this.contrasena = contrasena; }

    public String getEmail() { return email; }

    public void setEmail(String email) { this.email = email; }

    public String getRol() { return rol; }

    public void setRol(String rol) { this.rol = rol; }

    public int getIntentosFallidos() { return intentosFallidos; }

    public void setIntentosFallidos(int intentosFallidos) { this.intentosFallidos = intentosFallidos; }

    @Override
    public String toString() {
        return "Usuario{" +
                "id=" + id +
                ", nombreUsuario='" + nombreUsuario + '\'' +
                ", email='" + email + '\'' +
                ", rol='" + rol + '\'' +
                ", intentosFallidos=" + intentosFallidos +
                '}';
    }
}
