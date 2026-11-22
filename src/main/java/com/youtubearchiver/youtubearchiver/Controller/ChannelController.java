package com.youtubearchiver.youtubearchiver.Controller;

import YoutubeArchiverDatabase.Configuration;
import YoutubeArchiverDatabase.Model.YoutubeVideo;
import com.youtubearchiver.youtubearchiver.Controller.Interface.IYoutubeArchiverController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.stage.Stage;

public class ChannelController implements IYoutubeArchiverController
{
    @FXML ListView<YoutubeVideo> videoListView;

    ObservableList<YoutubeVideo> videoListSource;

    private Configuration configuration;
    private YoutubeVideo selectedVideo;

    public ChannelController()
    {

    }

    @FXML
    private void initialize()
    {
        // Data Sources
        this.selectedVideo = null;
        this.videoListSource = FXCollections.observableArrayList();

        // UI Listeners
        this.videoListView
            .getSelectionModel()
            .selectedItemProperty()
            .addListener((observable, oldValue, newValue) ->
        {
            this.selectedVideo = newValue;
        });


    }

    /// Initializes the component with the application configuration
    ///
    /// @param configuration Application configuration
    @Override
    public void Initialize(Configuration configuration)
    {
        this.configuration = configuration;
    }

    /// Returns true if the component has already been initialized
    ///
    /// @return true / false indicating initialization
    @Override
    public boolean IsInitialized()
    {
        return this.configuration != null;
    }

    /// Returns model object for this controller. This would be the root object needed by the
    /// view. (TODO: Think of a way to handle templates for the model)
    ///
    /// @return The object (root) of the view's model
    @Override
    public Object GetModel()
    {
        return null;
    }

    /// Sets primary stage from the main application
    ///
    /// @param primaryStage Primary stage - owner of dialog windows
    @Override
    public void SetPrimaryStage(Stage primaryStage)
    {

    }
}
