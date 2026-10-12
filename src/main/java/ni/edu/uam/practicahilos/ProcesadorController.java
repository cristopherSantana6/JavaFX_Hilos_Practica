package ni.edu.uam.practicahilos;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;

public class ProcesadorController {

    // Controles de la práctica guiada: procesamiento de 100 clientes.
    @FXML private Label lblEstado;
    @FXML private Label lblPorcentaje;
    @FXML private ProgressBar barraProgreso;
    @FXML private Button btnIniciar;
    @FXML private Button btnDetener;
    @FXML private Button btnReiniciar;

    // Controles del reto integrador: dos procesos concurrentes.
    @FXML private Label lblEstadoClientes;
    @FXML private Label lblPorcentajeClientes;
    @FXML private ProgressBar barraClientes;
    @FXML private Button btnDetenerClientes;

    @FXML private Label lblEstadoProductos;
    @FXML private Label lblPorcentajeProductos;
    @FXML private ProgressBar barraProductos;
    @FXML private Button btnDetenerProductos;

    @FXML private Button btnSimularReto;
    @FXML private Button btnReiniciarReto;

    private Thread hiloProceso;
    private Thread hiloClientes;
    private Thread hiloProductos;

    // Estas banderas se modifican desde el hilo de JavaFX.
    private boolean procesoIndividualActivo;
    private boolean clientesActivo;
    private boolean productosActivo;

    @FXML
    private void initialize() {
        habilitarBotonesPractica();
        actualizarBotonesReto();
    }

    // PASOS 1-5: un hilo que simula la lectura de 100 registros de clientes.
    @FXML
    private void iniciarProceso() {
        if (procesoIndividualActivo) {
            return;
        }

        procesoIndividualActivo = true;
        barraProgreso.setProgress(0);
        lblPorcentaje.setText("0% (0/100)");
        lblEstado.setText("Procesando registros de clientes...");
        btnIniciar.setDisable(true);
        btnDetener.setDisable(false);
        btnReiniciar.setDisable(true);

        Runnable tarea = () -> {
            try {
                for (int i = 1; i <= 100; i++) {
                    if (Thread.currentThread().isInterrupted()) {
                        throw new InterruptedException("Proceso interrumpido por el usuario");
                    }

                    Thread.sleep(100);
                    final int avance = i;
                    Platform.runLater(() -> {
                        barraProgreso.setProgress(avance / 100.0);
                        lblPorcentaje.setText(avance + "% (" + avance + "/100)");
                    });
                }
                Platform.runLater(() -> finalizarPractica("Proceso finalizado correctamente"));
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                Platform.runLater(() -> finalizarPractica("Proceso detenido por el usuario"));
            }
        };

        hiloProceso = new Thread(tarea, "Hilo-Practica-Clientes");
        hiloProceso.setDaemon(true);
        hiloProceso.start();
    }

    @FXML
    private void detenerProceso() {
        if (hiloProceso != null && procesoIndividualActivo && hiloProceso.isAlive()) {
            btnDetener.setDisable(true);
            lblEstado.setText("Solicitando detención...");
            hiloProceso.interrupt();
        }
    }

    @FXML
    private void reiniciarProceso() {
        if (procesoIndividualActivo) {
            return;
        }
        barraProgreso.setProgress(0);
        lblPorcentaje.setText("0% (0/100)");
        lblEstado.setText("Esperando inicio");
        habilitarBotonesPractica();
    }

    private void finalizarPractica(String mensaje) {
        procesoIndividualActivo = false;
        lblEstado.setText(mensaje);
        habilitarBotonesPractica();
    }

    private void habilitarBotonesPractica() {
        btnIniciar.setDisable(false);
        btnDetener.setDisable(true);
        btnReiniciar.setDisable(false);
    }

    // RETO FINAL: dos Runnable independientes, cada uno con su propio Thread.
    // Clientes: 100 registros x 70 ms. Productos: 50 registros x 160 ms.
    // Los botones Detener actúan sobre un único proceso.
    @FXML
    private void simularReto() {
        if (clientesActivo || productosActivo) {
            return;
        }

        prepararClientes();
        prepararProductos();
        clientesActivo = true;
        productosActivo = true;
        actualizarBotonesReto();

        Runnable tareaClientes = crearTarea(
                "clientes", 100, 70,
                barraClientes, lblPorcentajeClientes,
                () -> finalizarClientes("Clientes procesados: 100/100"),
                () -> finalizarClientes("Procesamiento de clientes detenido")
        );
        Runnable tareaProductos = crearTarea(
                "productos", 50, 160,
                barraProductos, lblPorcentajeProductos,
                () -> finalizarProductos("Productos procesados: 50/50"),
                () -> finalizarProductos("Procesamiento de productos detenido")
        );

        hiloClientes = new Thread(tareaClientes, "Hilo-Clientes");
        hiloProductos = new Thread(tareaProductos, "Hilo-Productos");
        hiloClientes.setDaemon(true);
        hiloProductos.setDaemon(true);
        hiloClientes.start();
        hiloProductos.start();
    }

    private Runnable crearTarea(
            String nombre,
            int total,
            long pausaMilisegundos,
            ProgressBar barra,
            Label porcentaje,
            Runnable alCompletar,
            Runnable alDetener
    ) {
        return () -> {
            try {
                for (int i = 1; i <= total; i++) {
                    if (Thread.currentThread().isInterrupted()) {
                        throw new InterruptedException("Hilo interrumpido");
                    }

                    Thread.sleep(pausaMilisegundos);
                    final int avance = i;
                    Platform.runLater(() -> {
                        barra.setProgress(avance / (double) total);
                        porcentaje.setText(String.format(
                                "%d%% (%d/%d)", Math.round(avance * 100.0 / total), avance, total
                        ));
                    });
                }
                Platform.runLater(alCompletar);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                Platform.runLater(alDetener);
            }
        };
    }

    private void prepararClientes() {
        barraClientes.setProgress(0);
        lblPorcentajeClientes.setText("0% (0/100)");
        lblEstadoClientes.setText("Procesando clientes...");
    }

    private void prepararProductos() {
        barraProductos.setProgress(0);
        lblPorcentajeProductos.setText("0% (0/50)");
        lblEstadoProductos.setText("Procesando productos...");
    }

    @FXML
    private void detenerClientes() {
        if (clientesActivo && hiloClientes != null && hiloClientes.isAlive()) {
            btnDetenerClientes.setDisable(true);
            lblEstadoClientes.setText("Solicitando detención...");
            hiloClientes.interrupt();
        }
    }

    @FXML
    private void detenerProductos() {
        if (productosActivo && hiloProductos != null && hiloProductos.isAlive()) {
            btnDetenerProductos.setDisable(true);
            lblEstadoProductos.setText("Solicitando detención...");
            hiloProductos.interrupt();
        }
    }

    private void finalizarClientes(String mensaje) {
        clientesActivo = false;
        lblEstadoClientes.setText(mensaje);
        actualizarBotonesReto();
    }

    private void finalizarProductos(String mensaje) {
        productosActivo = false;
        lblEstadoProductos.setText(mensaje);
        actualizarBotonesReto();
    }

    @FXML
    private void reiniciarReto() {
        if (clientesActivo || productosActivo) {
            return;
        }
        barraClientes.setProgress(0);
        lblPorcentajeClientes.setText("0% (0/100)");
        lblEstadoClientes.setText("Esperando inicio");
        barraProductos.setProgress(0);
        lblPorcentajeProductos.setText("0% (0/50)");
        lblEstadoProductos.setText("Esperando inicio");
        actualizarBotonesReto();
    }

    private void actualizarBotonesReto() {
        boolean hayProcesoActivo = clientesActivo || productosActivo;
        btnSimularReto.setDisable(hayProcesoActivo);
        btnReiniciarReto.setDisable(hayProcesoActivo);
        btnDetenerClientes.setDisable(!clientesActivo);
        btnDetenerProductos.setDisable(!productosActivo);
    }

    public void detenerAlCerrar() {
        if (hiloProceso != null && hiloProceso.isAlive()) {
            hiloProceso.interrupt();
        }
        if (hiloClientes != null && hiloClientes.isAlive()) {
            hiloClientes.interrupt();
        }
        if (hiloProductos != null && hiloProductos.isAlive()) {
            hiloProductos.interrupt();
        }
    }
}
