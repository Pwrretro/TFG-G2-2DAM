package com.grupo2_2dam.tpv_software.controladores;

import com.grupo2_2dam.tpv_software.objetos.DetalleTicket;
import com.grupo2_2dam.tpv_software.util.Alertas;
import com.grupo2_2dam.tpv_software.util.CambiarVistas;
import com.grupo2_2dam.tpv_software.util.basededatos.FuncionUsuario;
import io.github.palexdev.materialfx.controls.MFXButton;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import java.io.File;


import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VistaPagoControlador {
    @FXML private VBox vboxProductos;
    @FXML private Label lblTotal;
    @FXML private javafx.scene.control.Button btnVolver;

    private List<DetalleTicket> listaRecibida;
    private double totalVenta;
    private javafx.scene.Scene escenaAnterior;

    Alertas alertas = new Alertas();

    /**
     * Inicializar la vista de pago con la lista de productos, el total de la venta y la escena anterior para poder volver a ella sin perder los datos introducidos
     * @param lista
     * @param total
     * @param escenaAnterior
     */
    //Se ha llamado desde el controlador principal antes de mostrar la escena para cargas los datos
    public void inicializarDatos(List<DetalleTicket> lista, double total, javafx.scene.Scene escenaAnterior) {
        this.listaRecibida = lista;
        this.totalVenta = total;
        this.escenaAnterior = escenaAnterior; //guardamos la pantalla principal intacta, si volvemos se han guardado los productos añadidos

        lblTotal.setText(String.format("%.2f €", total).replace(",", "."));

        vboxProductos.getChildren().clear();

        for (DetalleTicket item : lista) {
            //estilo de la vista
            javafx.scene.layout.HBox fila = new javafx.scene.layout.HBox();
            fila.setSpacing(10);
            fila.setPadding(new javafx.geometry.Insets(8, 12, 8, 12));

            fila.setStyle("-fx-background-radius: 8; -fx-background-color: transparent;");
            fila.setOnMouseEntered(e -> fila.setStyle("-fx-background-radius: 8; -fx-background-color: #f0f0f0;"));
            fila.setOnMouseExited(e -> fila.setStyle("-fx-background-radius: 8; -fx-background-color: transparent;"));

            //peso o cantidad
            boolean esPorPeso = item.getCantidad() % 1 != 0;
            String unidad = esPorPeso ? "kg" : "ud";
            String txtCant = esPorPeso ? String.format("%.3f kg", item.getCantidad()).replace(",", ".") : (int)item.getCantidad() + "x";

            String precioUnitarioTxt = String.format(" (%.2f €/%s)", item.getPrecioUnitario(), unidad).replace(",", ".");
            Label lblInfo = new Label(txtCant + " " + item.getNombreProducto() + precioUnitarioTxt);
            lblInfo.setStyle("-fx-text-fill: black; -fx-font-size: 14px;");

            javafx.scene.layout.Region spacer = new javafx.scene.layout.Region();
            javafx.scene.layout.HBox.setHgrow(spacer, javafx.scene.layout.Priority.ALWAYS);

            Label lblPrecio = new Label(String.format("%.2f €", item.getTotalLinea()).replace(",", "."));
            lblPrecio.setStyle("-fx-font-weight: bold; -fx-text-fill: black; -fx-font-size: 14px;");

            fila.getChildren().addAll(lblInfo, spacer, lblPrecio);
            vboxProductos.getChildren().add(fila);
        }
    }

    /**
     * Finalizar la venta generando un PDF con JasperReports, guardándolo en el escritorio del usuario y abriéndolo automáticamente, además de mostrar una alerta de confirmación y volver a la pantalla principal
     */
    @FXML
    private void finalizarVentaGenerarPDF() {
        try {
            //cargamos jrxml
            JasperReport reporte = JasperCompileManager.compileReport(
                    getClass().getResourceAsStream("/reports/plantilla_factura.jrxml")
            );

            //pasamos los parámetros necesarios
            Map<String, Object> parametros = new HashMap<>();
            parametros.put("TOTAL_FACTURA", totalVenta);

            FuncionUsuario fu = new FuncionUsuario();
            parametros.put("CAJERO", fu.obtenerUsuarioActual().getNombre_usuario()); //Obtenemos el nombre del usuario actual del JSON

            //recibimos la lista de productos
            JRBeanCollectionDataSource ds = new JRBeanCollectionDataSource(listaRecibida);

            //escribimos el report
            JasperPrint print = JasperFillManager.fillReport(reporte, parametros, ds);

            //lo guardamos en una carpeta del equipo
            /** Cambio para guardar en desktop
            String nombreArchivo = "Factura_" + System.currentTimeMillis() + ".pdf";
            String ruta = System.getProperty("user.home") + File.separator + nombreArchivo;
            JasperExportManager.exportReportToPdfFile(print, ruta);
             **/
            String desktopPath = System.getProperty("user.home") + File.separator + "Desktop";
            String nombreArchivo = "Factura_" + System.currentTimeMillis() + ".pdf";
            String ruta = desktopPath + File.separator + nombreArchivo;
            JasperExportManager.exportReportToPdfFile(print, ruta);

            //creamos el pdf
            File pdfFile = new File(ruta);

            //el archivo pdf se abre automáticamente
            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().open(pdfFile);
            } else {
                System.out.println("El sistema no soporta la apertura automática de archivos.");
            }

            //confirmación de la compra
            alertas.mostrarAlerta("Éxito", "Factura generada en: " + ruta);

            //volvemos a la ventana principal
            Stage stage = (Stage) lblTotal.getScene().getWindow();
            CambiarVistas.cambiarVista("/com/grupo2_2dam/tpv_software/vistas/vista_principal.fxml", stage);

            System.out.println("PDF creado en: " + ruta);
        } catch (JRException e) {
            e.printStackTrace();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Volver a la pantalla principal sin perder los datos introducidos, ya que se ha guardado la escena principal al abrir la vista de pago, por lo que volvemos a esa escena sin necesidad de recargarla ni perder los datos introducidos
     * @param event
     */
    @FXML
    private void volverAlTPV(ActionEvent event) {
        javafx.stage.Stage stage = (javafx.stage.Stage) btnVolver.getScene().getWindow();
        stage.setScene(escenaAnterior);
    }
}
