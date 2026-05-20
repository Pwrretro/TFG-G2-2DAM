package com.grupo2_2dam.tpv_software.objetos;

public class DetalleTicket {
    private String nombreProducto;
    private double cantidad;
    private double precioUnitario;
    private double totalLinea;

    /**
     * Detalle del ticket, con el nombre del producto, la cantidad, el precio unitario y el total de la línea
     * @param nombreProducto
     * @param cantidad
     * @param precioUnitario
     * @param totalLinea
     */
    public DetalleTicket(String nombreProducto, double cantidad, double precioUnitario, double totalLinea) {
        this.nombreProducto = nombreProducto;
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
        this.totalLinea = totalLinea;
    }

    public String getNombreProducto() {
        return nombreProducto;
    }

    public void setNombreProducto(String nombreProducto) {
        this.nombreProducto = nombreProducto;
    }

    public double getCantidad() {
        return cantidad;
    }

    public void setCantidad(double cantidad) {
        this.cantidad = cantidad;
    }

    public double getPrecioUnitario() {
        return precioUnitario;
    }

    public void setPrecioUnitario(double precioUnitario) {
        this.precioUnitario = precioUnitario;
    }

    public double getTotalLinea() {
        return totalLinea;
    }

    public void setTotalLinea(double totalLinea) {
        this.totalLinea = totalLinea;
    }
}