package com.grupo2_2dam.tpv_software.controladores;

import com.grupo2_2dam.tpv_software.util.Alertas;
import com.grupo2_2dam.tpv_software.util.basededatos.crud.ConsultasUpdate;
import com.grupo2_2dam.tpv_software.util.tratadodeimagenes.GestorImagenes;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.io.File;

public class ModificarProductoControlador {
    @FXML private Label lblTitulo;
    @FXML private MFXTextField txtNombre;
    @FXML private MFXTextField txtPrecio;
    @FXML private MFXButton btnCancelar;
    @FXML private MFXButton btnSeleccionarImagen;
    @FXML private ImageView imgVistaPrevia;

    private String nombreOriginal;
    private double precioOriginal;
    private int idCategoria;
    private String imagenRutaActual;
    private VistaPrincipalControlador controladorPrincipal;
    private File nuevaImagenSeleccionada;

    private ConsultasUpdate consultasUpdate = new ConsultasUpdate();
    private Alertas alertas = new Alertas();

    public void inicializarDatos(String nombreOriginal, double precioOriginal, int idCategoria, String imagenRutaActual, VistaPrincipalControlador controladorPrincipal) {
        this.nombreOriginal = nombreOriginal;
        this.precioOriginal = precioOriginal;
        this.idCategoria = idCategoria;
        this.imagenRutaActual = imagenRutaActual;
        this.controladorPrincipal = controladorPrincipal;

        lblTitulo.setText("Modificar: " + nombreOriginal);
        txtNombre.setText(nombreOriginal);
        txtPrecio.setText(String.valueOf(precioOriginal));

        if (imagenRutaActual != null && !imagenRutaActual.isEmpty()) {
            String rutaCompleta = System.getProperty("user.home") + "/.tpv_software/" + imagenRutaActual;
            Image img = new Image("file:" + rutaCompleta, 60, 60, true, true);
            imgVistaPrevia.setImage(img);
        }
    }

    @FXML
    private void seleccionarImagen() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Imágenes", "*.png", "*.jpg", "*.jpeg"));
        File file = fileChooser.showOpenDialog(btnSeleccionarImagen.getScene().getWindow());
        if (file != null) {
            nuevaImagenSeleccionada = file;
            Image preview = new Image(file.toURI().toString(), 60, 60, true, true);
            imgVistaPrevia.setImage(preview);
        }
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
            String nuevaImagenRuta = imagenRutaActual;
            if (nuevaImagenSeleccionada != null) {
                String nombreBase = "producto_" + nuevoNombre.replaceAll("\\s+", "_");
                String ruta = GestorImagenes.guardarImagen(nuevaImagenSeleccionada, nombreBase, 150, 150);
                if (ruta != null) nuevaImagenRuta = ruta;
                else alertas.mostrarAlerta("Advertencia", "No se pudo guardar la nueva imagen, se mantiene la anterior.");
            }

            boolean actualizado = consultasUpdate.actualizarProducto(nombreOriginal, idCategoria, nuevoNombre, nuevoPrecio, nuevaImagenRuta);
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