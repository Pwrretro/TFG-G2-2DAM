package com.grupo2_2dam.tpv_software.util.tratadodetexto;

import com.grupo2_2dam.tpv_software.util.basededatos.DatosConexion;
import org.json.JSONObject;

import java.io.*;
import java.nio.file.*;

public class WRJSON {

    private static final String DIR_NAME = ".tpv_software";
    private static final String FILE_NAME = "database_info.json";
    private static final String HOME_DIR = System.getProperty("user.home");
    private static final Path FILE_PATH = Paths.get(HOME_DIR, DIR_NAME, FILE_NAME);

    public static DatosConexion leerJSON() {
        // Valores por defecto (modo 2 = host, localhost, 5432, postgres, admin, admin)
        DatosConexion defecto = new DatosConexion(2, "", "localhost", 5432, "tpv", "postgres", "admin");

        if (!Files.exists(FILE_PATH)) {
            System.out.println("Archivo de configuración no encontrado. Usando valores por defecto.");
            return defecto;
        }

        try (BufferedReader reader = Files.newBufferedReader(FILE_PATH)) {
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            JSONObject jo = new JSONObject(sb.toString());

            int modo = jo.optInt("modo", 2);
            String url = jo.optString("url", "");
            String host = jo.optString("host", "localhost");
            int puerto = jo.optInt("puerto", 5432);
            String nombre = jo.optString("nombre", "postgres");
            String usuario = jo.optString("usuario", "postgres");
            String contrasena = jo.optString("contrasena", "admin");

            //System.out.println("Lectura JSON correcta");
            return new DatosConexion(modo, url, host, puerto, nombre, contrasena, usuario);

        } catch (IOException | org.json.JSONException e) {
            System.err.println("Error leyendo JSON: " + e.getMessage());
            return defecto;
        }
    }

    /**
     * Escribe la configuración en el archivo JSON.
     * Crea el directorio si no existe.
     */
    public static boolean escribirJSON(DatosConexion dc) {
        try {
            // Crear directorio si no existe
            Files.createDirectories(FILE_PATH.getParent());

            JSONObject jo = new JSONObject();
            jo.put("modo", dc.getModo());
            jo.put("url", dc.getUrl());
            jo.put("host", dc.getHost());
            jo.put("puerto", dc.getPuerto());
            jo.put("nombre", dc.getNombre());
            jo.put("usuario", dc.getUsuario());
            jo.put("contrasena", dc.getContrasena());

            try (BufferedWriter writer = Files.newBufferedWriter(FILE_PATH)) {
                writer.write(jo.toString(4)); // Indentación de 4 espacios
            }
            System.out.println("Configuración guardada en: " + FILE_PATH);
            return true;

        } catch (IOException e) {
            System.err.println("Error escribiendo JSON: " + e.getMessage());
            return false;
        }
    }
}