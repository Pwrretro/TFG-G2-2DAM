package com.grupo2_2dam.tpv_software.util.basededatos.crud;

import com.grupo2_2dam.tpv_software.util.basededatos.ConexionDB;
import com.grupo2_2dam.tpv_software.util.tratadodetexto.HashContrasena;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

import static com.grupo2_2dam.tpv_software.util.basededatos.ConexionDB.obtenerConexion;


/**
 * Todos los métodos hechos por Marcos :)
 */
public class ConsultasCreate {

    //Querries para buscar el admin y para
    private final String selectAdminUser = "select * from usuarios where nombre_usuario='admin'";
    private final String createUserAdmin = "insert into usuarios (nombre_usuario, contrasena_usuario) values(?,?)";

    /**
     * Método para crear usuarios en la base de datos
     * @param usuario nombre de usuario
     * @param contrasena contraseña del usuario
     */
    public void createAdminUser(String usuario, String contrasena) {

        try (Connection connection = obtenerConexion()) { //Obtenemos la conexión con la clase ConexionDB
            if (connection != null) {
                try (ResultSet rs = connection.createStatement().executeQuery(selectAdminUser)) { //Verificamos si existe el usuario admin/admin

                    boolean existeAdmin = rs.next();

                    if (!existeAdmin) { //Si existe procedemos a crearlo

                        //Hasheamos las contraseñas
                        String contraseñaHasheada = HashContrasena.hashPassword(contrasena);

                        //Ejecutamos el querry
                        try (PreparedStatement ps = connection.prepareStatement(createUserAdmin)) {
                            ps.setString(1,usuario);
                            ps.setString(2,contraseñaHasheada);

                            ps.executeUpdate();
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    public boolean crearCategoria(String nombre) {
        String sql = "INSERT INTO CATEGORIAS (COD_CATEGORIA, NOMBRE_CATEGORIA) VALUES ((SELECT COALESCE(MAX(COD_CATEGORIA),0)+1 FROM CATEGORIAS), ?)";
        try (Connection conn = ConexionDB.obtenerConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, nombre.trim());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean crearProducto(String nombre, double precio, int codCategoria) {
        String codProducto = "PROD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        String sql = "INSERT INTO PRODUCTOS (COD_PRODUCTO, NOMBRE_PRODUCTO, PRECIO_VENTA_PRODUCTO, COD_CATEGORIA) VALUES (?, ?, ?, ?)";
        try (Connection conn = ConexionDB.obtenerConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, codProducto);
            pstmt.setString(2, nombre.trim());
            pstmt.setDouble(3, precio);
            pstmt.setInt(4, codCategoria);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
