package com.grupo2_2dam.tpv_software.util.tratadodetexto;

import com.grupo2_2dam.tpv_software.objetos.Usuario;
import com.grupo2_2dam.tpv_software.util.basededatos.DatosConexion;
import org.json.JSONObject;
import java.io.*;
import java.nio.file.*;

public class WRJSON {

    private static final String DIR_NAME = ".tpv_software";
    private static final String FILE_NAME_DATABASE = "database_info.json";
    private static final String FILE_NAME_USER = "user_info.json";
    private static final String HOME_DIR = System.getProperty("user.home");
    private static final Path FILE_PATH_DATABASE_INFO = Paths.get(HOME_DIR, DIR_NAME, FILE_NAME_DATABASE);
    private static final Path FILE_PATH_USER = Paths.get(HOME_DIR, DIR_NAME, FILE_NAME_USER);

    public static DatosConexion leerJSONBaseDeDatos() {
        DatosConexion defecto = new DatosConexion(2, "", "localhost", 5432, "tpv", "postgres", "admin");
        if (!Files.exists(FILE_PATH_DATABASE_INFO)) {
            System.out.println("Archivo de configuración no encontrado. Usando valores por defecto.");
            return defecto;
        }
        try (BufferedReader reader = Files.newBufferedReader(FILE_PATH_DATABASE_INFO)) {
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) sb.append(line);
            JSONObject jo = new JSONObject(sb.toString());
            int modo = jo.optInt("modo", 2);
            String url = jo.optString("url", "");
            String host = jo.optString("host", "localhost");
            int puerto = jo.optInt("puerto", 5432);
            String nombre = jo.optString("nombre", "postgres");
            String usuario = jo.optString("usuario", "postgres");
            String contrasena = jo.optString("contrasena", "admin");
            return new DatosConexion(modo, url, host, puerto, nombre, contrasena, usuario);
        } catch (IOException | org.json.JSONException e) {
            System.err.println("Error leyendo JSON: " + e.getMessage());
            return defecto;
        }
    }

    public static boolean escribirJSONBaseDeDatos(DatosConexion dc) {
        try {
            Files.createDirectories(FILE_PATH_DATABASE_INFO.getParent());
            JSONObject jo = new JSONObject();
            jo.put("modo", dc.getModo());
            jo.put("url", dc.getUrl());
            jo.put("host", dc.getHost());
            jo.put("puerto", dc.getPuerto());
            jo.put("nombre", dc.getNombre());
            jo.put("usuario", dc.getUsuario());
            jo.put("contrasena", dc.getContrasena());
            try (BufferedWriter writer = Files.newBufferedWriter(FILE_PATH_DATABASE_INFO)) {
                writer.write(jo.toString(4));
            }
            System.out.println("Configuración guardada en: " + FILE_PATH_DATABASE_INFO);
            return true;
        } catch (IOException e) {
            System.err.println("Error escribiendo JSON: " + e.getMessage());
            return false;
        }
    }

    public void crearJSONUsuarioActual(Usuario usuario) {
        JSONObject jo = new JSONObject();
        jo.put("cod_usuario", usuario.getCod_usuario());
        jo.put("nombre_usuario", usuario.getNombre_usuario());
        jo.put("imagen_ruta", usuario.getImagenRuta() != null ? usuario.getImagenRuta() : "");
        try {
            Files.createDirectories(FILE_PATH_USER.getParent());
            try (BufferedWriter writer = Files.newBufferedWriter(FILE_PATH_USER)) {
                writer.write(jo.toString(4));
            }
            System.out.println("Usuario actual guardado en: " + FILE_PATH_USER);
        } catch (IOException e) {
            System.err.println("Error escribiendo JSON: " + e.getMessage());
        }
    }

    public Usuario leerJSONUsuarioActual() {
        if (!Files.exists(FILE_PATH_USER)) {
            System.out.println("Archivo de usuario actual no encontrado.");
            return null;
        }
        try (BufferedReader reader = Files.newBufferedReader(FILE_PATH_USER)) {
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) sb.append(line);
            JSONObject jo = new JSONObject(sb.toString());
            return new Usuario(jo.optInt("cod_usuario", 0), jo.optString("nombre_usuario", "desconocido"), null, jo.optString("imagen_ruta", null));
        } catch (IOException | org.json.JSONException e) {
            System.err.println("Error leyendo JSON: " + e.getMessage());
        }
        return null;
    }
}