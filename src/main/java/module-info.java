module com.example.coursework {
    requires javafx.controls;
    requires javafx.fxml;

    requires com.dlsc.formsfx;
    requires com.almasb.fxgl.all;

    opens com.example.coursework to javafx.fxml;
//    exports com.example.coursework.server;
    exports com.example.coursework;
    opens com.example.coursework.images to javafx.fxml;


}