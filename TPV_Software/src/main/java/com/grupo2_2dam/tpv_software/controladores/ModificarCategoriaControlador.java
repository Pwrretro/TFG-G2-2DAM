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

public class ModificarCategoriaControlador {

    @FXML private Label lblTitulo;
    @FXML private MFXTextField txtNombre;
    @FXML private MFXButton btnCancelar;
    @FXML private MFXButton btnGuardar;
    @FXML private MFXButton btnSeleccionarImagen;
    @FXML private ImageView imgVistaPrevia;

    private String nombreOriginal;
    private String imagenRutaActual;
    private VistaPrincipalControlador controladorPrincipal;
    private File nuevaImagenSeleccionada;

    private ConsultasUpdate consultasUpdate = new ConsultasUpdate();
    private Alertas alertas = new Alertas();

    public void inicializarDatos(String nombreOriginal, String imagenRutaActual, VistaPrincipalControlador controladorPrincipal) {
        this.nombreOriginal = nombreOriginal;
        this.imagenRutaActual = imagenRutaActual;
        this.controladorPrincipal = controladorPrincipal;
        lblTitulo.setText("Modificar: " + nombreOriginal);
        txtNombre.setText(nombreOriginal);
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
        if (nuevoNombre.isEmpty()) {
            alertas.mostrarAlerta("Error", "El nombre no puede estar vacío.");
            return;
        }
        String nuevaImagenRuta = imagenRutaActual;
        if (nuevaImagenSeleccionada != null) {
            String nombreBase = "categoria_" + nuevoNombre.replaceAll("\\s+", "_");
            String ruta = GestorImagenes.guardarImagen(nuevaImagenSeleccionada, nombreBase, 150, 150);
            if (ruta != null) nuevaImagenRuta = ruta;
            else alertas.mostrarAlerta("Advertencia", "No se pudo guardar la nueva imagen, se mantiene la anterior.");
        }
        boolean actualizado = consultasUpdate.actualizarCategoria(nombreOriginal, nuevoNombre, nuevaImagenRuta);
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