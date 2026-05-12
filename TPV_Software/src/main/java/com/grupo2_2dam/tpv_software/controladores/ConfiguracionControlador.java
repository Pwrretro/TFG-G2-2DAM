package com.grupo2_2dam.tpv_software.controladores;

import com.grupo2_2dam.tpv_software.util.CambiarVistas;
import io.github.palexdev.materialfx.controls.*;
import javafx.fxml.FXML;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;

public class ConfiguracionControlador {

    @FXML private ToggleGroup conexionGroup;
    @FXML private MFXRadioButton rbHost, rbURL;
    @FXML private VBox hostOptions, urlOptions;
    @FXML private MFXTextField hostField, portField, urlField, databaseField, usernameField;
    @FXML private MFXCheckbox showAllDatabasesCheck, savePasswordCheck;
    @FXML private MFXButton guardarButton, regresarBoton;
    @FXML private MFXPasswordField passwordField;

    @FXML
    public void initialize() {

        // Floating texts
        hostField.setFloatingText("Host");
        portField.setFloatingText("Puerto");
        urlField.setFloatingText("URL");
        databaseField.setFloatingText("Base de datos");
        usernameField.setFloatingText("Usuario");
        passwordField.setFloatingText("Contraseña");

        // Alternar visibilidad
        rbHost.selectedProperty().addListener((obs, oldVal, newVal) -> {
            hostOptions.setVisible(newVal);
            hostOptions.setManaged(newVal);

            urlOptions.setVisible(!newVal);
            urlOptions.setManaged(!newVal);
        });

        rbHost.setSelected(true);
    }

    public void guardarConfiguracion() throws IOException {

        regresarInicioSesion();
    }

    public void regresarInicioSesion() throws IOException {
        Stage stage = (Stage) regresarBoton.getScene().getWindow();
        String regresar = "/com/grupo2_2dam/tpv_software/vistas/inicio_de_sesion.fxml";
        CambiarVistas.cambiarVista(regresar, stage); // Cambiar de vista
    }
}
