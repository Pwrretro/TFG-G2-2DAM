package com.grupo2_2dam.tpv_software.controladores;

import com.grupo2_2dam.tpv_software.util.Alertas;
import com.grupo2_2dam.tpv_software.util.basededatos.DatosConexion;
import com.grupo2_2dam.tpv_software.util.CambiarVistas;
import com.grupo2_2dam.tpv_software.util.basededatos.ConexionDB;
import com.grupo2_2dam.tpv_software.util.tratadodetexto.HashContrasena;
import com.grupo2_2dam.tpv_software.util.tratadodetexto.WRJSON;
import io.github.palexdev.materialfx.controls.*;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

public class ConfiguracionControlador {

    @FXML private ToggleGroup conexionGroup;
    @FXML private MFXRadioButton rbHost, rbURL;
    @FXML private VBox hostOptions, urlOptions;
    @FXML private MFXTextField hostField, portField, urlField, databaseField, usernameField;
    @FXML private MFXCheckbox showAllDatabasesCheck, savePasswordCheck;
    @FXML private MFXButton guardarButton, regresarBoton;
    @FXML private MFXPasswordField passwordField;
    @FXML private MFXButton probarButton;

    Alertas alertas = new Alertas();

    /**
     * Inicializar la vista de configuración, estableciendo los textos flotantes y la lógica para mostrar/ocultar las opciones según el tipo de conexión seleccionado
     */
    @FXML
    public void initialize() {

        // Floating texts
        hostField.setFloatingText("Host");
        portField.setFloatingText("Puerto");
        urlField.setFloatingText("URL");
        databaseField.setFloatingText("Base de datos");
        usernameField.setFloatingText("Usuario");
        passwordField.setFloatingText("Contraseña");

        // Alternar visibilidad
        rbHost.selectedProperty().addListener((obs, oldVal, newVal) -> {
            hostOptions.setVisible(newVal);
            hostOptions.setManaged(newVal);

            urlOptions.setVisible(!newVal);
            urlOptions.setManaged(!newVal);
        });

        rbHost.setSelected(true);
    }

    /**
     * Obtenemos los datos de la vista y los guardamos en un objeto DatosConexion para luego usarlo tanto para probar la conexión como para guardarlo en el JSON
     * @return
     */
    private DatosConexion obtenerDatos(){

        HashContrasena hc = new HashContrasena();
        DatosConexion dc = null;

        //Hacemos comprobaciones
        int modo = rbURL.isSelected() ? 1 : 2; // 1 = URL, 2 = Host
        String url = urlField.getText();
        String host = hostField.getText();
        String nombreDB = databaseField.getText();
        String usuario = usernameField.getText();
        String contrasena = passwordField.getText();

        //Parseamos el puerto
        int puerto;
        try{
            puerto = Integer.parseInt(portField.getText());
        }catch (NumberFormatException e){
            puerto = 5432;
        }

        try{
            String contrasenaHasheada = hc.hashPassword(contrasena);

            if (modo == 1){
                dc = new DatosConexion(1, url, "", 0, "", contrasena, usuario);
            }

            if (modo == 2){
                dc = new DatosConexion(2, "", host, puerto, nombreDB, contrasena, usuario);
            }
        }catch (Exception e){
            System.out.println("Error: " + e.getMessage());
        }

        return dc;
    }

    /**
     * Guardamos la configuración hecha en la vista de la configuración en un JSON
     * @throws IOException
     */
    @FXML
    private void guardarConfiguracion() throws IOException {

        WRJSON wrjson = new WRJSON();
        DatosConexion dc = obtenerDatos();
        boolean hecho = wrjson.escribirJSONBaseDeDatos(dc);

            if (hecho){
                alertas.mostrarAlerta("Guardado correctamente", "La configuración se ha guardado correctamente.");

            }else{
                alertas.mostrarAlerta("Error al guardar", "Parece que hubo un error al intentar guardar los datos.");
            }
        regresarInicioSesion();
    }


    /**
     * Probamos la configuración antes de sobreescribir la configuración del JSON
     * @return boolean para comprobar que la conexión se hizo correctamente
     * @throws SQLException
     */
    @FXML
    private boolean probarConfiguracion() throws SQLException {

        ConexionDB cdb = new ConexionDB();
        DatosConexion dc = obtenerDatos();
        //HashContraseña hc = new HashContraseña();

        try {

            Connection c = cdb.getConnectionWithObject(dc);

            if (c != null && !c.isClosed()) {
                alertas.mostrarAlerta("Conexión correcta", "Se ha conectado correctamente con la base de datos");
                c.close();  // cerrar después de la prueba
                return true;
            } else {
                alertas.mostrarAlerta("Error al conectar", "No se pudo establecer la conexión (conexión nula o cerrada)");
            }

        } catch (SQLException e) {
            alertas.mostrarAlerta("Error al conectar", "Error de base de datos: " + e.getMessage());;
        } catch (Exception e) {
            alertas.mostrarAlerta("Error al conectar", "Error inesperado: " + e.getMessage());
        }
        return false;
    }

    /**
     * Cambiar de vista a la vista inicio de sesion
     * @throws IOException
     */
    @FXML
    private void regresarInicioSesion() throws IOException {
        Stage stage = (Stage) regresarBoton.getScene().getWindow();
        String regresar = "/com/grupo2_2dam/tpv_software/vistas/inicio_de_sesion.fxml";
        CambiarVistas.cambiarVista(regresar, stage); // Cambiar de vista
    }
}
