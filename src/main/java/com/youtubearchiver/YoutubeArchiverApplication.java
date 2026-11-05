package com.youtubearchiver;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class YoutubeArchiverApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(YoutubeArchiverApplication.class.getResource("main-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 460, 240);
        stage.setTitle("Youtube Archiver");
        stage.setScene(scene);
        stage.show();
    }
}
