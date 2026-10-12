module ni.edu.uam.practicahilos {
    requires javafx.controls;
    requires javafx.fxml;

    exports ni.edu.uam.practicahilos;
    opens ni.edu.uam.practicahilos to javafx.fxml;
}
