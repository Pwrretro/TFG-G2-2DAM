package com.grupo2_2dam.tpv_software.objetos;

public class Categoria {

    private int codigo;
    private String nombre;
    private String imagenRuta;

    public Categoria(int codigo, String nombre, String imagenRuta) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.imagenRuta = imagenRuta;
    }

    public int getCodigo() {
        return codigo;
    }

    public void setCodigo(int codigo) {
        this.codigo = codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getImagenRuta() {
        return imagenRuta;
    }

    public void setImagenRuta(String imagenRuta) {
        this.imagenRuta = imagenRuta;
    }
}