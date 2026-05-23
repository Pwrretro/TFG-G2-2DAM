package com.grupo2_2dam.tpv_software.controladores;

import com.grupo2_2dam.tpv_software.util.Alertas;
import com.grupo2_2dam.tpv_software.util.basededatos.crud.ConsultasUpdate;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class ModificarProductoControlador {
    @FXML private Label lblTitulo;
    @FXML private MFXTextField txtNombre;
    @FXML private MFXTextField txtPrecio;
    @FXML private MFXButton btnCancelar;

    private String nombreOriginal;
    private int idCategoria;
    private VistaPrincipalControlador controladorPrincipal;
    private ConsultasUpdate consultasUpdate = new ConsultasUpdate();
    private Alertas alertas = new Alertas();

    public void inicializarDatos(String nombreOriginal, double precioOriginal, int idCategoria, VistaPrincipalControlador controladorPrincipal) {
        this.nombreOriginal = nombreOriginal;
        this.idCategoria = idCategoria;
        this.controladorPrincipal = controladorPrincipal;

        lblTitulo.setText("Modificar: " + nombreOriginal);
        txtNombre.setText(nombreOriginal); // Valores pre-cargados
        txtPrecio.setText(String.valueOf(precioOriginal));
    }

    @FXML
    private void guardarCambios() {
        String nuevoNombre = txtNombre.getText().trim();
        String precioStr = txtPrecio.getText().trim().replace(",", ".");

        if (nuevoNombre.isEmpty() || precioStr.isEmpty()) {
            alertas.mostrarAlerta("Error", "Los campos no pueden estar vacíos.");
            return;
        }

        try {
            double nuevoPrecio = Double.parseDouble(precioStr);
            boolean actualizado = consultasUpdate.actualizarProducto(nombreOriginal, idCategoria, nuevoNombre, nuevoPrecio);

            if (actualizado) {
                controladorPrincipal.cargarProductos();
                alertas.mostrarAlerta("Éxito", "Producto actualizado.");
                cerrarVentana();
            } else {
                alertas.mostrarAlerta("Error de BD", "No se pudo actualizar el producto.");
            }
        } catch (NumberFormatException e) {
            alertas.mostrarAlerta("Error", "Introduce un precio numérico válido.");
        }
    }

    @FXML
    private void cerrarVentana() {
        Stage stage = (Stage) btnCancelar.getScene().getWindow();
        stage.close();
    }
}