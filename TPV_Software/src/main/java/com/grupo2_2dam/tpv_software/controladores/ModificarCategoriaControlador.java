package com.grupo2_2dam.tpv_software.controladores;

import com.grupo2_2dam.tpv_software.util.Alertas;
import com.grupo2_2dam.tpv_software.util.basededatos.crud.ConsultasUpdate;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class ModificarCategoriaControlador {
    @FXML private Label lblTitulo;
    @FXML private MFXTextField txtNombre;
    @FXML private MFXButton btnCancelar;

    private String nombreOriginal;
    private VistaPrincipalControlador controladorPrincipal;
    private ConsultasUpdate consultasUpdate = new ConsultasUpdate();
    private Alertas alertas = new Alertas();

    public void inicializarDatos(String nombreOriginal, VistaPrincipalControlador controladorPrincipal) {
        this.nombreOriginal = nombreOriginal;
        this.controladorPrincipal = controladorPrincipal;
        lblTitulo.setText("Modificar: " + nombreOriginal);
        txtNombre.setText(nombreOriginal); // Cargar el nombre actual
    }

    @FXML
    private void guardarCambios() {
        String nuevoNombre = txtNombre.getText().trim();
        if (nuevoNombre.isEmpty()) {
            alertas.mostrarAlerta("Error", "El nombre no puede estar vacío.");
            return;
        }

        boolean actualizado = consultasUpdate.actualizarCategoria(nombreOriginal, nuevoNombre);
        if (actualizado) {
            controladorPrincipal.cargarCategorias();
            alertas.mostrarAlerta("Éxito", "Categoría actualizada.");
            cerrarVentana();
        } else {
            alertas.mostrarAlerta("Error de BD", "No se pudo actualizar la categoría.");
        }
    }

    @FXML
    private void cerrarVentana() {
        Stage stage = (Stage) btnCancelar.getScene().getWindow();
        stage.close();
    }
}