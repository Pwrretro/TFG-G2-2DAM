package com.grupo2_2dam.tpv_software.util.basededatos;

import com.grupo2_2dam.tpv_software.util.Alertas;
import com.grupo2_2dam.tpv_software.util.tratadodetexto.WRJSON;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexionDB {

    /*
    private static final String URL = "jdbc:postgresql://localhost:5432/tpv";
    private static final String USER = "postgres";
    private static final String PASSWORD = "admin"; //Lo cambiamos a cada rato 😭😭😭😭😭😭😭

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
    */

    public static Connection getConnectionWithObject(DatosConexion dc) throws SQLException {

        String url = "";

        if (dc.getModo() == 1){
            url = dc.getUrl();
        }

        if (dc.getModo() == 2){
            url = "jdbc:postgresql://" + dc.getHost() + ":" + dc.getPuerto() + "/" + dc.getNombre();
        }

        return DriverManager.getConnection(url, dc.getUsuario(), dc.getContrasena());
    }

    /**
     * Obtener conexion con la base de datos leyendo el JSON con los datos de conexión
     * @return
     * @throws SQLException
     */
    public static Connection obtenerConexion() throws SQLException {
        DatosConexion dc = WRJSON.leerJSONBaseDeDatos();
        return ConexionDB.getConnectionWithObject(dc);
    }

    /*
    //Modo 1
    public static Connection getConnectionWithURL(DatosConexion dc) throws SQLException {
        return DriverManager.getConnection(dc.getUrl(), dc.getUsuario(), dc.getContrasena());
    }

    //Modo 2
    public static Connection getConnectionWithHost(DatosConexion dc) throws SQLException {

        String host = dc.getHost();
        int port = dc.getPuerto();
        String dbName = dc.getNombre();
        String USER = dc.getUsuario();
        String PASSWORD = dc.getContrasena();

        String URL = "jdbc:postgresql://" + host + ":" + port + "/" +dbName;

        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
    */
}