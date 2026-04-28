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
        /* REEMPLAZO CONEXIÓN A DB
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
         */
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            mostrarAlerta("Error", "Los campos no pueden estar vacíos");
            return;
        }

        // Consulta SQL para buscar al usuario
        String sql = "SELECT * FROM USUARIOS WHERE NOMBRE_USUARIO = ? AND CONTRASEÑA_USUARIO = ?";

        // Usamos try-with-resources para que la conexión se cierre sola al terminar
        try (java.sql.Connection conn = com.grupo2_2dam.tpv_software.util.ConexionDB.getConnection();
             java.sql.PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            pstmt.setString(2, password);

            java.sql.ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                System.out.println("¡Inicio de sesión correcto! Bienvenido " + rs.getString("USERNAME"));
                // MOSTRAMOS VISTA PRINCIPAL

            } else {
                mostrarAlerta("Error al iniciar sesión", "Usuario o contraseña incorrectos");
            }

        } catch (java.sql.SQLException e) {
            e.printStackTrace();
            mostrarAlerta("Error de BD", "No se pudo conectar a la base de datos.");
        }
    }
    private void mostrarAlerta(String titulo, String contenido) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(contenido);
        alert.showAndWait();
    }
}