package com.grupo2_2dam.tpv_software.util.tratadodeimagenes;

import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class GestorImagenes {

    private static final String DIR_NAME = ".tpv_software";
    private static final String SUBDIR_IMAGENES = "imagenes";
    private static final Path BASE_PATH = Paths.get(System.getProperty("user.home"), DIR_NAME, SUBDIR_IMAGENES);

    static {
        try {
            Files.createDirectories(BASE_PATH);
            System.out.println("Directorio de imágenes creado/verificado: " + BASE_PATH);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String guardarImagen(File archivoOrigen, String nombreBase, int anchoDestino, int altoDestino) {
        try {
            System.out.println("Procesando imagen: " + archivoOrigen.getAbsolutePath());
            Image imagenOriginal = new Image(archivoOrigen.toURI().toString());
            double anchoOrig = imagenOriginal.getWidth();
            double altoOrig = imagenOriginal.getHeight();
            double lado = Math.min(anchoOrig, altoOrig);
            double x = (anchoOrig - lado) / 2;
            double y = (altoOrig - lado) / 2;

            // Recortar cuadrado central
            WritableImage imagenCuadrada = new WritableImage((int) lado, (int) lado);
            PixelReader reader = imagenOriginal.getPixelReader();
            PixelWriter writer = imagenCuadrada.getPixelWriter();
            for (int i = 0; i < lado; i++) {
                for (int j = 0; j < lado; j++) {
                    writer.setArgb(i, j, reader.getArgb((int) x + i, (int) y + j));
                }
            }

            // Escalar usando ImageView y snapshot (puro JavaFX)
            javafx.scene.image.ImageView imageView = new javafx.scene.image.ImageView(imagenCuadrada);
            imageView.setFitWidth(anchoDestino);
            imageView.setFitHeight(altoDestino);
            imageView.setPreserveRatio(false);
            imageView.setSmooth(true);
            Image imagenEscalada = imageView.snapshot(null, null);

            // Convertir a BufferedImage
            BufferedImage bufferedEscalada = new BufferedImage(anchoDestino, altoDestino, BufferedImage.TYPE_INT_ARGB);
            PixelReader readerEsc = imagenEscalada.getPixelReader();
            for (int yPx = 0; yPx < altoDestino; yPx++) {
                for (int xPx = 0; xPx < anchoDestino; xPx++) {
                    int argb = readerEsc.getArgb(xPx, yPx);
                    bufferedEscalada.setRGB(xPx, yPx, argb);
                }
            }

            String nombreLimpio = nombreBase.replaceAll("[^a-zA-Z0-9_\\-]", "_");
            String extension = obtenerExtension(archivoOrigen.getName());
            String nombreArchivo = nombreLimpio + "." + extension;
            Path rutaDestino = BASE_PATH.resolve(nombreArchivo);
            System.out.println("Guardando en: " + rutaDestino);

            boolean escrito = ImageIO.write(bufferedEscalada, extension.toUpperCase(), rutaDestino.toFile());
            if (!escrito) {
                System.err.println("No se pudo escribir la imagen con extensión " + extension + ". Probando PNG.");
                extension = "png";
                nombreArchivo = nombreLimpio + "." + extension;
                rutaDestino = BASE_PATH.resolve(nombreArchivo);
                escrito = ImageIO.write(bufferedEscalada, "PNG", rutaDestino.toFile());
            }
            if (escrito) {
                System.out.println("Imagen guardada correctamente: " + rutaDestino);
                return SUBDIR_IMAGENES + "/" + nombreArchivo;
            } else {
                System.err.println("Error: no se pudo guardar la imagen.");
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static String obtenerExtension(String nombreArchivo) {
        int idx = nombreArchivo.lastIndexOf('.');
        if (idx > 0) {
            String ext = nombreArchivo.substring(idx + 1).toLowerCase();
            if (ext.equals("jpg") || ext.equals("jpeg") || ext.equals("png") || ext.equals("gif")) {
                return ext;
            }
        }
        return "png";
    }
}