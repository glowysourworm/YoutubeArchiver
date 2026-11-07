package com.youtubearchiver.youtubearchiver;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class MainApplication extends Application {

    private Scene mainScene;

    @Override
    public void start(Stage mainStage) throws IOException
    {
        FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("main-view.fxml"));

        mainScene = new Scene(fxmlLoader.load(), 800, 600);
        mainStage.setTitle("Main View!");
        mainStage.setScene(mainScene);
        mainStage.setMinWidth(600);
        mainStage.setMinHeight(400);
        mainStage.show();
    }
}
