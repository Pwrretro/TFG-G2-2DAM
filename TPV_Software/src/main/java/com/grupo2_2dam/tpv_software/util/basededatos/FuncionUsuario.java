package com.grupo2_2dam.tpv_software.util.basededatos;
import com.grupo2_2dam.tpv_software.objetos.Usuario;
import com.grupo2_2dam.tpv_software.util.Alertas;
import com.grupo2_2dam.tpv_software.util.tratadodetexto.HashContrasena;
import com.grupo2_2dam.tpv_software.util.tratadodetexto.WRJSON;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class FuncionUsuario {

    private static String sqlComprobarUsuario = "SELECT 1 FROM USUARIOS WHERE NOMBRE_USUARIO = ?";
    private static String sqlCrearUsuario = "INSERT INTO USUARIOS (NOMBRE_USUARIO, CONTRASENA_USUARIO) VALUES (?, ?)";

    Alertas alertas = new Alertas();

    public Usuario verificarCredenciales(String username, String password) {
        String sql = "SELECT cod_usuario, contrasena_usuario FROM USUARIOS WHERE NOMBRE_USUARIO = ?";
        try (Connection conn = ConexionDB.obtenerConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                String hashAlmacenado = rs.getString("contrasena_usuario");
                if (HashContrasena.verifyPassword(password, hashAlmacenado)) {
                    return new Usuario(rs.getInt("cod_usuario"), username, null);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Método para crear un nuevo usuario en la base de datos
     * @param nombre_usuario nombre del usuario a crear
     * @param contrasena_usuario contraseña del usuario a crear, que será hasheada antes de guardarse en la base de datos
     */
    public boolean crearCuenta(String nombre_usuario, String contrasena_usuario) {

        try{
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

        try{
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

    /**
     * Guardamos el usuario actual en un JSON para poder usarlo en otras vistas
     * @param u Usuario actual que se ha logueado correctamente, con su cod_usuario y nombre_usuario, para usarlo en otras vistas
     */
    public void guardarUsuarioActual(Usuario u){
        WRJSON wrjson = new WRJSON();
        wrjson.crearJSONUsuarioActual(u);
    }

    /**
     * Obtenemos el usuario actual desde el JSON para usarlo en otras vistas
     * @return Usuario actual que se ha logueado correctamente, con su cod_usuario y nombre_usuario, para usarlo en otras vistas
     */
    public Usuario obtenerUsuarioActual(){
        WRJSON wrjson = new WRJSON();
        Usuario u = wrjson.leerJSONUsuarioActual();
        return u;
    }
}