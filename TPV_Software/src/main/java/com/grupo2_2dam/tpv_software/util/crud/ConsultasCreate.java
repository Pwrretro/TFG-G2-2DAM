package com.grupo2_2dam.tpv_software.util.crud;

import com.grupo2_2dam.tpv_software.util.tratadodetexto.HashContrasena;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import static com.grupo2_2dam.tpv_software.util.basededatos.ConexionDB.obtenerConexion;

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
}
