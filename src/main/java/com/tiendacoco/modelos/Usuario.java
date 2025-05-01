package com.tiendacoco.modelos;

public class Usuario {
    private int id;
    private String nombreUsuario;
    private String contrasena;
    private String email;
    private String rol;

    // Constructor vacío
    public Usuario() {
        this.rol = "usuario";
    }

    // Constructor con parámetros básicos
    public Usuario(int id, String nombreUsuario, String contrasena) {
        this();
        this.id = id;
        this.nombreUsuario = nombreUsuario;
        this.contrasena = contrasena;
    }

    // Constructor con todos los parámetros
    public Usuario(int id, String nombreUsuario, String contrasena, String email, String rol) {
        this(id, nombreUsuario, contrasena);
        this.email = email;
        this.rol = rol;
    }

    // Getters y Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public void setNombreUsuario(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
    }

    public String getContrasena() {
        return contrasena;
    }

    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }

    @Override
    public String toString() {
        return "Usuario{" +
                "id=" + id +
                ", nombreUsuario='" + nombreUsuario + '\'' +
                ", email='" + email + '\'' +
                ", rol='" + rol + '\'' +
                '}';
    }
}