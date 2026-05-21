package com.grupo2_2dam.tpv_software.util;

import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class Alertas extends RuntimeException {

    /**
     * Mostrar una alerta de información con un título, un mensaje y un icono personalizado
     * @param titulo El título de la alerta
     * @param mensaje El mensaje de la alerta
     */
    public void mostrarAlerta(String titulo, String mensaje) {
        Alert alerta = new Alert(Alert.AlertType.INFORMATION);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);

        try {
            // Obtener la ventana (Stage) interna de la alerta y añadir el icono
            Stage stage = (Stage) alerta.getDialogPane().getScene().getWindow();
            stage.getIcons().add(new Image(getClass().getResourceAsStream("/imagenes/icon_tpv.png")));
        } catch (Exception e) {
            System.err.println("Error al cargar el icono: " + e.getMessage());
        }

        alerta.showAndWait();
    }
}
