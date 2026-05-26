package com.grupo2_2dam.tpv_software.controladores;

import com.grupo2_2dam.tpv_software.objetos.Categoria;
import com.grupo2_2dam.tpv_software.objetos.DetalleTicket;
import com.grupo2_2dam.tpv_software.objetos.Producto;
import com.grupo2_2dam.tpv_software.objetos.Usuario;
import com.grupo2_2dam.tpv_software.util.Alertas;
import com.grupo2_2dam.tpv_software.util.CambiarVistas;
import com.grupo2_2dam.tpv_software.util.basededatos.FuncionUsuario;
import com.grupo2_2dam.tpv_software.util.basededatos.crud.ConsultasCreate;
import com.grupo2_2dam.tpv_software.util.basededatos.crud.ConsultasDelete;
import com.grupo2_2dam.tpv_software.util.basededatos.crud.ConsultasRead;
import com.grupo2_2dam.tpv_software.util.basededatos.crud.ConsultasUpdate;
import com.grupo2_2dam.tpv_software.util.tratadodeimagenes.GestorImagenes;
import com.grupo2_2dam.tpv_software.util.tratadodeimagenes.ProcesarImagen;
import com.grupo2_2dam.tpv_software.util.tratadodetexto.WRJSON;
import io.github.palexdev.materialfx.controls.MFXButton;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.scene.layout.Region;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static com.grupo2_2dam.tpv_software.util.basededatos.ConexionDB.obtenerConexion;

public class VistaPrincipalControlador {

    @FXML private MFXButton regresarButton;
    @FXML private Label nombre_Usuario;
    @FXML private ImageView foto_de_perfil;
    @FXML private ImageView foto_categoria;
    @FXML private ImageView foto_mango;
    @FXML private FlowPane flowProductos;
    @FXML private MFXButton btnAnadir;
    @FXML private MFXButton btnModificar;
    @FXML private MFXButton btnEliminar;
    @FXML private javafx.scene.layout.VBox ticketVBox;
    @FXML private Label subtotalLabel;
    @FXML private MFXButton btnPagar;
    @FXML private StackPane fotoContainer;

    private boolean panelCategorias = true;
    private int currentCategId = -1;
    private String currentCategNombre = "";
    private ArrayList<DetalleTicket> listaProductosTicket = new ArrayList<>();
    private double subtotalVenta = 0.0;

    private Alertas alertas = new Alertas();
    private ProcesarImagen procesarImagen = new ProcesarImagen();
    private ConsultasCreate consultasCreate = new ConsultasCreate();
    private ConsultasRead consultasRead = new ConsultasRead();
    private ConsultasUpdate consultasUpdate = new ConsultasUpdate();
    private ConsultasDelete consultasDelete = new ConsultasDelete();
    private FuncionUsuario funcionUsuario = new FuncionUsuario();

    @FXML
    public void initialize() {
        Usuario usuario = funcionUsuario.obtenerUsuarioActual();
        nombre_Usuario.setText(usuario.getNombre_usuario());

        // Cargar foto de perfil desde BD o por defecto
        if (usuario.getImagenRuta() != null && !usuario.getImagenRuta().isEmpty()) {
            String rutaCompleta = System.getProperty("user.home") + "/.tpv_software/" + usuario.getImagenRuta();
            Image img = new Image("file:" + rutaCompleta, 90, 90, true, true);
            foto_de_perfil.setImage(img);
            // Aplicar clip circular ya está en FXML
        } else {
            String imagenurl = "/imagenes/default_user_profile.png";
            foto_de_perfil.setImage(procesarImagen.procesarImagen(imagenurl, foto_de_perfil));
        }

        fotoContainer.setOnMouseClicked(event -> cambiarFotoPerfil());
        cargarCategorias();
    }

    private void cambiarFotoPerfil() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Imágenes", "*.png", "*.jpg", "*.jpeg"));
        File file = fileChooser.showOpenDialog(foto_de_perfil.getScene().getWindow());

        if (file != null) {
            Usuario usuarioActual = funcionUsuario.obtenerUsuarioActual();
            String nombreBase = "user_" + usuarioActual.getCod_usuario();
            String rutaRelativa = GestorImagenes.guardarImagen(file, nombreBase, 90, 90);

            if (rutaRelativa != null) {
                boolean actualizado = funcionUsuario.actualizarImagenUsuario(usuarioActual, rutaRelativa);

                if (actualizado) {
                    usuarioActual.setImagenRuta(rutaRelativa);
                    new WRJSON().crearJSONUsuarioActual(usuarioActual);
                    Image nuevaImg = new Image("file:" + System.getProperty("user.home") + "/.tpv_software/" + rutaRelativa, 90, 90, true, true);
                    foto_de_perfil.setImage(nuevaImg);
                    alertas.mostrarAlerta("Éxito", "Foto de perfil actualizada.");
                } else {
                    alertas.mostrarAlerta("Error", "No se pudo guardar la imagen en la base de datos.");
                }
            } else {
                alertas.mostrarAlerta("Error", "No se pudo procesar la imagen.");
            }
        }
    }

    public void cerrar_sesion(ActionEvent actionEvent) throws IOException {
        Stage stage = (Stage) regresarButton.getScene().getWindow();
        CambiarVistas.cambiarVista("/com/grupo2_2dam/tpv_software/vistas/inicio_de_sesion.fxml", stage);
    }

    @FXML private void handleAdd() {
        if (panelCategorias) addCategoria();
        else addProducto();
    }

    @FXML private void handleModificar() {
        if (panelCategorias) modificarCategoria();
        else modificarProducto();
    }

    @FXML private void handleEliminar() {
        if (panelCategorias) eliminarCategoria();
        else eliminarProducto();
    }

    private void addCategoria() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/grupo2_2dam/tpv_software/vistas/vista_anadir_categoria.fxml"));
            javafx.scene.Parent root = loader.load();
            AnadirCategoriaControlador controlador = loader.getController();
            controlador.inicializarDatos(this);

            Stage stage = new Stage();
            stage.setTitle("Añadir Categoría");
            stage.initModality(javafx.stage.Modality.APPLICATION_MODAL);
            // Añadir el icono de la aplicación
            stage.getIcons().add(new Image(getClass().getResourceAsStream("/imagenes/icon_tpv.png")));

            javafx.scene.Scene scene = new javafx.scene.Scene(root);
            scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
            stage.setScene(scene);
            stage.setResizable(false);
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
            alertas.mostrarAlerta("Error", "No se pudo cargar la vista para añadir la categoría.");
        }
    }

    private void modificarCategoria() {
        List<Categoria> categorias = consultasRead.obtenerCategorias();
        if (categorias == null || categorias.isEmpty()) return;

        List<String> nombres = new ArrayList<>();
        for (Categoria cat : categorias) nombres.add(cat.getNombre());

        ChoiceDialog<String> dialog = new ChoiceDialog<>(nombres.get(0), nombres);
        dialog.setTitle("Modificar Categoría");
        dialog.setHeaderText("Selecciona la categoría a modificar");
        dialog.setContentText("Categoría:");

        // Asignar icono personalizado al diálogo
        Stage dialogStage = (Stage) dialog.getDialogPane().getScene().getWindow();
        dialogStage.getIcons().add(new Image(getClass().getResourceAsStream("/imagenes/icon_tpv.png")));

        java.util.Optional<String> resultado = dialog.showAndWait();
        if (resultado.isPresent()) {
            String nombreOriginal = resultado.get();
            Categoria catSeleccionada = categorias.stream().filter(c -> c.getNombre().equals(nombreOriginal)).findFirst().orElse(null);
            if (catSeleccionada == null) return;
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/grupo2_2dam/tpv_software/vistas/vista_modificar_categoria.fxml"));
                javafx.scene.Parent root = loader.load();
                ModificarCategoriaControlador controlador = loader.getController();
                controlador.inicializarDatos(nombreOriginal, catSeleccionada.getImagenRuta(), this);
                Stage stage = new Stage();
                stage.setTitle("Modificar Categoría");
                stage.initModality(javafx.stage.Modality.APPLICATION_MODAL);
                stage.getIcons().add(new Image(getClass().getResourceAsStream("/imagenes/icon_tpv.png")));

                javafx.scene.Scene scene = new javafx.scene.Scene(root);
                scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
                stage.setScene(scene);

                stage.setResizable(false);
                stage.showAndWait();
            } catch (Exception e) {
                e.printStackTrace();
                alertas.mostrarAlerta("Error", "No se pudo cargar la vista.");
            }
        }
    }

    private void eliminarCategoria() {
        List<Categoria> categorias = consultasRead.obtenerCategorias();
        if (categorias == null || categorias.isEmpty()) return;

        List<String> nombres = new ArrayList<>();
        for (Categoria cat : categorias) nombres.add(cat.getNombre());

        ChoiceDialog<String> dialog = new ChoiceDialog<>(nombres.get(0), nombres);
        dialog.setTitle("Eliminar Categoría");
        dialog.setHeaderText("Selecciona la categoría a eliminar");
        dialog.setContentText("Categoría:");

        // Icono para el ChoiceDialog
        Stage dialogStage = (Stage) dialog.getDialogPane().getScene().getWindow();
        dialogStage.getIcons().add(new Image(getClass().getResourceAsStream("/imagenes/icon_tpv.png")));

        java.util.Optional<String> resultado = dialog.showAndWait();
        if (resultado.isPresent()) {
            String nombre = resultado.get();

            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
            confirm.setTitle("Confirmar Eliminación");
            confirm.setHeaderText("¡ATENCIÓN! Se borrarán también todos los productos asociados.");
            confirm.setContentText("¿Estás seguro de que deseas eliminar la categoría '" + nombre + "'?");

            // Icono para el Alert de confirmación
            Stage confirmStage = (Stage) confirm.getDialogPane().getScene().getWindow();
            confirmStage.getIcons().add(new Image(getClass().getResourceAsStream("/imagenes/icon_tpv.png")));

            java.util.Optional<ButtonType> respuesta = confirm.showAndWait();
            if (respuesta.isPresent() && respuesta.get() == ButtonType.OK) {
                boolean eliminado = consultasDelete.eliminarCategoria(nombre);
                if (eliminado) {
                    cargarCategorias();
                    alertas.mostrarAlerta("Éxito", "Categoría y sus productos eliminados.");
                } else {
                    alertas.mostrarAlerta("Error", "No se pudo borrar. Es posible que existan restricciones en la Base de Datos.");
                }
            }
        }
    }

    private void addProducto() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/grupo2_2dam/tpv_software/vistas/vista_anadir_producto.fxml"));
            javafx.scene.Parent root = loader.load();
            AnadirProductoControlador controlador = loader.getController();
            controlador.inicializarDatos(currentCategId, currentCategNombre, this);
            Stage stage = new Stage();
            stage.setTitle("Añadir Producto");
            stage.initModality(javafx.stage.Modality.APPLICATION_MODAL);
            stage.getIcons().add(new Image(getClass().getResourceAsStream("/imagenes/icon_tpv.png"))); // ICONO
            javafx.scene.Scene scene = new javafx.scene.Scene(root);
            scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
            stage.setScene(scene);
            stage.setResizable(false);
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
            alertas.mostrarAlerta("Error", "No se pudo cargar la vista para añadir el producto.");
        }
    }

    private void modificarProducto() {
        List<Producto> productos = consultasRead.obtenerProductosPorCategoria(currentCategId);
        if (productos == null || productos.isEmpty()) {
            alertas.mostrarAlerta("Información", "No hay productos en esta categoría.");
            return;
        }

        List<String> nombres = new ArrayList<>();
        for (Producto prod : productos) nombres.add(prod.getNombre());

        ChoiceDialog<String> dialog = new ChoiceDialog<>(nombres.get(0), nombres);
        dialog.setTitle("Modificar Producto");
        dialog.setHeaderText("Selecciona el producto a modificar");
        dialog.setContentText("Producto:");

        // Icono para el diálogo
        Stage dialogStage = (Stage) dialog.getDialogPane().getScene().getWindow();
        dialogStage.getIcons().add(new Image(getClass().getResourceAsStream("/imagenes/icon_tpv.png")));

        java.util.Optional<String> resultado = dialog.showAndWait();
        if (resultado.isPresent()) {
            String nombreOriginal = resultado.get();
            Producto prodOriginal = productos.stream().filter(p -> p.getNombre().equals(nombreOriginal)).findFirst().orElse(null);
            if (prodOriginal == null) return;
            Double precioOriginal = consultasRead.obtenerPrecioProducto(nombreOriginal, currentCategId);
            if (precioOriginal != null) {
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/grupo2_2dam/tpv_software/vistas/vista_modificar_producto.fxml"));
                    javafx.scene.Parent root = loader.load();
                    ModificarProductoControlador controlador = loader.getController();
                    controlador.inicializarDatos(nombreOriginal, precioOriginal, currentCategId, prodOriginal.getImagenRuta(), this);
                    Stage stage = new Stage();
                    stage.setTitle("Modificar Producto");
                    stage.initModality(javafx.stage.Modality.APPLICATION_MODAL);
                    stage.getIcons().add(new Image(getClass().getResourceAsStream("/imagenes/icon_tpv.png")));

                    javafx.scene.Scene scene = new javafx.scene.Scene(root);
                    scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
                    stage.setScene(scene);

                    stage.setResizable(false);
                    stage.showAndWait();
                } catch (Exception e) {
                    e.printStackTrace();
                    alertas.mostrarAlerta("Error", "No se pudo cargar la vista.");
                }
            }
        }
    }

    private void eliminarProducto() {
        List<Producto> productos = consultasRead.obtenerProductosPorCategoria(currentCategId);
        if (productos == null || productos.isEmpty()) {
            alertas.mostrarAlerta("Información", "No hay productos en esta categoría.");
            return;
        }

        List<String> nombres = new ArrayList<>();
        for (Producto prod : productos) nombres.add(prod.getNombre());

        ChoiceDialog<String> dialog = new ChoiceDialog<>(nombres.get(0), nombres);
        dialog.setTitle("Eliminar Producto");
        dialog.setHeaderText("Selecciona el producto a eliminar");
        dialog.setContentText("Producto:");

        // Icono para el ChoiceDialog
        Stage dialogStage = (Stage) dialog.getDialogPane().getScene().getWindow();
        dialogStage.getIcons().add(new Image(getClass().getResourceAsStream("/imagenes/icon_tpv.png")));

        java.util.Optional<String> resultado = dialog.showAndWait();
        if (resultado.isPresent()) {
            String nombre = resultado.get();

            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
            confirm.setTitle("Confirmar Eliminación");
            confirm.setHeaderText("Borrado de producto");
            confirm.setContentText("¿Estás seguro de que deseas eliminar el producto '" + nombre + "'?");

            // Icono para el Alert
            Stage confirmStage = (Stage) confirm.getDialogPane().getScene().getWindow();
            confirmStage.getIcons().add(new Image(getClass().getResourceAsStream("/imagenes/icon_tpv.png")));

            java.util.Optional<ButtonType> respuesta = confirm.showAndWait();
            if (respuesta.isPresent() && respuesta.get() == ButtonType.OK) {
                boolean eliminado = consultasDelete.eliminarProducto(nombre, currentCategId);
                if (eliminado) {
                    cargarProductos();
                    alertas.mostrarAlerta("Éxito", "Producto eliminado correctamente.");
                } else {
                    alertas.mostrarAlerta("Error", "No se puede eliminar el producto porque tiene ventas o movimientos asociados.");
                }
            }
        }
    }

    private boolean categoriaExiste(String nombreCategoria) {
        String sql = "SELECT COUNT(*) FROM CATEGORIAS WHERE UPPER(NOMBRE_CATEGORIA) = UPPER(?)";
        try (Connection conn = obtenerConexion(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, nombreCategoria.trim());
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            alertas.mostrarAlerta("Error de Conexión", "Fallo al verificar la existencia de la categoría.");
        }
        return false;
    }

    private boolean productoExiste(String nombreProducto, int codCategoria) {
        String sql = "SELECT COUNT(*) FROM PRODUCTOS WHERE UPPER(NOMBRE_PRODUCTO) = UPPER(?) AND COD_CATEGORIA = ?";
        try (Connection conn = obtenerConexion(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
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

    @FXML
    public void cargarCategorias() {
        flowProductos.getChildren().clear();
        List<Categoria> categorias = consultasRead.obtenerCategorias();
        if (categorias == null) {
            alertas.mostrarAlerta("Error de carga", "No se pudieron recuperar las categorías.");
            return;
        }
        for (Categoria cat : categorias) {
            MFXButton btnCategoria = new MFXButton(cat.getNombre());
            btnCategoria.getStyleClass().add("mfx-button-categoria");
            btnCategoria.setPrefSize(150, 120);
            btnCategoria.setContentDisplay(ContentDisplay.TOP);
            ImageView imgView = new ImageView();
            imgView.setFitWidth(70);
            imgView.setFitHeight(70);
            imgView.setPreserveRatio(true);

            if (cat.getImagenRuta() != null && !cat.getImagenRuta().isEmpty()) {
                String rutaCompleta = System.getProperty("user.home") + "/.tpv_software/" + cat.getImagenRuta();
                Image img = new Image("file:" + rutaCompleta);
                imgView.setImage(img);
            } else {
                imgView.setImage(new Image(getClass().getResourceAsStream("/imagenes/default_category.png")));
            }

            //Redondear imágenes
            Rectangle clip = new Rectangle(70, 70);
            clip.setArcWidth(15);   // radio horizontal del redondeo
            clip.setArcHeight(15);  // radio vertical del redondeo
            imgView.setClip(clip);

            btnCategoria.setGraphic(imgView);
            btnCategoria.setOnAction(e -> cambiarModoProductos(cat.getCodigo(), cat.getNombre()));
            flowProductos.getChildren().add(btnCategoria);
        }
    }

    public void cargarProductos() {
        flowProductos.getChildren().clear();
        MFXButton btnVolver = new MFXButton("⬅ Volver");
        btnVolver.getStyleClass().add("mfx-button-categoria");
        btnVolver.setPrefSize(150, 120);
        btnVolver.setOnAction(e -> cambiarModoCategorias());
        flowProductos.getChildren().add(btnVolver);

        List<Producto> productos = consultasRead.obtenerProductosPorCategoria(currentCategId);
        if (productos == null) {
            alertas.mostrarAlerta("Error de carga", "No se pudieron recuperar los productos.");
            return;
        }
        for (Producto prod : productos) {
            MFXButton btnProducto = new MFXButton(prod.getNombre());
            btnProducto.getStyleClass().add("mfx-button-producto");
            btnProducto.setPrefSize(150, 120);
            btnProducto.setContentDisplay(ContentDisplay.TOP);
            ImageView imgView = new ImageView();
            imgView.setFitWidth(70);
            imgView.setFitHeight(70);
            imgView.setPreserveRatio(true);
            if (prod.getImagenRuta() != null && !prod.getImagenRuta().isEmpty()) {
                String rutaCompleta = System.getProperty("user.home") + "/.tpv_software/" + prod.getImagenRuta();
                Image img = new Image("file:" + rutaCompleta);
                imgView.setImage(img);
            } else {
                imgView.setImage(new Image(getClass().getResourceAsStream("/imagenes/default_product.png")));
            }

            //Redondear imágenes
            Rectangle clip = new Rectangle(70, 70);
            clip.setArcWidth(15);   // radio horizontal del redondeo
            clip.setArcHeight(15);  // radio vertical del redondeo
            imgView.setClip(clip);

            btnProducto.setGraphic(imgView);
            btnProducto.setOnAction(e -> agregarAlTicket(prod.getNombre()));
            flowProductos.getChildren().add(btnProducto);
        }
    }

    private void cambiarModoCategorias() {
        panelCategorias = true;
        currentCategId = -1;
        currentCategNombre = "";
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

    private void agregarAlTicket(String nombreProducto) {
        String nombreCat = currentCategNombre.toUpperCase();
        boolean cobroPorPeso = nombreCat.contains("FRUTA") || nombreCat.contains("VERDURA") ||
                nombreCat.contains("CARNE") || nombreCat.contains("PESCADO");
        TextInputDialog dialog = new TextInputDialog(cobroPorPeso ? "1.000" : "1");
        dialog.setTitle("Añadir al carrito");
        dialog.setHeaderText("Producto: " + nombreProducto);
        dialog.setContentText(cobroPorPeso ? "Peso en Kg:" : "Cantidad:");
        dialog.showAndWait().ifPresent(cantidadStr -> {
            Double precioRecuperado = consultasRead.obtenerPrecioProducto(nombreProducto, currentCategId);
            if (precioRecuperado == null) {
                alertas.mostrarAlerta("Error de BD", "No se pudo obtener el precio.");
                return;
            }
            final double precioUnitario = precioRecuperado;
            try {
                double cantIni = Double.parseDouble(cantidadStr.trim().replace(",", "."));
                if (cantIni <= 0 || (!cobroPorPeso && cantIni % 1 != 0)) {
                    alertas.mostrarAlerta("Error", "Cantidad no válida.");
                    return;
                }
                DetalleTicket nuevoItem = new DetalleTicket(nombreProducto, cantIni, precioUnitario, cantIni * precioUnitario);
                listaProductosTicket.add(nuevoItem);
                double[] estadoLinea = {cantIni, cantIni * precioUnitario};
                subtotalVenta += estadoLinea[1];
                javafx.scene.layout.HBox linea = new javafx.scene.layout.HBox();
                linea.setPadding(new javafx.geometry.Insets(5, 5, 5, 5));
                linea.setStyle("-fx-cursor: hand; -fx-background-color: transparent;");
                linea.setOnMouseEntered(e -> linea.setStyle("-fx-cursor: hand; -fx-background-color: #f0f0f0; -fx-background-radius: 5;"));
                linea.setOnMouseExited(e -> linea.setStyle("-fx-cursor: hand; -fx-background-color: transparent;"));
                Label lblProd = new Label();
                Region spacer = new Region();
                javafx.scene.layout.HBox.setHgrow(spacer, javafx.scene.layout.Priority.ALWAYS);
                Label lblTotal = new Label();
                Runnable actualizarLabels = () -> {
                    String txtCant = cobroPorPeso ? String.format("%.3f kg", estadoLinea[0]).replace(",", ".") : (int)estadoLinea[0] + "x";
                    String uni = cobroPorPeso ? "kg" : "ud";
                    lblProd.setText(txtCant + " " + nombreProducto + String.format(" (%.2f €/%s)", precioUnitario, uni).replace(",", "."));
                    lblTotal.setText(String.format("%.2f €", estadoLinea[1]).replace(",", "."));
                    nuevoItem.setCantidad(estadoLinea[0]);
                    nuevoItem.setTotalLinea(estadoLinea[1]);
                };
                actualizarLabels.run();
                linea.setOnMouseClicked(event -> modificarTicket(linea, nuevoItem, cobroPorPeso, estadoLinea, actualizarLabels));
                linea.getChildren().addAll(lblProd, spacer, lblTotal);
                ticketVBox.getChildren().add(linea);
                subtotalLabel.setText(String.format("%.2f €", subtotalVenta).replace(",", "."));
            } catch (NumberFormatException e) {
                alertas.mostrarAlerta("Error", "Introduce una cantidad válida");
            }
        });
    }

    private void modificarTicket(javafx.scene.layout.HBox linea, DetalleTicket item, boolean porPeso, double[] estado, Runnable refresh) {
        Alert opciones = new Alert(Alert.AlertType.CONFIRMATION);
        opciones.setTitle("Modificar producto");
        opciones.setHeaderText("Producto: " + item.getNombreProducto());
        opciones.setContentText("¿Qué deseas hacer con este producto?");
        ButtonType btnModificar = new ButtonType("Modificar Cantidad");
        ButtonType btnEliminar = new ButtonType("Eliminar");
        ButtonType btnCancelar = new ButtonType("Cancelar", ButtonBar.ButtonData.CANCEL_CLOSE);
        opciones.getButtonTypes().setAll(btnModificar, btnEliminar, btnCancelar);
        opciones.showAndWait().ifPresent(opcion -> {
            if (opcion == btnEliminar) {
                listaProductosTicket.remove(item);
                subtotalVenta -= estado[1];
                ticketVBox.getChildren().remove(linea);
                subtotalLabel.setText(String.format("%.2f €", Math.abs(subtotalVenta)).replace(",", "."));
            } else if (opcion == btnModificar) {
                String cantActualStr = porPeso ? String.valueOf(estado[0]) : String.valueOf((int)estado[0]);
                TextInputDialog dialogEdit = new TextInputDialog(cantActualStr);
                dialogEdit.setTitle("Modificar Cantidad");
                dialogEdit.setHeaderText("Nueva cantidad para: " + item.getNombreProducto());
                dialogEdit.setContentText(porPeso ? "Peso en Kg:" : "Cantidad:");
                dialogEdit.showAndWait().ifPresent(nuevaCantStr -> {
                    try {
                        double nuevaCant = Double.parseDouble(nuevaCantStr.trim().replace(",", "."));
                        if (nuevaCant <= 0 || (!porPeso && nuevaCant % 1 != 0)) {
                            alertas.mostrarAlerta("Error", "Cantidad no válida.");
                            return;
                        }
                        subtotalVenta -= estado[1];
                        estado[0] = nuevaCant;
                        estado[1] = nuevaCant * item.getPrecioUnitario();
                        subtotalVenta += estado[1];
                        item.setCantidad(nuevaCant);
                        item.setTotalLinea(estado[1]);
                        refresh.run();
                        subtotalLabel.setText(String.format("%.2f €", subtotalVenta).replace(",", "."));
                    } catch (NumberFormatException ex) {
                        alertas.mostrarAlerta("Error", "Introduce una cantidad válida");
                    }
                });
            }
        });
    }

    @FXML
    private void abrirPantallaPago(ActionEvent event) {
        if (subtotalVenta <= 0) {
            alertas.mostrarAlerta("Carrito vacío", "Añade productos antes de pagar.");
            return;
        }
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/grupo2_2dam/tpv_software/vistas/vista_pago.fxml"));
            javafx.scene.Parent root = loader.load();
            VistaPagoControlador pagoController = loader.getController();
            pagoController.inicializarDatos(this.listaProductosTicket, subtotalVenta, subtotalLabel.getScene());
            Stage stage = (Stage) subtotalLabel.getScene().getWindow();
            javafx.scene.Scene nuevaEscena = new javafx.scene.Scene(root, stage.getScene().getWidth(), stage.getScene().getHeight());
            nuevaEscena.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
            stage.setScene(nuevaEscena);
        } catch (IOException e) {
            e.printStackTrace();
            alertas.mostrarAlerta("Error", "No se pudo cargar la vista de pago.");
        }
    }

    public void handleAnadirUsuario(ActionEvent actionEvent) {
        TextInputDialog dialogUser = new TextInputDialog();
        dialogUser.setTitle("Crear cuenta");
        dialogUser.setHeaderText("Nueva cuenta");
        dialogUser.setContentText("Nombre de usuario:");

        dialogUser.showAndWait();
        String nombreUsuario = dialogUser.getEditor().getText();
        if (nombreUsuario.isEmpty()) {
            alertas.mostrarAlerta("Error", "Debes escribir un nombre");
            return;
        }
        TextInputDialog dialogPass = new TextInputDialog();
        dialogPass.setTitle("Contraseña");
        dialogPass.setHeaderText("Contraseña para " + nombreUsuario);
        dialogPass.setContentText("Contraseña:");
        dialogPass.showAndWait();
        String contrasenaUsuario = dialogPass.getEditor().getText();
        if (contrasenaUsuario.isEmpty()) {
            alertas.mostrarAlerta("Error", "Debes escribir una contraseña");
            return;
        }
        boolean creado = funcionUsuario.crearCuenta(nombreUsuario, contrasenaUsuario);
        if (creado) {
            alertas.mostrarAlerta("Éxito", "Cuenta creada correctamente");
        } else {
            alertas.mostrarAlerta("Error", "Cuenta no creada. Es posible que el nombre de usuario ya exista.");
        }
    }

    public void handleEliminarUsuario(ActionEvent actionEvent) {

        TextInputDialog dialogUser = new TextInputDialog();
        dialogUser.setTitle("Eliminar cuenta");
        dialogUser.setHeaderText("Cuenta a eliminar");
        dialogUser.setContentText("Nombre de usuario:");

        dialogUser.showAndWait();
        String nombreUsuario = dialogUser.getEditor().getText();
        if (nombreUsuario.isEmpty()) {
            alertas.mostrarAlerta("Error", "Debes escribir un nombre");
            return;
        }

        String contrasenaUsuario = dialogUser.getEditor().getText();
        if (contrasenaUsuario.isEmpty()) {
            alertas.mostrarAlerta("Error", "Debes escribir una contraseña");
            return;
        }

        boolean eliminado = funcionUsuario.eliminarCuenta(nombreUsuario);

        if (eliminado) {
            alertas.mostrarAlerta("Éxito", "Cuenta eliminada correctamente");
        }else {
            alertas.mostrarAlerta("Error", "Cuenta no eliminada. Es posible que el nombre de usuario no exista.");
        }

    }
}