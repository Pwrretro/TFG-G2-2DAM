package com.grupo2_2dam.tpv_software.util;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class CambiarVistas {

    public static void cambiarVista(String fxmlPath, Stage stage) throws IOException {

        // Cargar el FXML
        Parent root = FXMLLoader.load(CambiarVistas.class.getResource(fxmlPath));

        // Crear la nueva escena con el mismo tamaño que la actual
        Scene nuevaEscena = new Scene(root, stage.getScene().getWidth(), stage.getScene().getHeight());

        // Reaplicar la hoja de estilos CSS (la misma que se usa en el Main)
        nuevaEscena.getStylesheets().add(CambiarVistas.class.getResource("/css/styles.css").toExternalForm());

        // Aplicar la nueva escena al Stage
        stage.setScene(nuevaEscena);

        // Opcional: centrar la ventana en la pantalla
        // stage.centerOnScreen();

        // Opcional: ajustar el tamaño de la ventana al contenido
        stage.sizeToScene();
    }

    public static Object cambiarVistaConControlador(String fxmlPath, Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader(CambiarVistas.class.getResource(fxmlPath));
        javafx.scene.Parent root = loader.load();
        javafx.scene.Scene nuevaEscena = new javafx.scene.Scene(root, stage.getScene().getWidth(), stage.getScene().getHeight());
        nuevaEscena.getStylesheets().add(CambiarVistas.class.getResource("/css/styles.css").toExternalForm());
        stage.setScene(nuevaEscena);
        return loader.getController();
    }
}