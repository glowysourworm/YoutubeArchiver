package com.youtubearchiver.youtubearchiver;

import YoutubeArchiverDatabase.Configuration;
import YoutubeArchiverDatabase.Model.YoutubeComment;
import YoutubeArchiverDatabase.Utility.UtilityModel.YoutubeVideoDownloaderMetadata;
import YoutubeArchiverDatabase.Utility.YoutubeCommentRunner;
import YoutubeArchiverDatabase.Utility.YoutubeVideoRunner;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

import java.util.ArrayList;
import java.util.List;

public class MainController
{
    // UI Elements (main-view.fxml)
    @FXML private TextField youtubeURLText;
    @FXML private ListView<String> videoListView;
    @FXML private ListView<YoutubeComment> commentListView;
    @FXML private Button getButton;

    // Data Sources
    protected ObservableList<String> videoListSource;
    protected ObservableList<YoutubeComment> commentListSource;

    // Configuration
    protected Configuration configuration;

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
    }

    @FXML
    protected void initialize()
    {
        // Create ObservableList<> implementation from ArrayList<> (assuming their bindings are already implemented)
        this.videoListSource = FXCollections.observableList(new ArrayList<String>());
        this.commentListSource = FXCollections.observableList(new ArrayList<YoutubeComment>());

        // Apply Data Source
        this.videoListView.setItems(this.videoListSource);
        this.commentListView.setItems(this.commentListSource);
    }

    @FXML
    protected void onGetButtonClicked()
    {
        // Run video extraction service
        var videoResult = ExecuteVideoDownload(this.youtubeURLText.getText());

        // Run comment extraction service
        if (videoResult != null)
        {
            // TODO:  Load results into the database

            var commentResult = ExecuteCommentsDownload(videoResult.channel_id, this.youtubeURLText.getText());

            // Populate Result Comments
            if (commentResult != null)
            {
                this.commentListSource.clear();
                this.commentListSource.addAll(commentResult);
            }
        }
    }

    private List<YoutubeComment> ExecuteCommentsDownload(String channelId, String videoId)
    {
        // Run channel extraction service
        try
        {
            var youtubeCommentDownloader = new YoutubeCommentRunner(this.configuration);
            return youtubeCommentDownloader.Run(channelId, videoId);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return null;
    }

    private YoutubeVideoDownloaderMetadata ExecuteVideoDownload(String videoId)
    {
        // Run video extraction service
        try
        {
            var youtubeVideoDownloader = new YoutubeVideoRunner(this.configuration);
            return youtubeVideoDownloader.Run(videoId, true);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return null;
    }
}
