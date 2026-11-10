module com.youtubearchiver.youtubearchiver {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires org.kordamp.ikonli.javafx;
    requires YoutubeArchiverDatabase;
    requires jdk.compiler;
    requires jakarta.persistence;
    requires org.hibernate.orm.core;
    requires tools.jackson.databind;

    opens com.youtubearchiver.youtubearchiver to javafx.fxml;
    exports com.youtubearchiver.youtubearchiver;
}