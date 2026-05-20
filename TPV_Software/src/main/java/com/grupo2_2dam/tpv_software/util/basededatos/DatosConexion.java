package com.grupo2_2dam.tpv_software.util.basededatos;

public class DatosConexion {

    //Modo de conexión
    int modo;

    //Modo 1 - por url
    String url;

    //Modo 2 - por host
    String host;
    int puerto;
    String nombre;

    //Datos Usuario
    String contrasena, usuario;

    /**
     * Datos de conexión con modo, url, host, puerto, nombre, contraseña y usuario
     * @param modo
     * @param url
     * @param host
     * @param puerto
     * @param nombre
     * @param contrasena
     * @param usuario
     */
    public DatosConexion(int modo, String url, String host, int puerto, String nombre, String contrasena, String usuario) {
        this.modo = modo;
        this.url = url;
        this.host = host;
        this.puerto = puerto;
        this.nombre = nombre;
        this.contrasena = contrasena;
        this.usuario = usuario;
    }

    @Override
    public String toString() {
        return "DatosConexion{" +
                "modo=" + modo +
                ", url='" + url + '\'' +
                ", host='" + host + '\'' +
                ", puerto=" + puerto +
                ", nombre='" + nombre + '\'' +
                ", contrasena='" + contrasena + '\'' +
                ", usuario='" + usuario + '\'' +
                '}';
    }

    public int getModo() {
        return modo;
    }

    public void setModo(int modo) {
        this.modo = modo;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPuerto() {
        return puerto;
    }

    public void setPuerto(int puerto) {
        this.puerto = puerto;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getContrasena() {
        return contrasena;
    }

    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }
}
