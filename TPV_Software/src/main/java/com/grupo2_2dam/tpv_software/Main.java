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

    /*
     * Funcionamiento de la clase util/HashContraseña
     */
    //public class Main {
    //    public static void main(String[] args) {
    //        try {
    //            // 1. Registras un usuario: generar un hash y guarda en DB
    //            String password = "contraseña123";
    //            String hash = PasswordHasher.hashPassword(password);
    //            System.out.println("Hash que se guarda en DB: " + hash);
    //
    //            // 2. Validar login: comparar contraseña con el hash
    //            String passwordIngresada = "contraseña123";
    //            boolean esCorrecta = PasswordHasher.verifyPassword(passwordIngresada, hash);
    //            System.out.println("Contraseña correcta " + esCorrecta);
    //
    //            // Prueba contraseña incorrecta
    //            String passwordIncorrecta = "Contrasena1234";
    //            boolean esIncorrecta = PasswordHasher.verifyPassword(passwordIncorrecta, hash);
    //            System.out.println("Contraseña incorrecta " + esIncorrecta);
    //
    //        } catch (Exception e) {
    //            e.printStackTrace();
    //        }
    //    }
    //}
}