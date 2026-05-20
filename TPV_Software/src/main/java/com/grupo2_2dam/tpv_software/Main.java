package com.grupo2_2dam.tpv_software;

import io.github.palexdev.materialfx.theming.JavaFXThemes;
import io.github.palexdev.materialfx.theming.MaterialFXStylesheets;
import io.github.palexdev.materialfx.theming.UserAgentBuilder;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {
    @Override
    public void start(Stage stage) throws IOException {

        // --- Inicialización del tema ---
       UserAgentBuilder.builder()
                .themes(JavaFXThemes.MODENA)                     // Tema base de JavaFX
                .themes(MaterialFXStylesheets.forAssemble(true)) // Tema de MaterialFX
                .setDeploy(true)                                // Extraer los assets necesarios
                .setResolveAssets(true)                         // Resuelve las rutas de los assets
                .build()
                .setGlobal();                                   // Para hacerlo global

        //System.setProperty("javafx.controls.useragentStylesheet", "true");

        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("vistas/inicio_de_sesion.fxml")); //Arreglado de mejor manera
        Scene escenaPrincipal = new Scene(fxmlLoader.load(), 960, 750); //Se define un tamaño que no rompe la app - 960, 750

        escenaPrincipal.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());

        stage.setTitle("PurpleBOX");
        //Tamaño mínimo de la escena
        stage.minHeightProperty().bind(escenaPrincipal.heightProperty());
        stage.minWidthProperty().bind(escenaPrincipal.widthProperty());

        stage.getIcons().clear();
        stage.getIcons().add(new Image(getClass().getResourceAsStream("/imagenes/icon_tpv.png")));

        stage.setScene(escenaPrincipal);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}