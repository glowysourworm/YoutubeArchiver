module com.youtubearchiver.youtubearchiver
{
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires org.kordamp.ikonli.javafx;
    requires YoutubeArchiverDatabase;
    requires jdk.compiler;
    requires jakarta.persistence;
    requires org.hibernate.orm.core;
    requires tools.jackson.databind;
    requires org.apache.httpcomponents.httpcore;
    requires jsr305;
    requires com.google.api.services.youtube;
    requires java.naming;
    requires org.kordamp.ikonli.core;
    requires org.kordamp.ikonli.fontawesome;
    requires java.management;

    opens com.youtubearchiver.youtubearchiver to javafx.fxml;
    opens com.youtubearchiver.youtubearchiver.Controller to javafx.fxml;

    exports com.youtubearchiver.youtubearchiver;
    exports com.youtubearchiver.youtubearchiver.Controller;
    exports com.youtubearchiver.youtubearchiver.DataModel;
    exports com.youtubearchiver.youtubearchiver.Event;
    exports com.youtubearchiver.youtubearchiver.Service;
    exports com.youtubearchiver.youtubearchiver.UIModel;
    exports com.youtubearchiver.youtubearchiver.Utility;
    exports com.youtubearchiver.youtubearchiver.Dialog.Service;
    exports com.youtubearchiver.youtubearchiver.Dialog.Model;
    exports com.youtubearchiver.youtubearchiver.Dialog.Controller;
    opens com.youtubearchiver.youtubearchiver.Dialog.Controller to javafx.fxml;
    exports com.youtubearchiver.youtubearchiver.Controller.Interface;
    opens com.youtubearchiver.youtubearchiver.Controller.Interface to javafx.fxml;
}