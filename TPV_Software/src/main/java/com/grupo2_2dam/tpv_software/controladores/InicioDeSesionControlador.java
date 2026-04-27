package com.grupo2_2dam.tpv_software.controladores;

import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXPasswordField;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;

public class InicioDeSesionControlador {

    @FXML private MFXTextField usernameField;
    @FXML private MFXPasswordField passwordField;
    @FXML private MFXButton loginButton;

    @FXML
    public void initialize() {
        // El texto flotante sobre los MFXTextField
        usernameField.setFloatingText("Usuario");

        // El texto flotante sobre los MFXPasswordField
        passwordField.setFloatingText("Contraseña");
    }

    @FXML
    private void handleLogin() { //método en el que compararemos los campos que hay en los Field con el

        String username = usernameField.getText();
        String password = passwordField.getText();

        if ("admin".equals(username) && "admin".equals(password)) {
            System.out.println("Inicio de sesion correcto");

            // Aquí se cargará la vista principal

        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Credenciales incorrectas");
            alert.showAndWait();
        }
    }
}