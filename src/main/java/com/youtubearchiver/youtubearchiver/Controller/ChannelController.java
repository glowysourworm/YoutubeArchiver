package com.youtubearchiver.youtubearchiver.Controller;

import YoutubeArchiverDatabase.Configuration;
import YoutubeArchiverDatabase.Model.YoutubeVideo;
import YoutubeArchiverDatabase.IYoutubeArchiverInitializer;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;

public class ChannelController implements IYoutubeArchiverInitializer
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
}
