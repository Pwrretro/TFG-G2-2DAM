package com.grupo2_2dam.tpv_software.util.tratadodeimagenes;

import javafx.geometry.Rectangle2D;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class ProcesarImagen {

    /**
     * @param imagenurl se recibe la url de la imagen
     * @return regresa la imagen ya procesada
     */
    public Image procesarImagen(String imagenurl, ImageView foto_de_perfil) {
        // Cargar la imagen original (sin redimensionar aún)
        Image imagenOriginal = new Image(getClass().getResourceAsStream(imagenurl), 90,90,true,true);

        // Obtener dimensiones originales
        double ancho = imagenOriginal.getWidth();
        double alto = imagenOriginal.getHeight();

        // Calcular el cuadrado central más grande posible (min(ancho, alto))
        double lado = Math.min(ancho, alto);
        double x = (ancho - lado) / 2;
        double y = (alto - lado) / 2;

        // Aplicar viewport para recortar el cuadrado central
        foto_de_perfil.setViewport(new Rectangle2D(x, y, lado, lado));

        // Ajustar el ImageView: ya no preservamos la proporción porque el viewport ya da un cuadrado
        foto_de_perfil.setPreserveRatio(false);
        foto_de_perfil.setFitWidth(90);
        foto_de_perfil.setFitHeight(90);
        foto_de_perfil.setSmooth(true); // calidad de escalado

        return imagenOriginal;
    }

}
