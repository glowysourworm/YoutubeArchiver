package com.youtubearchiver.youtubearchiver;

import YoutubeArchiverDatabase.Configuration;
import YoutubeArchiverDatabase.Model.YoutubeArchiverEntityBase;
import com.youtubearchiver.youtubearchiver.Service.YoutubePrimaryEntityService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;

import java.util.ArrayList;

public class MainController
{
    // UI Elements (main-view.fxml)
    @FXML private ListView<YoutubeArchiverEntityBase> entityListView;

    // Data Sources
    protected ObservableList<YoutubeArchiverEntityBase> entityListSource;

    // Configuration
    protected Configuration configuration;

    // Services
    protected YoutubePrimaryEntityService entityService;

    public MainController()
    {
        // Must wait for fxml code to compile (see @FXML initialize)
    }

    /// Sets application configuration - must be done during startup; and before user gets
    /// to the UI.
    /// @param configuration Primary (readonly configuration)
    public void setConfiguration(final Configuration configuration)
    {
        this.configuration = configuration;

        // Services (Initializable Components)
        this.entityService = new YoutubePrimaryEntityService(this.configuration);
    }

    @FXML
    protected void initialize()
    {
        // Create ObservableList<> implementation from ArrayList<> (assuming their bindings are already implemented)
        this.entityListSource = FXCollections.observableList(new ArrayList<YoutubeArchiverEntityBase>());

        // Apply Data Source
        this.entityListView.setItems(this.entityListSource);

        // Youtube Downloader Events: Route the downloader events back to this class' handler
        //
    }
}
