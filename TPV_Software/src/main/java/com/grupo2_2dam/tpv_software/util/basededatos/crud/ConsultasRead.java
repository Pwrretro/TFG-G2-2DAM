package com.grupo2_2dam.tpv_software.util.basededatos.crud;

import com.grupo2_2dam.tpv_software.objetos.Categoria;
import com.grupo2_2dam.tpv_software.objetos.Producto;
import com.grupo2_2dam.tpv_software.util.basededatos.ConexionDB;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ConsultasRead {

    public List<Categoria> obtenerCategorias() {
        List<Categoria> lista = new ArrayList<>();
        String sql = "SELECT COD_CATEGORIA, NOMBRE_CATEGORIA, IMAGEN_RUTA FROM CATEGORIAS ORDER BY COD_CATEGORIA ASC";
        try (Connection conn = ConexionDB.obtenerConexion();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                lista.add(new Categoria(rs.getInt("COD_CATEGORIA"), rs.getString("NOMBRE_CATEGORIA"), rs.getString("IMAGEN_RUTA")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
        return lista;
    }

    public List<Producto> obtenerProductosPorCategoria(int codCategoria) {
        List<Producto> lista = new ArrayList<>();
        String sql = "SELECT NOMBRE_PRODUCTO, PRECIO_VENTA_PRODUCTO, IMAGEN_RUTA FROM PRODUCTOS WHERE COD_CATEGORIA = ? ORDER BY NOMBRE_PRODUCTO ASC";
        try (Connection conn = ConexionDB.obtenerConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, codCategoria);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                lista.add(new Producto(rs.getString("NOMBRE_PRODUCTO"), rs.getDouble("PRECIO_VENTA_PRODUCTO"), rs.getString("IMAGEN_RUTA")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
        return lista;
    }

    public boolean existeCategoria(String nombre) {
        String sql = "SELECT COUNT(*) FROM CATEGORIAS WHERE UPPER(NOMBRE_CATEGORIA) = UPPER(?)";
        try (Connection conn = ConexionDB.obtenerConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, nombre.trim());
            ResultSet rs = pstmt.executeQuery();
            return rs.next() && rs.getInt(1) > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean existeProducto(String nombre, int codCategoria) {
        String sql = "SELECT COUNT(*) FROM PRODUCTOS WHERE UPPER(NOMBRE_PRODUCTO) = UPPER(?) AND COD_CATEGORIA = ?";
        try (Connection conn = ConexionDB.obtenerConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, nombre.trim());
            pstmt.setInt(2, codCategoria);
            ResultSet rs = pstmt.executeQuery();
            return rs.next() && rs.getInt(1) > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public Double obtenerPrecioProducto(String nombre, int codCategoria) {
        String sql = "SELECT PRECIO_VENTA_PRODUCTO FROM PRODUCTOS WHERE UPPER(NOMBRE_PRODUCTO) = UPPER(?) AND COD_CATEGORIA = ?";
        try (Connection conn = ConexionDB.obtenerConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, nombre.trim());
            pstmt.setInt(2, codCategoria);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) return rs.getDouble("PRECIO_VENTA_PRODUCTO");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}