package com.sena.springecommerce.dto;

public class AuthResponse {
    private String token;
    private String tipo = "Bearer";
    private String email;
    private String rol;
    private String nombre;
    private String mensaje;

    public AuthResponse() {
    }

    public AuthResponse(String token, String email, String rol, String nombre, String mensaje) {
        this.token = token;
        this.tipo = "Bearer";
        this.email = email;
        this.rol = rol;
        this.nombre = nombre;
        this.mensaje = mensaje;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
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

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }
}
