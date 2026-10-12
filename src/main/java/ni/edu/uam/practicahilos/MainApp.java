package ni.edu.uam.practicahilos;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class MainApp extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader(
                MainApp.class.getResource("procesador-view.fxml")
        );
        Scene scene = new Scene(loader.load());

        stage.setTitle("Práctica - Hilos en JavaFX");
        stage.setMinWidth(720);
        stage.setMinHeight(540);
        stage.setScene(scene);
        stage.show();

        ProcesadorController controller = loader.getController();
        stage.setOnCloseRequest(event -> controller.detenerAlCerrar());
    }

    public static void main(String[] args) {
        launch(args);
    }
}
