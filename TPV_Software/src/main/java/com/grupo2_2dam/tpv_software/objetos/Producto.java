package com.grupo2_2dam.tpv_software.objetos;

public class Producto {

    private String nombre;
    private double precio;
    private String imagenRuta;

    public Producto(String nombre, double precio, String imagenRuta) {
        this.nombre = nombre;
        this.precio = precio;
        this.imagenRuta = imagenRuta;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }

    public String getImagenRuta() {
        return imagenRuta;
    }

    public void setImagenRuta(String imagenRuta) {
        this.imagenRuta = imagenRuta;
    }
}
