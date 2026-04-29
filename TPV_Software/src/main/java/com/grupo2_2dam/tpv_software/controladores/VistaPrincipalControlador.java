package com.grupo2_2dam.tpv_software.controladores;

import com.grupo2_2dam.tpv_software.util.CambiarVistas;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Rectangle2D;
import javafx.scene.control.Label;

//Liberías que se utilizaran, no tocar que luego se me olvidan xd
import io.github.palexdev.materialfx.controls.MFXButton;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.io.IOException;

public class VistaPrincipalControlador {

    @FXML
    private MFXButton regresarButton;

    @FXML
    private Label nombre_Usuario;

    @FXML
    private ImageView foto_de_perfil; //Imagen de perfil de usuario

    @FXML
    private ImageView foto_categoria;

    @FXML
    private ImageView foto_mango;

    /*
    Definimos usuario
    public void setUsuario(String nombreUsuario) {

    }
    */

    @FXML
    public void initialize() { // Aquí se cargaran los productos e imagenes de la base de datos

        //Perfil ---------------------------
        nombre_Usuario.setText("Pawer"); //15 carácteres máximos para evitar errores

        String imagenurl = "/imagenes/kanna.png"; // Esto hay que cambiarlo por la base de datos
        foto_de_perfil.setImage(procesarImagen(imagenurl));
        //Perfil ---------------------------


        //Productos -----------------------
        String categoria = "/imagenes/frutas.jpg";
        foto_categoria.setImage(procesarImagen(categoria));

        String mango = "/imagenes/mango.jpg";
        foto_mango.setImage(procesarImagen(mango));
        //Productos -----------------------

    }

    public void cerrar_sesion(ActionEvent actionEvent) throws IOException {
        Stage stage = (Stage) regresarButton.getScene().getWindow();
        String vistaPrincipal = "/com/grupo2_2dam/tpv_software/vistas/inicio_de_sesion.fxml";
        CambiarVistas.cambiarVista(vistaPrincipal, stage); // Cambiar de vista
    }

    /**
     * @param imagenurl se recibe la url de la imagen
     * @return regresa la imagen ya procesada
     */
    private Image procesarImagen(String imagenurl) {
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