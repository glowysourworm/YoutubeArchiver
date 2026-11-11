package com.youtubearchiver.youtubearchiver;

import YoutubeArchiverDatabase.Configuration;
import YoutubeArchiverDatabase.Event.EventAggregator;
import YoutubeArchiverDatabase.Event.LogMessageEvent;
import YoutubeArchiverDatabase.Logging.LogMessage;
import YoutubeArchiverDatabase.Model.YoutubeComment;
import YoutubeArchiverDatabase.Logging.SimpleLogger;
import YoutubeArchiverDatabase.Utility.ApplicationHelpers;
import YoutubeArchiverDatabase.Utility.UtilityModel.YoutubeVideoDownloaderMetadata;
import YoutubeArchiverDatabase.Utility.YoutubeCommentRunner;
import YoutubeArchiverDatabase.Utility.YoutubeVideoRunner;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
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
    @FXML private TextArea outputTextArea;

    // Data Sources
    protected ObservableList<String> videoListSource;
    protected ObservableList<YoutubeComment> commentListSource;
    protected int textLineCounter;

    // Configuration
    protected Configuration configuration;

    // UI Log Appender (needed to access initial log)
    protected SimpleLogger logAppender;

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
        this.textLineCounter = 0;

        // Apply Data Source
        this.videoListView.setItems(this.videoListSource);
        this.commentListView.setItems(this.commentListSource);

        // Output Log: Append text messages from the log
        EventAggregator.GetEvent(LogMessageEvent.class).Subscribe(message ->
        {
            appendLogMessage(message);
        });

        // Initialize Log:  There are already log messages on the output
        ApplicationHelpers.GetLogs().forEach(message ->
        {
            appendLogMessage(ApplicationHelpers.GetLogger().FormatMessage(message));
        });
    }

    private void appendLogMessage(String message)
    {
        // Remove oldest line of text
        if (this.textLineCounter > 1000)
        {
            var firstLineIndex = this.outputTextArea.getText().indexOf('\n');

            // Remove (top-most) line of text
            this.outputTextArea.deleteText(0, firstLineIndex);
        }

        // -> Autoscroll (to bottom)
        this.outputTextArea.appendText(message + "\n");
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
            ApplicationHelpers.Log(LogMessage.Error("Error executing comment downloader", e));
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
            ApplicationHelpers.Log(LogMessage.Error("Error executing video downloader", e));
        }

        return null;
    }
}
