package com.grupo2_2dam.tpv_software.controladores;

import com.grupo2_2dam.tpv_software.util.CambiarVistas;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Rectangle2D;
import javafx.scene.control.Label;

//Liberías que se utilizaran, no tocar que luego se me olvidan xd
import io.github.palexdev.materialfx.controls.MFXButton;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.*;
import java.util.Optional;

public class VistaPrincipalControlador {

    @FXML
    private MFXButton regresarButton;

    @FXML
    private Label nombre_Usuario;

    @FXML
    private ImageView foto_de_perfil; //Imagen de perfil de usuario

    @FXML
    private ImageView foto_categoria;

    @FXML
    private ImageView foto_mango;

    @FXML
    private FlowPane flowProductos;

    /*
    Definimos usuario
    public void setUsuario(String nombreUsuario) {

    }
    */

    @FXML
    public void initialize() { // Aquí se cargaran los productos e imagenes de la base de datos

        //Perfil ---------------------------
        nombre_Usuario.setText("Pawer"); //15 carácteres máximos para evitar errores

        String imagenurl = "/imagenes/kanna.png"; // Esto hay que cambiarlo por la base de datos
        foto_de_perfil.setImage(procesarImagen(imagenurl));
        //Perfil ---------------------------

/*
        //Productos -----------------------
        String categoria = "/imagenes/frutas.jpg";
        foto_categoria.setImage(procesarImagen(categoria));

        String mango = "/imagenes/mango.jpg";
        foto_mango.setImage(procesarImagen(mango));
        //Productos -----------------------
*/

        //Categorías ----------------------
        cargarCategorias();
    }

    public void cerrar_sesion(ActionEvent actionEvent) throws IOException {
        Stage stage = (Stage) regresarButton.getScene().getWindow();
        String vistaPrincipal = "/com/grupo2_2dam/tpv_software/vistas/inicio_de_sesion.fxml";
        CambiarVistas.cambiarVista(vistaPrincipal, stage); // Cambiar de vista
    }

    /**
     * @param imagenurl se recibe la url de la imagen
     * @return regresa la imagen ya procesada
     */
    private Image procesarImagen(String imagenurl) {
        // Cargar la imagen original (sin redimensionar aún)
        Image imagenOriginal = new Image(getClass().getResourceAsStream(imagenurl), 90,90,true,true);

        // Obtener dimensiones originales
        double ancho = imagenOriginal.getWidth();
        double alto = imagenOriginal.getHeight();

        // Calcular el cuadrado central más grande posible (min(ancho, alto))
        double lado = Math.min(ancho, alto);
        double x = (ancho - lado) / 2;
        double y = (alto - lado) / 2;

        // Aplicar viewport para recortar el cuadrado central
        foto_de_perfil.setViewport(new Rectangle2D(x, y, lado, lado));

        // Ajustar el ImageView: ya no preservamos la proporción porque el viewport ya da un cuadrado
        foto_de_perfil.setPreserveRatio(false);
        foto_de_perfil.setFitWidth(90);
        foto_de_perfil.setFitHeight(90);
        foto_de_perfil.setSmooth(true); // calidad de escalado

        return imagenOriginal;
    }

    @FXML
    private void handleAddCategoria() {
        //Abre un cuadro de texto para añadir una nueva categoría
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Nueva Categoría");
        dialog.setHeaderText("Añadir una nueva categoría de productos");
        dialog.setContentText("Nombre:");

        //Solo hace la select si se ha escrito algo en el TextInputDialog
        Optional<String> result = dialog.showAndWait();
        //Se almacena el nombre de la categoría en el String "nombre"
        result.ifPresent(nombre -> {
            //Comprobamos que se haya introducido un nombre y que no esté ya creado
            if (nombre.trim().isEmpty()) {
                mostrarAlerta("Error de validación", "El nombre de la categoría NO puede estar vacío.");
                handleAddCategoria(); // Volvemos a pedir el nombre
                return;
            }
            if (categoriaExiste(nombre)) {
                mostrarAlerta("Registro duplicado", "La categoría '" + nombre + "' ya existe en el sistema. Por favor, introduzca un nombre distinto.");
                handleAddCategoria(); // Volvemos a pedir el nombre
                return;
            }

            //añadimos la categoría, el id será 1 más que la última
            String sql = "INSERT INTO CATEGORIAS (COD_CATEGORIA, NOMBRE_CATEGORIA) VALUES ((SELECT COALESCE(MAX(COD_CATEGORIA),0)+1 FROM CATEGORIAS), ?)";

            try (Connection conn = com.grupo2_2dam.tpv_software.util.ConexionDB.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {

                pstmt.setString(1, nombre);
                pstmt.executeUpdate();

                System.out.println("Categoría " + nombre.toUpperCase() + " añadida");

            } catch (SQLException e) {
                e.printStackTrace();
                mostrarAlerta("Error de BD", "No se pudo añadir la categoría.");
            }
            //refrescamos la vista del panel
            cargarCategorias();
        });
    }
    private boolean categoriaExiste(String nombreCategoria) {
        String sql = "SELECT COUNT(*) FROM CATEGORIAS WHERE UPPER(NOMBRE_CATEGORIA) = UPPER(?)";

        try (java.sql.Connection conn = com.grupo2_2dam.tpv_software.util.ConexionDB.getConnection();
             java.sql.PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, nombreCategoria.trim());

            try (java.sql.ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0; // Devuelve true si es mayor a 0
                }
            }
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
            mostrarAlerta("Error de Conexión", "Fallo al verificar la existencia de la categoría.");
        }
        return false;
    }

    @FXML
    private void handleModificarCategoria() {
        TextInputDialog idDialog = new TextInputDialog();
        idDialog.setTitle("Modificar Categoría");
        idDialog.setHeaderText("Introduce el nombre de la categoría a modificar");

        idDialog.showAndWait().ifPresent(nombreOriginal -> {
            // checkeamos que exista
            if (!categoriaExiste(nombreOriginal)) {
                mostrarAlerta("Error de búsqueda", "No existe una categoría llamada " + nombreOriginal);
                handleModificarCategoria();
                return;
            }

            TextInputDialog nameDialog = new TextInputDialog();
            nameDialog.setTitle("Nuevo Nombre");
            nameDialog.setContentText("Nuevo nombre para la categoría '" + nombreOriginal + "':");

            nameDialog.showAndWait().ifPresent(nuevoNombre -> {
                if (nuevoNombre.trim().isEmpty()) {
                    mostrarAlerta("Error de validación", "El nombre de la categoría NO puede estar vacío.");
                    handleModificarCategoria();
                    return;
                }
                if (categoriaExiste(nuevoNombre)) {
                    mostrarAlerta("Registro duplicado", "La categoría '" + nuevoNombre + "' ya existe en el sistema.");
                    handleModificarCategoria();
                    return;
                }

                String sql = "UPDATE CATEGORIAS SET NOMBRE_CATEGORIA = ? WHERE UPPER(NOMBRE_CATEGORIA) = UPPER(?)";
                try (Connection conn = com.grupo2_2dam.tpv_software.util.ConexionDB.getConnection();
                     PreparedStatement pstmt = conn.prepareStatement(sql)) {

                    pstmt.setString(1, nuevoNombre.trim());
                    pstmt.setString(2, nombreOriginal.trim());
                    pstmt.executeUpdate();

                    System.out.println("Categoría actualizada exitosamente.");
                    cargarCategorias(); // Refrescamos el panel central

                } catch (SQLException e) {
                    e.printStackTrace();
                    mostrarAlerta("Error de BD", "No se pudo actualizar la categoría.");
                }
            });
        });
    }

    @FXML
    private void handleEliminarCategoria() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Eliminar Categoría");
        dialog.setHeaderText("Introduce el nombre de la categoría a eliminar");

        //almacenamos el nombre recibido en "nombre"
        dialog.showAndWait().ifPresent(nombre -> {
            // checkeamos que exista
            if (!categoriaExiste(nombre)) {
                mostrarAlerta("Error de búsqueda", "No existe una categoría llamada " + nombre);
                handleEliminarCategoria();
                return;
            }

            String sql = "DELETE FROM CATEGORIAS WHERE UPPER(NOMBRE_CATEGORIA) = UPPER(?)";
            try (Connection conn = com.grupo2_2dam.tpv_software.util.ConexionDB.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {

                pstmt.setString(1, nombre.trim());
                int affected = pstmt.executeUpdate();

                if (affected == 0) {
                    mostrarAlerta("Error", "No existe ninguna categoría con ese nombre.");
                } else {
                    System.out.println("Categoría '" + nombre + "' eliminada correctamente.");
                    cargarCategorias(); // Refrescamos el panel central
                }

            } catch (SQLException e) {
                mostrarAlerta("Error de integridad", "No puedes borrar una categoría que tiene productos asociados.");
            }
        });
    }

    //Mensaje informativo auxiliar
    private void mostrarAlerta(String titulo, String contenido) {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(contenido);
        alert.showAndWait();
    }

    @FXML
    private void cargarCategorias() {
        flowProductos.getChildren().clear();

        String sql = "SELECT * FROM CATEGORIAS ORDER BY COD_CATEGORIA ASC";

        try (Connection conn = com.grupo2_2dam.tpv_software.util.ConexionDB.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                String nombre = rs.getString("NOMBRE_CATEGORIA");
                //int id = rs.getInt("COD_CATEGORIA");

                //Creamos el de la categoría nuevoa de manera dinámica
                MFXButton btnCategoria = new MFXButton(nombre);

                btnCategoria.getStyleClass().add("mfx-button-categoria");
                btnCategoria.setPrefSize(150, 120);

                //añadimos categoría nueva
                flowProductos.getChildren().add(btnCategoria);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            mostrarAlerta("Error de carga", "No se pudieron recuperar las categorías de la base de datos.");
        }
    }
}