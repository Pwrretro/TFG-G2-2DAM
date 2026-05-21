package com.grupo2_2dam.tpv_software.util.basededatos.crud;

import com.grupo2_2dam.tpv_software.util.basededatos.ConexionDB;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;


/**
 * Todos los métodos hechos por Marcos :)
 */
public class ConsultasDelete {

    String sqlBorrarProductos = "DELETE FROM PRODUCTOS WHERE COD_CATEGORIA = (SELECT COD_CATEGORIA FROM CATEGORIAS WHERE UPPER(NOMBRE_CATEGORIA) = UPPER(?))";
    String sqlBorrarCat = "DELETE FROM CATEGORIAS WHERE UPPER(NOMBRE_CATEGORIA) = UPPER(?)";
    String sql = "DELETE FROM PRODUCTOS WHERE UPPER(NOMBRE_PRODUCTO) = UPPER(?) AND COD_CATEGORIA = ?";

    public boolean eliminarCategoria(String nombre) {
        String sqlBorrarProductos = "DELETE FROM PRODUCTOS WHERE COD_CATEGORIA = (SELECT COD_CATEGORIA FROM CATEGORIAS WHERE UPPER(NOMBRE_CATEGORIA) = UPPER(?))";
        String sqlBorrarCat = "DELETE FROM CATEGORIAS WHERE UPPER(NOMBRE_CATEGORIA) = UPPER(?)";
        try (Connection conn = ConexionDB.obtenerConexion()) {
            conn.setAutoCommit(false);
            try (PreparedStatement pstmtProd = conn.prepareStatement(sqlBorrarProductos);
                 PreparedStatement pstmtCat = conn.prepareStatement(sqlBorrarCat)) {
                pstmtProd.setString(1, nombre.trim());
                pstmtProd.executeUpdate();
                pstmtCat.setString(1, nombre.trim());
                int affected = pstmtCat.executeUpdate();
                conn.commit();
                return affected > 0;
            } catch (SQLException ex) {
                conn.rollback();
                throw ex;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean eliminarProducto(String nombre, int codCategoria) {
        String sql = "DELETE FROM PRODUCTOS WHERE UPPER(NOMBRE_PRODUCTO) = UPPER(?) AND COD_CATEGORIA = ?";
        try (Connection conn = ConexionDB.obtenerConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, nombre.trim());
            pstmt.setInt(2, codCategoria);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

}
