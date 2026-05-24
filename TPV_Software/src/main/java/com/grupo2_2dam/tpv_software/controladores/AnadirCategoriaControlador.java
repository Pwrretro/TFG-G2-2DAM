package com.grupo2_2dam.tpv_software.controladores;

import com.grupo2_2dam.tpv_software.util.Alertas;
import com.grupo2_2dam.tpv_software.util.basededatos.crud.ConsultasCreate;
import com.grupo2_2dam.tpv_software.util.basededatos.crud.ConsultasRead;
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

public class AnadirCategoriaControlador {

    @FXML private Label lblTitulo;
    @FXML private MFXTextField txtNombre;
    @FXML private MFXButton btnGuardar;
    @FXML private MFXButton btnCancelar;
    @FXML private MFXButton btnImagen;
    @FXML private ImageView imgVistaPrevia;

    private VistaPrincipalControlador controladorPrincipal;
    private File imagenSeleccionada;

    private ConsultasCreate consultasCreate = new ConsultasCreate();
    private ConsultasRead consultasRead = new ConsultasRead();
    private Alertas alertas = new Alertas();

    public void inicializarDatos(VistaPrincipalControlador controladorPrincipal) {
        this.controladorPrincipal = controladorPrincipal;
    }

    @FXML
    private void guardarCategoria() {
        String nombre = txtNombre.getText().trim();
        if (nombre.isEmpty()) {
            alertas.mostrarAlerta("Error", "El nombre de la categoría no puede estar vacío.");
            return;
        }
        if (consultasRead.existeCategoria(nombre)) {
            alertas.mostrarAlerta("Error", "La categoría ya existe.");
            return;
        }
        String imagenRuta = null;
        if (imagenSeleccionada != null) {
            String nombreBase = "categoria_" + nombre.replaceAll("\\s+", "_");
            imagenRuta = GestorImagenes.guardarImagen(imagenSeleccionada, nombreBase, 150, 150);
            if (imagenRuta == null) {
                alertas.mostrarAlerta("Advertencia", "No se pudo guardar la imagen. La categoría se creará sin imagen.");
            }
        }
        boolean creado = consultasCreate.crearCategoria(nombre, imagenRuta);
        if (creado) {
            controladorPrincipal.cargarCategorias();
            cerrarVentana();
            alertas.mostrarAlerta("Éxito", "Categoría añadida correctamente.");
        } else {
            alertas.mostrarAlerta("Error en la Base de Datos", "No se pudo añadir la categoría.");
        }
    }

    @FXML
    private void seleccionarImagen() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Imágenes", "*.png", "*.jpg", "*.jpeg"));
        File file = fileChooser.showOpenDialog(btnImagen.getScene().getWindow());
        if (file != null) {
            imagenSeleccionada = file;
            Image preview = new Image(file.toURI().toString(), 60, 60, true, true);
            imgVistaPrevia.setImage(preview);
        }
    }

    @FXML
    private void cerrarVentana() {
        Stage stage = (Stage) btnCancelar.getScene().getWindow();
        stage.close();
    }
}