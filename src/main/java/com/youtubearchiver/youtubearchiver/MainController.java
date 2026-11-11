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

import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.BooleanPropertyBase;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import org.controlsfx.control.StatusBar;

import java.util.concurrent.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

public class MainController
{
    // UI Elements (main-view.fxml)
    @FXML private TextField youtubeURLText;
    @FXML private Button searchButton;
    @FXML private ListView<String> videoListView;
    @FXML private ListView<YoutubeComment> commentListView;
    @FXML private TextArea outputTextArea;
    @FXML private StatusBar statusBar;

    // Data Sources
    protected ObservableList<String> videoListSource;
    protected ObservableList<YoutubeComment> commentListSource;
    protected int textLineCounter;

    // Configuration
    protected Configuration configuration;

    // Async Blocking
    private SimpleBooleanProperty running;

    public MainController()
    {
        // Must wait for fxml code to compile (see @FXML initialize)

        // Async Blocking:  The long running actions are for the shell commands
        this.running = new SimpleBooleanProperty();
        this.setRunning(false);
    }

    public boolean getRunning()
    {
        return this.running.get();
    }
    public void setRunning(boolean value)
    {
        this.running.set(value);
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

        // Enable / Disable Run Button
        this.searchButton.disableProperty().bind(this.running);

        // Output Log: Append text messages from the log
        EventAggregator.GetEvent(LogMessageEvent.class).Subscribe(message ->
        {
            // o(@.^)@  <-- Java Psycho..  When you "send" a delegate to the main thread it is common
            //                             to add data.. like a real function..! Why is there no parameter
            //                             available?
            Platform.runLater(() ->
            {
                // When was message changed?  There could've been some reference data modified.  Obviously,
                // we can handle that here.. But, why is there no parameter? The Java backend would be
                // dealing with threading issues related to the core.
                //
                // I suspect they did not want to deal with context switching for performance reasons. Both
                // MSFT and Java have never restructured their backends after binding was introduced. They
                // have to learn the hardest part of pri-maddona-ing:
                //
                // Giving up control
                //
                // Let's work together and rebuild C++, using a Java-based compiler toolset, reducing the
                // number of language features, ridding the kernel bugs of the past, and completing an
                // actually modular compiler to perform native compiling.
                //
                // GNU Standards! Meetings are daily at 9am sharp! All in attendance are the team!  ^_^
                //
                appendLogMessage(message);
            });
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
        if (this.textLineCounter++ > 1000)
        {
            var firstLineIndex = this.outputTextArea.getText().indexOf('\n');

            // Remove (top-most) line of text
            this.outputTextArea.deleteText(0, firstLineIndex);
        }

        // -> Autoscroll (to bottom)
        this.outputTextArea.appendText(message + "\n");
        this.statusBar.setText(message);
    }

    @FXML
    protected void onSearchButtonClicked()
    {
        if (this.getRunning())
            return;

        if (this.youtubeURLText.getText().isBlank())
            return;

        // Async Blocker
        this.setRunning(true);

        var videoId = this.youtubeURLText.getText();
        var videoResult = new AtomicReference<YoutubeVideoDownloaderMetadata>();
        var commentResult = new AtomicReference<List<YoutubeComment>>();

        // Run video extraction service
        CompletableFuture.supplyAsync(() ->
        {
            // -> yt-dlp (videoId)
            videoResult.set(ExecuteVideoDownload(videoId));

            // Success!
            if (videoResult.get() != null)
            {
                // TODO:  Load results into the database

                // -> youtube-comment-downloader (channelId, videoId)
                commentResult.set(ExecuteCommentsDownload(videoResult.get().channel_id, videoId));
            }

            return commentResult.get() != null;

        }).thenAccept((result) ->
        {
            // Populate Result Comments
            if (result)
            {
                Platform.runLater(() ->
                {
                    this.commentListSource.clear();
                    this.commentListSource.addAll(commentResult.get());

                    this.setRunning(false);
                });
            }

            else
            {
                this.setRunning(false);
            }
        });
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
