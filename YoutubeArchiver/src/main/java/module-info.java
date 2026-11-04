module com.youtubearchiver.youtubearchiver {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires org.kordamp.ikonli.javafx;

    opens com.youtubearchiver.youtubearchiver to javafx.fxml;
    exports com.youtubearchiver.youtubearchiver;
}