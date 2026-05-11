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
import java.util.UUID;

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

    //Botones laterales, se inicializan para cambiar texto y funcionalidad categorías/productos
    @FXML private MFXButton btnAnadir;
    @FXML private MFXButton btnModificar;
    @FXML private MFXButton btnEliminar;

    //Gestión de paneles, variables de estado
    private boolean panelCategorias = true;
    private int currentCategId = -1;
    private String currentCategNombre = "";

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

        cargarCategorias();

/*
        //Productos -----------------------
        String categoria = "/imagenes/frutas.jpg";
        foto_categoria.setImage(procesarImagen(categoria));

        String mango = "/imagenes/mango.jpg";
        foto_mango.setImage(procesarImagen(mango));
        //Productos -----------------------
*/

        //Categorías ----------------------
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


    //------------------------------------CATEGORÍA-------------------------------------
    @FXML
    private void handleAdd() {
        if (panelCategorias) addCategoria();
        else addProducto();
    }

    @FXML
    private void handleModificar() {
        if (panelCategorias) modificarCategoria();
        else modificarProducto();
    }

    @FXML
    private void handleEliminar() {
        if (panelCategorias) eliminarCategoria();
        else eliminarProducto();
    }

    //------------------------------------CATEGORÍA-------------------------------------
    private void addCategoria() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Nueva Categoría");
        dialog.setHeaderText("Añadir una nueva categoría");
        dialog.setContentText("Nombre:");

        dialog.showAndWait().ifPresent(nombre -> {
            if (nombre.trim().isEmpty()) {
                mostrarAlerta("Error", "El nombre NO puede estar vacío.");
                return;
            }
            if (categoriaExiste(nombre)) {
                mostrarAlerta("Error", "La categoría ya existe.");
                return;
            }

            String sql = "INSERT INTO CATEGORIAS (COD_CATEGORIA, NOMBRE_CATEGORIA) VALUES ((SELECT COALESCE(MAX(COD_CATEGORIA),0)+1 FROM CATEGORIAS), ?)";
            try (Connection conn = com.grupo2_2dam.tpv_software.util.ConexionDB.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, nombre);
                pstmt.executeUpdate();
                cargarCategorias();
            } catch (SQLException e) {
                mostrarAlerta("Error de BD", "No se pudo añadir.");
            }
        });
    }

    private void modificarCategoria() {
        TextInputDialog idDialog = new TextInputDialog();
        idDialog.setTitle("Modificar Categoría");
        idDialog.setHeaderText("Introduce el nombre actual de la categoría");

        idDialog.showAndWait().ifPresent(nombreOriginal -> {
            if (!categoriaExiste(nombreOriginal)) {
                mostrarAlerta("Error", "No existe esa categoría.");
                return;
            }
            TextInputDialog nameDialog = new TextInputDialog();
            nameDialog.setTitle("Nuevo Nombre");
            nameDialog.setContentText("Nuevo nombre para " + nombreOriginal + ":");

            nameDialog.showAndWait().ifPresent(nuevoNombre -> {
                String sql = "UPDATE CATEGORIAS SET NOMBRE_CATEGORIA = ? WHERE UPPER(NOMBRE_CATEGORIA) = UPPER(?)";
                try (Connection conn = com.grupo2_2dam.tpv_software.util.ConexionDB.getConnection();
                     PreparedStatement pstmt = conn.prepareStatement(sql)) {
                    pstmt.setString(1, nuevoNombre.trim());
                    pstmt.setString(2, nombreOriginal.trim());
                    pstmt.executeUpdate();
                    cargarCategorias();
                } catch (SQLException e) {
                    mostrarAlerta("Error de BD", "No se pudo actualizar.");
                }
            });
        });
    }

    private void eliminarCategoria() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Eliminar Categoría");
        dialog.setHeaderText("¡ATENCIÓN! Se borrarán también todos los productos asociados.\nIntroduce el nombre de la categoría:");

        dialog.showAndWait().ifPresent(nombre -> {
            if (!categoriaExiste(nombre)) {
                mostrarAlerta("Error", "No existe esa categoría.");
                return;
            }

            // Borramos en cascada, si borramos categoría, se eliminan sus productos también
            String sqlBorrarProductos = "DELETE FROM PRODUCTOS WHERE COD_CATEGORIA = (SELECT COD_CATEGORIA FROM CATEGORIAS WHERE UPPER(NOMBRE_CATEGORIA) = UPPER(?))";
            String sqlBorrarCat = "DELETE FROM CATEGORIAS WHERE UPPER(NOMBRE_CATEGORIA) = UPPER(?)";

            try (Connection conn = com.grupo2_2dam.tpv_software.util.ConexionDB.getConnection()) {
                conn.setAutoCommit(false); //Eliminamoas manualmente, finaliza con el commit de abajo

                try (PreparedStatement pstmtProd = conn.prepareStatement(sqlBorrarProductos);
                     PreparedStatement pstmtCat = conn.prepareStatement(sqlBorrarCat)) {

                    pstmtProd.setString(1, nombre.trim());
                    pstmtProd.executeUpdate();

                    pstmtCat.setString(1, nombre.trim());
                    pstmtCat.executeUpdate();

                    conn.commit();
                    cargarCategorias();
                } catch (SQLException ex) {
                    conn.rollback(); //Si algo falla revertimos
                    throw ex;
                }
            } catch (SQLException e) {
                mostrarAlerta("Error", "No se pudo borrar. Es posible que los productos de esta categoría estén asociados a un registro de ventas o movimientos.");
            }
        });
    }
    //------------------------------------CATEGORÍA-------------------------------------
    //------------------------------------PRODUCTOS-------------------------------------

    private void addProducto() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Nuevo Producto");
        dialog.setHeaderText("Añadir producto a la categoría: " + currentCategNombre);
        dialog.setContentText("Nombre del producto:");

        dialog.showAndWait().ifPresent(nombre -> {
            if (nombre.trim().isEmpty()) {
                mostrarAlerta("Error", "El nombre NO puede estar vacío.");
                return;
            }
            if (productoExiste(nombre, currentCategId)) {
                mostrarAlerta("Error", "El producto ya existe en esta categoría.");
                return;
            }

            //Generamos un id para el producto creado
            String codProducto = "PROD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

            String sql = "INSERT INTO PRODUCTOS (COD_PRODUCTO, NOMBRE_PRODUCTO, COD_CATEGORIA) VALUES (?, ?, ?)";
            try (Connection conn = com.grupo2_2dam.tpv_software.util.ConexionDB.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, codProducto);
                pstmt.setString(2, nombre);
                pstmt.setInt(3, currentCategId);
                pstmt.executeUpdate();
                cargarProductos();
            } catch (SQLException e) {
                e.printStackTrace();
                mostrarAlerta("Error en la Base de Datos", "No se pudo añadir el producto.");
            }
        });
    }

    private void modificarProducto() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Modificar Producto");
        dialog.setHeaderText("Introduce el nombre actual del producto a modificar:");

        dialog.showAndWait().ifPresent(nombreOriginal -> {
            if (!productoExiste(nombreOriginal, currentCategId)) {
                mostrarAlerta("Error", "No existe ese producto en esta categoría.");
                return;
            }
            TextInputDialog nameDialog = new TextInputDialog();
            nameDialog.setTitle("Nuevo Nombre");
            nameDialog.setContentText("Nuevo nombre para " + nombreOriginal + ":");

            nameDialog.showAndWait().ifPresent(nuevoNombre -> {
                String sql = "UPDATE PRODUCTOS SET NOMBRE_PRODUCTO = ? WHERE UPPER(NOMBRE_PRODUCTO) = UPPER(?) AND COD_CATEGORIA = ?";
                try (Connection conn = com.grupo2_2dam.tpv_software.util.ConexionDB.getConnection();
                     PreparedStatement pstmt = conn.prepareStatement(sql)) {
                    pstmt.setString(1, nuevoNombre.trim());
                    pstmt.setString(2, nombreOriginal.trim());
                    pstmt.setInt(3, currentCategId);
                    pstmt.executeUpdate();
                    cargarProductos();
                } catch (SQLException e) {
                    mostrarAlerta("Error en la Base de Datos", "No se pudo actualizar.");
                }
            });
        });
    }

    private void eliminarProducto() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Eliminar Producto");
        dialog.setHeaderText("Introduce el nombre del producto a eliminar:");

        dialog.showAndWait().ifPresent(nombre -> {
            if (!productoExiste(nombre, currentCategId)) {
                mostrarAlerta("Error", "No existe ese producto en esta categoría.");
                return;
            }

            String sql = "DELETE FROM PRODUCTOS WHERE UPPER(NOMBRE_PRODUCTO) = UPPER(?) AND COD_CATEGORIA = ?";
            try (Connection conn = com.grupo2_2dam.tpv_software.util.ConexionDB.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, nombre.trim());
                pstmt.setInt(2, currentCategId);
                pstmt.executeUpdate();
                cargarProductos();
            } catch (SQLException e) {
                mostrarAlerta("Error", "No se puede eliminar el producto porque tiene ventas, movimientos o líneas de venta asociadas.");
            }
        });
    }
    //------------------------------------PRODUCTOS-------------------------------------

    private boolean categoriaExiste(String nombreCategoria) {
        String sql = "SELECT COUNT(*) FROM CATEGORIAS WHERE UPPER(NOMBRE_CATEGORIA) = UPPER(?)";

        try (java.sql.Connection conn = com.grupo2_2dam.tpv_software.util.ConexionDB.getConnection();
             java.sql.PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, nombreCategoria.trim());

            try (java.sql.ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0; //Devuelve true si es mayor a 0
                }
            }
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
            mostrarAlerta("Error de Conexión", "Fallo al verificar la existencia de la categoría.");
        }
        return false;
    }

    private boolean productoExiste(String nombreProducto, int codCategoria) {
        String sql = "SELECT COUNT(*) FROM PRODUCTOS WHERE UPPER(NOMBRE_PRODUCTO) = UPPER(?) AND COD_CATEGORIA = ?";
        try (Connection conn = com.grupo2_2dam.tpv_software.util.ConexionDB.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, nombreProducto.trim());
            pstmt.setInt(2, codCategoria);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
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
                int id = rs.getInt("COD_CATEGORIA");

                //Creamos el de la categoría nuevoa de manera dinámica
                MFXButton btnCategoria = new MFXButton(nombre);

                btnCategoria.getStyleClass().add("mfx-button-categoria");
                btnCategoria.setPrefSize(150, 120);

                btnCategoria.setOnAction(e -> cambiarModoProductos(id, nombre));

                //añadimos categoría nueva
                flowProductos.getChildren().add(btnCategoria);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            mostrarAlerta("Error de carga", "No se pudieron recuperar las categorías de la base de datos.");
        }
    }

    private void cargarProductos() {
        flowProductos.getChildren().clear();

        //Volver a las categorías
        MFXButton btnVolver = new MFXButton("⬅ Volver");
        btnVolver.getStyleClass().add("mfx-button-categoria");
        btnVolver.setPrefSize(150, 120);
        btnVolver.setOnAction(e -> cambiarModoCategorias());
        flowProductos.getChildren().add(btnVolver);

        String sql = "SELECT * FROM PRODUCTOS WHERE COD_CATEGORIA = ? ORDER BY NOMBRE_PRODUCTO ASC";

        try (Connection conn = com.grupo2_2dam.tpv_software.util.ConexionDB.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, currentCategId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                String nombre = rs.getString("NOMBRE_PRODUCTO");

                MFXButton btnProducto = new MFXButton(nombre);
                btnProducto.getStyleClass().add("mfx-button-producto");
                btnProducto.setPrefSize(150, 120);

                // Añadir lógica para agregar el producto al carrito
                // btnProducto.setOnAction(e -> agregarAlTicket(nombre));

                flowProductos.getChildren().add(btnProducto);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            mostrarAlerta("Error de carga", "No se pudieron recuperar los productos.");
        }
    }

    private void cambiarModoCategorias() {
        panelCategorias = true;
        currentCategId = -1;
        currentCategNombre = "";

        //Cambiamos los textos de los botones laterales CATEGORÍA
        if (btnAnadir != null) btnAnadir.setText("Añadir Categoría");
        if (btnModificar != null) btnModificar.setText("Modificar Categoría");
        if (btnEliminar != null) btnEliminar.setText("Eliminar Categoría");

        cargarCategorias();
    }

    private void cambiarModoProductos(int codCategoria, String nombreCategoria) {
        panelCategorias = false;
        currentCategId = codCategoria;
        currentCategNombre = nombreCategoria;


        if (btnAnadir != null) btnAnadir.setText("Añadir Producto");
        if (btnModificar != null) btnModificar.setText("Modificar Producto");
        if (btnEliminar != null) btnEliminar.setText("Eliminar Producto");

        cargarProductos();
    }
}