package com.grupo2_2dam.tpv_software.util.basededatos.crud;

import com.grupo2_2dam.tpv_software.util.basededatos.ConexionDB;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Todos los métodos hechos por Marcos :)
 */
public class ConsultasUpdate {

    private static final String sqlActualizarProducto = "UPDATE PRODUCTOS SET NOMBRE_PRODUCTO = ?, PRECIO_VENTA_PRODUCTO = ? WHERE UPPER(NOMBRE_PRODUCTO) = UPPER(?) AND COD_CATEGORIA = ?";
    private static final String sqlActualizarCategoria = "UPDATE CATEGORIAS SET NOMBRE_CATEGORIA = ? WHERE UPPER(NOMBRE_CATEGORIA) = UPPER(?)";

    public boolean actualizarCategoria(String nombreOriginal, String nuevoNombre) {
        String sql = "UPDATE CATEGORIAS SET NOMBRE_CATEGORIA = ? WHERE UPPER(NOMBRE_CATEGORIA) = UPPER(?)";
        try (Connection conn = ConexionDB.obtenerConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, nuevoNombre.trim());
            pstmt.setString(2, nombreOriginal.trim());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean actualizarProducto(String nombreOriginal, int codCategoria, String nuevoNombre, double nuevoPrecio) {
        String sql = "UPDATE PRODUCTOS SET NOMBRE_PRODUCTO = ?, PRECIO_VENTA_PRODUCTO = ? WHERE UPPER(NOMBRE_PRODUCTO) = UPPER(?) AND COD_CATEGORIA = ?";
        try (Connection conn = ConexionDB.obtenerConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, nuevoNombre.trim());
            pstmt.setDouble(2, nuevoPrecio);
            pstmt.setString(3, nombreOriginal.trim());
            pstmt.setInt(4, codCategoria);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

}
