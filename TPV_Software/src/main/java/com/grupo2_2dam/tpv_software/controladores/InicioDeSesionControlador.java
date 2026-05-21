package com.grupo2_2dam.tpv_software.controladores;

import com.grupo2_2dam.tpv_software.objetos.Usuario;
import com.grupo2_2dam.tpv_software.util.Alertas;
import com.grupo2_2dam.tpv_software.util.basededatos.ConexionDB;
import com.grupo2_2dam.tpv_software.util.CambiarVistas;
import com.grupo2_2dam.tpv_software.util.basededatos.DatosConexion;
import com.grupo2_2dam.tpv_software.util.basededatos.FuncionUsuario;
import com.grupo2_2dam.tpv_software.util.tratadodetexto.HashContrasena;
import com.grupo2_2dam.tpv_software.util.tratadodetexto.WRJSON;
import com.grupo2_2dam.tpv_software.util.basededatos.crud.ConsultasCreate;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXPasswordField;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.fxml.FXML;
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

    Alertas alertas = new Alertas();

    // Identificamos si el usuario admin existe
    private FuncionUsuario funcionUsuario = new FuncionUsuario();
    // Querry obsoleta
    //private final String querryContrasena = "SELECT cod_usuario, contrasena_usuario FROM USUARIOS WHERE NOMBRE_USUARIO = ?";

    /**
     * Inicializar la vista de inicio de sesión, estableciendo el icono y los textos flotantes, además de crear el usuario admin si no existe en la base de datos
     */
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

    /**
     * Logueo con el usuario y contraseña introducidos, comprobando que no estén vacíos y que coincidan con los datos de la base de datos, mostrando una alerta en caso de error y cambiando a la vista principal en caso de éxito
     */
    @FXML
    private void handleLogin() { //método en el que compararemos los campos que hay en los Field con el

        String username = usernameField.getText();
        String password = passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            alertas.mostrarAlerta("Error", "Los campos no pueden estar vacíos.");
            return;
        }

        // Usamos try-with-resources para que la conexión se cierre sola al terminar
        try {
            Usuario usuario = funcionUsuario.verificarCredenciales(username, password);
            if (usuario != null) {
                funcionUsuario.guardarUsuarioActual(usuario);
                Stage stage = (Stage) loginButton.getScene().getWindow();
                CambiarVistas.cambiarVista("/com/grupo2_2dam/tpv_software/vistas/vista_principal.fxml", stage);
            } else {
                alertas.mostrarAlerta("Error al iniciar sesión", "Usuario o contraseña incorrectos");
            }
        } catch (Exception e) {
            alertas.mostrarAlerta("Error", "Hubo un error: " + e.getMessage());
        }
    }

    /**
     * Abrir la vista de configuración, donde se pueden modificar los datos de conexión a la base de datos
     * @throws IOException
     */
    public void abrirConfiguracion() throws IOException {
        Stage stage = (Stage) btnConfiguracion.getScene().getWindow();
        String configuracion = "/com/grupo2_2dam/tpv_software/vistas/configuracion.fxml";
        CambiarVistas.cambiarVista(configuracion, stage); // Cambiar de vista
    }
}