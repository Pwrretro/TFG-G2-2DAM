package com.grupo2_2dam.tpv_software.objetos;

public class Usuario {

    int cod_usuario;
    String nombre_usuario;
    String contrasena_usuario;

    public Usuario(int cod_usuario, String nombre_usuario, String contrasena_usuario) {
        this.cod_usuario = cod_usuario;
        this.nombre_usuario = nombre_usuario;
        this.contrasena_usuario = contrasena_usuario;
    }

    public int getCod_usuario() {
        return cod_usuario;
    }

    public void setCod_usuario(int cod_usuario) {
        this.cod_usuario = cod_usuario;
    }

    public String getNombre_usuario() {
        return nombre_usuario;
    }

    public void setNombre_usuario(String nombre_usuario) {
        this.nombre_usuario = nombre_usuario;
    }

    public String getContrasena_usuario() {
        return contrasena_usuario;
    }

    public void setContrasena_usuario(String contrasena_usuario) {
        this.contrasena_usuario = contrasena_usuario;
    }
}
