package com.restaurante.app;

import com.restaurante.dao.PlatoDAO;
import com.restaurante.model.Plato;
import com.restaurante.service.ReporteService;
import com.restaurante.util.Conexion;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.beans.property.SimpleStringProperty;

import java.awt.Desktop;
import java.io.File;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.Optional;

/**
 * Interfaz principal del Sistema Restaurante (CRUD de platos).
 */
public class MainApp extends Application {

    private final PlatoDAO dao = new PlatoDAO();
    private final ObservableList<Plato> datos = FXCollections.observableArrayList();

    // Controles del formulario
    private final TextField txtId = new TextField();
    private final TextField txtNombre = new TextField();
    private final ComboBox<String> cmbCategoria = new ComboBox<>();
    private final TextArea txtDescripcion = new TextArea();
    private final TextField txtPrecio = new TextField();
    private final Spinner<Integer> spnTiempo = new Spinner<>(1, 240, 15);
    private final CheckBox chkDisponible = new CheckBox("Disponible");

    // Tabla y búsqueda
    private final TableView<Plato> tabla = new TableView<>();
    private final TextField txtBuscar = new TextField();
    private final Label lblEstado = new Label();

    @Override
    public void start(Stage stage) {
        BorderPane root = new BorderPane();
        root.setTop(crearCabecera());
        root.setLeft(crearFormulario());
        root.setCenter(crearPanelTabla());
        root.setStyle("-fx-background-color: #f4f6f8;");

        Scene scene = new Scene(root, 1100, 650);
        stage.setTitle("Sistema Restaurante - CRUD de Platos");
        stage.setScene(scene);
        stage.show();

        verificarConexion();
        cargarTabla();
    }

    // ---------------------------------------------------------------- UI

    private VBox crearCabecera() {
        Label titulo = new Label("🍽  Sistema de Gestión de Platos - Restaurante");
        titulo.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: white;");
        lblEstado.setStyle("-fx-text-fill: white; -fx-font-size: 12px;");

        VBox cabecera = new VBox(4, titulo, lblEstado);
        cabecera.setPadding(new Insets(14, 20, 14, 20));
        cabecera.setStyle("-fx-background-color: #0b5394;");
        return cabecera;
    }

    private VBox crearFormulario() {
        txtId.setEditable(false);
        txtId.setPromptText("Automático");
        txtNombre.setPromptText("Ej. Lomo saltado");

        cmbCategoria.setItems(FXCollections.observableArrayList(
                "Entrada", "Plato de fondo", "Sopa", "Postre", "Bebida"));
        cmbCategoria.setPromptText("Seleccione");
        cmbCategoria.setMaxWidth(Double.MAX_VALUE);

        txtDescripcion.setPromptText("Descripción del plato");
        txtDescripcion.setPrefRowCount(3);
        txtDescripcion.setWrapText(true);

        txtPrecio.setPromptText("0.00");

        spnTiempo.setEditable(true);
        spnTiempo.setMaxWidth(Double.MAX_VALUE);

        chkDisponible.setSelected(true);

        GridPane form = new GridPane();
        form.setHgap(10);
        form.setVgap(10);
        form.addRow(0, new Label("ID:"), txtId);
        form.addRow(1, new Label("Nombre:"), txtNombre);
        form.addRow(2, new Label("Categoría:"), cmbCategoria);
        form.addRow(3, new Label("Descripción:"), txtDescripcion);
        form.addRow(4, new Label("Precio (S/):"), txtPrecio);
        form.addRow(5, new Label("Tiempo (min):"), spnTiempo);
        form.addRow(6, new Label("Estado:"), chkDisponible);
        GridPane.setHgrow(txtNombre, Priority.ALWAYS);

        Button btnRegistrar = crearBoton("Registrar", "#2e7d32", e -> registrar());
        Button btnActualizar = crearBoton("Actualizar", "#1565c0", e -> actualizar());
        Button btnEliminar = crearBoton("Eliminar", "#c62828", e -> eliminar());
        Button btnLimpiar = crearBoton("Limpiar", "#616161", e -> limpiar());
        Button btnReporte = crearBoton("📄 Reporte PDF", "#ef6c00", e -> generarReporte());

        HBox fila1 = new HBox(8, btnRegistrar, btnActualizar);
        HBox fila2 = new HBox(8, btnEliminar, btnLimpiar);
        fila1.setAlignment(Pos.CENTER);
        fila2.setAlignment(Pos.CENTER);
        btnReporte.setMaxWidth(Double.MAX_VALUE);

        VBox panel = new VBox(18, new Label("Datos del plato"), form, fila1, fila2, btnReporte);
        panel.getChildren().get(0).setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        panel.setPadding(new Insets(20));
        panel.setPrefWidth(340);
        panel.setStyle("-fx-background-color: white; -fx-border-color: #dcdcdc; -fx-border-width: 0 1 0 0;");
        return panel;
    }

    private VBox crearPanelTabla() {
        txtBuscar.setPromptText("Buscar por nombre o categoría...");
        txtBuscar.textProperty().addListener((obs, anterior, nuevo) -> buscar(nuevo));
        HBox.setHgrow(txtBuscar, Priority.ALWAYS);

        Button btnRecargar = crearBoton("Recargar", "#455a64", e -> {
            txtBuscar.clear();
            cargarTabla();
        });
        HBox barra = new HBox(10, new Label("Buscar:"), txtBuscar, btnRecargar);
        barra.setAlignment(Pos.CENTER_LEFT);

        TableColumn<Plato, Number> colId = new TableColumn<>("ID");
        colId.setCellValueFactory(c -> new javafx.beans.property.SimpleIntegerProperty(c.getValue().getIdPlato()));
        colId.setPrefWidth(50);

        TableColumn<Plato, String> colNombre = new TableColumn<>("Nombre");
        colNombre.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getNombre()));
        colNombre.setPrefWidth(160);

        TableColumn<Plato, String> colCategoria = new TableColumn<>("Categoría");
        colCategoria.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getCategoria()));
        colCategoria.setPrefWidth(110);

        TableColumn<Plato, String> colDescripcion = new TableColumn<>("Descripción");
        colDescripcion.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getDescripcion()));
        colDescripcion.setPrefWidth(230);

        TableColumn<Plato, String> colPrecio = new TableColumn<>("Precio (S/)");
        colPrecio.setCellValueFactory(c ->
                new SimpleStringProperty(String.format("%.2f", c.getValue().getPrecio())));
        colPrecio.setPrefWidth(85);

        TableColumn<Plato, String> colTiempo = new TableColumn<>("Tiempo (min)");
        colTiempo.setCellValueFactory(c ->
                new SimpleStringProperty(String.valueOf(c.getValue().getTiempoPreparacion())));
        colTiempo.setPrefWidth(90);

        TableColumn<Plato, String> colDisp = new TableColumn<>("Estado");
        colDisp.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().isDisponibilidad() ? "Disponible" : "Agotado"));
        colDisp.setPrefWidth(90);

        tabla.getColumns().addAll(colId, colNombre, colCategoria, colDescripcion, colPrecio, colTiempo, colDisp);
        tabla.setItems(datos);
        tabla.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        tabla.setPlaceholder(new Label("No hay platos registrados"));
        VBox.setVgrow(tabla, Priority.ALWAYS);

        // Al seleccionar una fila, se llena el formulario
        tabla.getSelectionModel().selectedItemProperty().addListener((obs, anterior, p) -> {
            if (p != null) {
                mostrarEnFormulario(p);
            }
        });

        VBox panel = new VBox(12, barra, tabla);
        panel.setPadding(new Insets(20));
        return panel;
    }

    private Button crearBoton(String texto, String color, javafx.event.EventHandler<javafx.event.ActionEvent> accion) {
        Button b = new Button(texto);
        b.setStyle("-fx-background-color: " + color + "; -fx-text-fill: white; -fx-font-weight: bold;"
                + "-fx-padding: 8 16 8 16; -fx-cursor: hand;");
        b.setOnAction(accion);
        return b;
    }

    // ------------------------------------------------------- Operaciones

    private void verificarConexion() {
        if (Conexion.probarConexion()) {
            lblEstado.setText("● Conectado a MySQL (db_restaurante)");
        } else {
            lblEstado.setText("● Sin conexión a MySQL - revisa config.properties");
            mostrar(AlertType.ERROR, "Conexión",
                    "No se pudo conectar a MySQL.\nVerifica que el servidor esté activo y que usuario/contraseña "
                            + "en config.properties sean correctos.");
        }
    }

    private void cargarTabla() {
        try {
            datos.setAll(dao.listar());
        } catch (SQLException e) {
            mostrar(AlertType.ERROR, "Error al consultar", e.getMessage());
        }
    }

    private void buscar(String texto) {
        try {
            if (texto == null || texto.isBlank()) {
                datos.setAll(dao.listar());
            } else {
                datos.setAll(dao.buscar(texto.trim()));
            }
        } catch (SQLException e) {
            mostrar(AlertType.ERROR, "Error al buscar", e.getMessage());
        }
    }

    private void registrar() {
        Plato p = leerFormulario(false);
        if (p == null) {
            return;
        }
        try {
            if (dao.insertar(p)) {
                mostrar(AlertType.INFORMATION, "Registro", "Plato registrado correctamente.");
                limpiar();
                cargarTabla();
            }
        } catch (SQLException e) {
            mostrar(AlertType.ERROR, "Error al registrar", e.getMessage());
        }
    }

    private void actualizar() {
        if (txtId.getText().isBlank()) {
            mostrar(AlertType.WARNING, "Actualizar", "Seleccione un plato de la tabla para actualizar.");
            return;
        }
        Plato p = leerFormulario(true);
        if (p == null) {
            return;
        }
        try {
            if (dao.actualizar(p)) {
                mostrar(AlertType.INFORMATION, "Actualización", "Plato actualizado correctamente.");
                limpiar();
                cargarTabla();
            }
        } catch (SQLException e) {
            mostrar(AlertType.ERROR, "Error al actualizar", e.getMessage());
        }
    }

    private void eliminar() {
        if (txtId.getText().isBlank()) {
            mostrar(AlertType.WARNING, "Eliminar", "Seleccione un plato de la tabla para eliminar.");
            return;
        }
        Alert confirmar = new Alert(AlertType.CONFIRMATION,
                "¿Eliminar el plato \"" + txtNombre.getText() + "\"? Esta acción no se puede deshacer.",
                ButtonType.YES, ButtonType.NO);
        confirmar.setTitle("Confirmar eliminación");
        confirmar.setHeaderText(null);
        Optional<ButtonType> respuesta = confirmar.showAndWait();
        if (respuesta.isPresent() && respuesta.get() == ButtonType.YES) {
            try {
                if (dao.eliminar(Integer.parseInt(txtId.getText()))) {
                    mostrar(AlertType.INFORMATION, "Eliminación", "Plato eliminado correctamente.");
                    limpiar();
                    cargarTabla();
                }
            } catch (SQLException e) {
                mostrar(AlertType.ERROR, "Error al eliminar", e.getMessage());
            }
        }
    }

    /** Reto 1: reporte PDF con JasperReports. */
    private void generarReporte() {
        FileChooser fc = new FileChooser();
        fc.setTitle("Guardar reporte PDF");
        fc.setInitialFileName("reporte_platos.pdf");
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Archivo PDF", "*.pdf"));
        File destino = fc.showSaveDialog(tabla.getScene().getWindow());
        if (destino == null) {
            return;
        }
        try {
            new ReporteService().generarPdf(destino.getAbsolutePath());
            mostrar(AlertType.INFORMATION, "Reporte", "Reporte generado en:\n" + destino.getAbsolutePath());
            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().open(destino);
            }
        } catch (Exception e) {
            mostrar(AlertType.ERROR, "Error al generar el reporte", String.valueOf(e.getMessage()));
        }
    }

    // ----------------------------------------------------------- Ayudas

    private Plato leerFormulario(boolean conId) {
        String nombre = txtNombre.getText().trim();
        String categoria = cmbCategoria.getValue();
        String precioTxt = txtPrecio.getText().trim().replace(',', '.');

        if (nombre.isEmpty() || categoria == null || precioTxt.isEmpty()) {
            mostrar(AlertType.WARNING, "Datos incompletos", "Nombre, categoría y precio son obligatorios.");
            return null;
        }

        BigDecimal precio;
        try {
            precio = new BigDecimal(precioTxt);
            if (precio.signum() < 0) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException e) {
            mostrar(AlertType.WARNING, "Precio inválido", "Ingrese un precio numérico mayor o igual a 0.");
            return null;
        }

        Integer tiempo;
        try {
            spnTiempo.increment(0); // fuerza a confirmar el valor escrito a mano
            tiempo = spnTiempo.getValue();
        } catch (Exception e) {
            tiempo = null;
        }
        if (tiempo == null || tiempo <= 0) {
            mostrar(AlertType.WARNING, "Tiempo inválido", "El tiempo de preparación debe ser mayor a 0.");
            return null;
        }

        Plato p = new Plato();
        if (conId) {
            p.setIdPlato(Integer.parseInt(txtId.getText()));
        }
        p.setNombre(nombre);
        p.setCategoria(categoria);
        p.setDescripcion(txtDescripcion.getText().trim());
        p.setPrecio(precio);
        p.setTiempoPreparacion(tiempo);
        p.setDisponibilidad(chkDisponible.isSelected());
        return p;
    }

    private void mostrarEnFormulario(Plato p) {
        txtId.setText(String.valueOf(p.getIdPlato()));
        txtNombre.setText(p.getNombre());
        cmbCategoria.setValue(p.getCategoria());
        txtDescripcion.setText(p.getDescripcion());
        txtPrecio.setText(p.getPrecio().toPlainString());
        spnTiempo.getValueFactory().setValue(p.getTiempoPreparacion());
        chkDisponible.setSelected(p.isDisponibilidad());
    }

    private void limpiar() {
        txtId.clear();
        txtNombre.clear();
        cmbCategoria.setValue(null);
        txtDescripcion.clear();
        txtPrecio.clear();
        spnTiempo.getValueFactory().setValue(15);
        chkDisponible.setSelected(true);
        tabla.getSelectionModel().clearSelection();
    }

    private void mostrar(AlertType tipo, String titulo, String mensaje) {
        Alert alerta = new Alert(tipo, mensaje);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
