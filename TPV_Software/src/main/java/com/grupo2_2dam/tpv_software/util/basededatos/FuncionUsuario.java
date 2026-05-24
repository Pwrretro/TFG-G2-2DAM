package com.grupo2_2dam.tpv_software.util.basededatos;

import com.grupo2_2dam.tpv_software.objetos.Usuario;
import com.grupo2_2dam.tpv_software.util.Alertas;
import com.grupo2_2dam.tpv_software.util.tratadodetexto.HashContrasena;
import com.grupo2_2dam.tpv_software.util.tratadodetexto.WRJSON;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class FuncionUsuario {

    private static String sqlComprobarUsuario = "SELECT 1 FROM USUARIOS WHERE NOMBRE_USUARIO = ?";
    private static String sqlCrearUsuario = "INSERT INTO USUARIOS (NOMBRE_USUARIO, CONTRASENA_USUARIO) VALUES (?, ?)";
    Alertas alertas = new Alertas();

    public Usuario verificarCredenciales(String username, String password) {
        String sql = "SELECT cod_usuario, contrasena_usuario, imagen_ruta FROM USUARIOS WHERE NOMBRE_USUARIO = ?";
        try (Connection conn = ConexionDB.obtenerConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                String hashAlmacenado = rs.getString("contrasena_usuario");
                if (HashContrasena.verifyPassword(password, hashAlmacenado)) {
                    return new Usuario(rs.getInt("cod_usuario"), username, null, rs.getString("imagen_ruta"));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean crearCuenta(String nombre_usuario, String contrasena_usuario) {
        try {
            Connection conn = ConexionDB.obtenerConexion();
            PreparedStatement pstmt = conn.prepareStatement(sqlComprobarUsuario);
            pstmt.setString(1, nombre_usuario);
            ResultSet rs = pstmt.executeQuery();
            boolean existe = rs.next();
            if (existe) {
                System.out.println("El usuario ya existe. Por favor, elige otro nombre de usuario.");
                return false;
            }
            pstmt = conn.prepareStatement(sqlCrearUsuario);
            pstmt.setString(1, nombre_usuario);
            pstmt.setString(2, HashContrasena.hashPassword(contrasena_usuario));
            pstmt.executeUpdate();
            System.out.println("Usuario creado correctamente.");
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            return false;
        }
        return true;
    }

    public void eliminarCuenta(String nombre_usuario) {
        if (nombre_usuario.equals("admin")) {
            alertas.mostrarAlerta("Error al eliminar el usuario", "No se puede eliminar el usuario admin.");
            return;
        }
        try {
            Connection conn = ConexionDB.obtenerConexion();
            String sqlEliminarUsuario = "DELETE FROM USUARIOS WHERE NOMBRE_USUARIO = ?";
            PreparedStatement pstmt = conn.prepareStatement(sqlEliminarUsuario);
            pstmt.setString(1, nombre_usuario);
            int filasEliminadas = pstmt.executeUpdate();
            if (filasEliminadas > 0) {
                System.out.println("Usuario eliminado correctamente.");
            } else {
                System.out.println("No se encontró el usuario para eliminar.");
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    public void guardarUsuarioActual(Usuario u) {
        WRJSON wrjson = new WRJSON();
        wrjson.crearJSONUsuarioActual(u);
    }

    public Usuario obtenerUsuarioActual() {
        WRJSON wrjson = new WRJSON();
        return wrjson.leerJSONUsuarioActual();
    }

    public boolean actualizarImagenUsuario(Usuario usuario, String nuevaImagenRuta) {
        String sql = "UPDATE USUARIOS SET IMAGEN_RUTA = ? WHERE COD_USUARIO = ?";
        try (Connection conn = ConexionDB.obtenerConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, nuevaImagenRuta);
            pstmt.setInt(2, usuario.getCod_usuario());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}