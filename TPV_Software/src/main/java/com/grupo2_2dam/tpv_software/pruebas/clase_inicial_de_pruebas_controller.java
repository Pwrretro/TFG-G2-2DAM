package com.grupo2_2dam.tpv_software.pruebas;

import io.github.palexdev.materialfx.controls.*;
import io.github.palexdev.materialfx.controls.legacy.MFXLegacyTableView;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class clase_inicial_de_pruebas_controller {

    @FXML private Label welcomeText;
    @FXML private MFXButton actionButton;
    @FXML private MFXTextField textField;
    @FXML private MFXListView<String> demoListView;
    @FXML private MFXSlider demoSlider;
    @FXML private MFXProgressSpinner demoSpinner;
    @FXML private MFXProgressBar demoProgressBar;
    @FXML private MFXDatePicker demoDatePicker;
    @FXML private MFXLegacyTableView<String> demoTableView;

    @FXML
    public void initialize() {
        demoTableView.getItems().addAll("Fila 1", "Fila 2", "Fila 3");
        demoSlider.valueProperty().addListener((obs, old, val) -> {
            double progress = val.doubleValue() / 100.0;
            demoProgressBar.setProgress(progress);
            demoSpinner.setProgress(progress);
        });
        demoListView.getItems().addAll("Elemento 4", "Elemento 5");
    }

    @FXML
    private void handleActionButton() {
        String text = textField.getText();
        if (text == null || text.trim().isEmpty()) {
            welcomeText.setText("❌ El campo de texto está vacío");
        } else {
            welcomeText.setText("✓ Has escrito: " + text);
        }
    }

    @FXML
    private void handleShowDialog() {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.INFORMATION);
        alert.setTitle("Información");
        alert.setHeaderText("Diálogo con MaterialFX");
        alert.setContentText("Esta es una ventana de diálogo estándar.\nPuedes reemplazarla por MFXGenericDialog cuando investigues su API actual.");
        alert.showAndWait();
    }

    @FXML
    private void handleShowNotification() {
        welcomeText.setText("🔔 Notificación: Esta es una simulación. Consulta la documentación de MFXNotificator.");
    }
}