package com.grupo2_2dam.tpv_software.controladores;

import com.grupo2_2dam.tpv_software.util.Alertas;
import com.grupo2_2dam.tpv_software.util.basededatos.crud.ConsultasCreate;
import com.grupo2_2dam.tpv_software.util.basededatos.crud.ConsultasRead;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

public class AnadirProductoControlador {

    @FXML private Label lblTitulo;
    @FXML private MFXTextField txtNombre;
    @FXML private MFXTextField txtPrecio;
    @FXML private MFXButton btnGuardar;
    @FXML private MFXButton btnCancelar;
    @FXML private MFXButton btnImagen;
    @FXML private ImageView imgVistaPrevia;

    private int idCategoria;
    private String nombreCategoria;
    private VistaPrincipalControlador controladorPrincipal;

    private ConsultasCreate consultasCreate = new ConsultasCreate();
    private ConsultasRead consultasRead = new ConsultasRead();
    private Alertas alertas = new Alertas();

    // Método para recibir los datos desde el controlador principal
    public void inicializarDatos(int idCategoria, String nombreCategoria, VistaPrincipalControlador controladorPrincipal) {
        this.idCategoria = idCategoria;
        this.nombreCategoria = nombreCategoria;
        this.controladorPrincipal = controladorPrincipal;

        lblTitulo.setText("Añadir producto para " + nombreCategoria);
    }

    @FXML
    private void guardarProducto() {
        String nombre = txtNombre.getText().trim();
        String precioStr = txtPrecio.getText().trim().replace(",", ".");

        if (nombre.isEmpty() || precioStr.isEmpty()) {
            alertas.mostrarAlerta("Error", "Los campos Nombre y Precio no pueden estar vacíos.");
            return;
        }

        //comprobamos que no está ya creado
        if (consultasRead.existeProducto(nombre, idCategoria)) {
            alertas.mostrarAlerta("Error", "El producto ya existe en esta categoría.");
            return;
        }

        try {
            double precio = Double.parseDouble(precioStr);
            boolean creado = consultasCreate.crearProducto(nombre, precio, idCategoria);

            if (creado) {
                //recargamos los productos
                controladorPrincipal.cargarProductos();
                cerrarVentana();
                alertas.mostrarAlerta("Éxito", "Producto añadido correctamente.");
            } else {
                alertas.mostrarAlerta("Error en la Base de Datos", "No se pudo añadir el producto.");
            }
        } catch (NumberFormatException e) {
            alertas.mostrarAlerta("Error", "Por favor, introduce un precio numérico válido.");
        }
    }

    @FXML
    private void seleccionarImagen() {
        //Lógica futura para abrir un 'FileChooser' y cargar la imagen
        alertas.mostrarAlerta("Información", "La subida de imágenes se implementará próximamente.");
    }

    @FXML
    private void cerrarVentana() {
        Stage stage = (Stage) btnCancelar.getScene().getWindow();
        stage.close();
    }
}