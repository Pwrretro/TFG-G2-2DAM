package com.grupo2_2dam.tpv_software.controladores;

import com.grupo2_2dam.tpv_software.objetos.Usuario;
import com.grupo2_2dam.tpv_software.util.basededatos.ConexionDB;
import com.grupo2_2dam.tpv_software.util.CambiarVistas;
import com.grupo2_2dam.tpv_software.util.basededatos.DatosConexion;
import com.grupo2_2dam.tpv_software.util.basededatos.FuncionUsuario;
import com.grupo2_2dam.tpv_software.util.tratadodetexto.HashContrasena;
import com.grupo2_2dam.tpv_software.util.tratadodetexto.WRJSON;
import com.grupo2_2dam.tpv_software.util.crud.ConsultasCreate;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXPasswordField;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

public class InicioDeSesionControlador {

    @FXML private ImageView icono_imagen;
    @FXML private MFXTextField usernameField;
    @FXML private MFXPasswordField passwordField;
    @FXML private MFXButton loginButton;
    @FXML private MFXButton btnConfiguracion;

    // Identificamos si el usuario admin existe
    private final String querryContrasena = "SELECT cod_usuario, contrasena_usuario FROM USUARIOS WHERE NOMBRE_USUARIO = ?";

    @FXML
    public void initialize() {

        icono_imagen.setImage(new javafx.scene.image.Image(getClass().getResourceAsStream("/imagenes/icon_tpv.png")));

        //Crear usuario admin si no existe
        ConsultasCreate c = new ConsultasCreate();
        c.createAdminUser("admin", "admin");

        // El texto flotante sobre los MFXTextField
        usernameField.setFloatingText("Usuario");

        // El texto flotante sobre los MFXPasswordField
        passwordField.setFloatingText("Contraseña");
    }

    @FXML
    private void handleLogin() { //método en el que compararemos los campos que hay en los Field con el

        String username = usernameField.getText();
        String password = passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            mostrarAlerta("Error", "Los campos no pueden estar vacíos.");
            return;
        }

        // Usamos try-with-resources para que la conexión se cierre sola al terminar
        try {
            DatosConexion dc = WRJSON.leerJSONBaseDeDatos();
            System.out.println(dc.toString());

            // 1. Establecer conexión
            java.sql.Connection conn = ConexionDB.obtenerConexion();

            // 2. Preparar y ejecutar la consulta
            java.sql.PreparedStatement pstmt = conn.prepareStatement(querryContrasena);
            pstmt.setString(1, username);
            //pstmt.setString(2, password); // Obtenemos abajo las nuevas comprobaciones

            java.sql.ResultSet rs = pstmt.executeQuery();;

            // 3. Procesar resultado
            if (rs.next()) {
                String cod_usuario = rs.getString(1);
                String hashAlmacenado = rs.getString(2);
                boolean verificarPassword = HashContrasena.verifyPassword(password, hashAlmacenado);

                if (verificarPassword){

                    // Crear objeto Usuario con el cod_usuario y nombre_usuario y lo guardamos en JSON para usarlo en otras vistas
                    Usuario usuarioActual = new Usuario(Integer.parseInt(cod_usuario), username, null);
                    FuncionUsuario fu = new FuncionUsuario();
                    fu.guardarUsuarioActual(usuarioActual);

                    // Obtener el Stage desde cualquier nodo (ej. loginButton)
                    Stage stage = (Stage) loginButton.getScene().getWindow();
                    String vistaPrincipal = "/com/grupo2_2dam/tpv_software/vistas/vista_principal.fxml";
                    CambiarVistas.cambiarVista(vistaPrincipal, stage); // Cambiar de vista
                } else {
                    mostrarAlerta("Error al iniciar sesión", "Usuario o contraseña incorrectos");
                }
            } else {
                mostrarAlerta("Error al iniciar sesión", "Usuario o contraseña incorrectos");
            }


        } catch (java.sql.SQLException e) {
            mostrarAlerta("Error de BD", "No se pudo conectar a la base de datos.");
        } catch (IOException e) {
            //Cualquier otro error
            mostrarAlerta("Error", "Hubo un error: " + e.getMessage());
        } catch (NoSuchAlgorithmException e) {
            mostrarAlerta("Error", e.getMessage());
        } catch (InvalidKeySpecException e) {
            mostrarAlerta("Error", e.getMessage());
        }
    }

    private void mostrarAlerta(String titulo, String contenido) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(contenido);

        try {
            // Obtener la ventana (Stage) interna de la alerta y añadir el icono
            Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
            stage.getIcons().add(new Image(getClass().getResourceAsStream("/imagenes/icon_tpv.png")));
        } catch (Exception e) {
            System.err.println("Error al cargar el icono: " + e.getMessage());
        }

        alert.showAndWait();
    }

    public void abrirConfiguracion() throws IOException {
        Stage stage = (Stage) btnConfiguracion.getScene().getWindow();
        String configuracion = "/com/grupo2_2dam/tpv_software/vistas/configuracion.fxml";
        CambiarVistas.cambiarVista(configuracion, stage); // Cambiar de vista
    }
}