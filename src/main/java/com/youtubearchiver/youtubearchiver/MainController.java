package com.youtubearchiver.youtubearchiver;

import YoutubeArchiverDatabase.Configuration;
import YoutubeArchiverDatabase.Event.EventAggregator;
import YoutubeArchiverDatabase.Event.LogMessageEvent;
import YoutubeArchiverDatabase.Model.YoutubeComment;
import YoutubeArchiverDatabase.Model.YoutubeVideo;
import YoutubeArchiverDatabase.Utility.ApplicationHelpers;
import YoutubeArchiverDatabase.Utility.UtilityModel.YoutubeDownloader.Comments;
import YoutubeArchiverDatabase.Utility.UtilityModel.YoutubeDownloader.Subtitles;
import YoutubeArchiverDatabase.Utility.UtilityModel.YoutubeDownloader.YoutubeDownloaderMetadata;
import YoutubeArchiverDatabase.Utility.UtilityModel.YoutubeDownloader.YoutubeDownloaderRunType;
import com.youtubearchiver.youtubearchiver.DataModel.YoutubeDownloaderRequest;
import com.youtubearchiver.youtubearchiver.DataModel.YoutubeDownloaderResponse;
import com.youtubearchiver.youtubearchiver.Event.*;
import com.youtubearchiver.youtubearchiver.Service.YoutubeChannelService;
import com.youtubearchiver.youtubearchiver.UIModel.YoutubeFunctionSelectionConverter;
import com.youtubearchiver.youtubearchiver.UIModel.YoutubeVideoListCell;
import com.youtubearchiver.youtubearchiver.Utility.FXApplicationHelpers;
import javafx.application.Platform;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.controlsfx.control.StatusBar;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class MainController
{
    // UI Elements (main-view.fxml)
    @FXML private TextField youtubeURLText;
    @FXML private Button executeButton;
    @FXML private Label youtubeFunctionLabel;
    @FXML private ListView<YoutubeVideo> videoListView;
    @FXML private ListView<YoutubeComment> commentListView;
    @FXML private ComboBox<YoutubeDownloaderRunType> youtubeFunctionComboBox;
    @FXML private TextArea outputTextArea;
    @FXML private StatusBar statusBar;

    // Constants
    private final String YOUTUBE_VIDEO_URL = "Youtube Video Url / Id";
    private final String YOUTUBE_CHANNEL_URL = "Youtube Channel Url / Id";

    // Data Sources
    protected ObservableList<YoutubeVideo> videoListSource;
    protected ObservableList<YoutubeComment> commentListSource;
    protected ObservableList<YoutubeDownloaderRunType> youtubeFunctions;
    protected SimpleObjectProperty<YoutubeDownloaderRunType> youtubeFunctionSelected;
    protected int textLineCounter;

    // Configuration
    protected Configuration configuration;

    // Services
    protected final YoutubeChannelService mainService;

    // Async Blocking
    private final SimpleBooleanProperty running;

    // UI Properties (Data)
    protected final SimpleStringProperty youtubeFunctionText;

    public MainController()
    {
        // Must wait for fxml code to compile (see @FXML initialize)

        // Services
        this.mainService = new YoutubeChannelService();

        // Async Blocking:  The long running actions are for the shell commands
        this.running = new SimpleBooleanProperty();
        this.setRunning(false);

        this.youtubeFunctionText = new SimpleStringProperty(YOUTUBE_VIDEO_URL);
        this.youtubeFunctionSelected = new SimpleObjectProperty<>();
    }

    public boolean getRunning()
    {
        return this.running.get();
    }
    public void setRunning(boolean value)
    {
        this.running.set(value);
    }

    public String getYoutubeFunctionText()
    {
        return this.youtubeFunctionText.get();
    }
    public void setYoutubeFunctionText(String value)
    {
        this.youtubeFunctionText.set(value);
    }

    /// Sets application configuration - must be done during startup; and before user gets
    /// to the UI.
    /// @param configuration Primary (readonly configuration)
    public void setConfiguration(final Configuration configuration)
    {
        if (this.running.get())
            throw new IllegalStateException("Cannot set configuration while running service");

        this.configuration = configuration;

        // Services (Initializable Components)
        this.mainService.Initialize(this.configuration);
    }

    @FXML
    protected void initialize()
    {
        // Create ObservableList<> implementation from ArrayList<> (assuming their bindings are already implemented)
        this.videoListSource = FXCollections.observableList(new ArrayList<YoutubeVideo>());
        this.commentListSource = FXCollections.observableList(new ArrayList<YoutubeComment>());
        this.youtubeFunctions = FXCollections.observableList(Arrays.stream(YoutubeDownloaderRunType.values().clone()).toList());
        this.textLineCounter = 0;

        // Apply Data Source
        this.youtubeFunctionLabel.textProperty().bindBidirectional(this.youtubeFunctionText);
        this.videoListView.setItems(this.videoListSource);
        this.commentListView.setItems(this.commentListSource);
        this.youtubeFunctionComboBox.setItems(this.youtubeFunctions);
        this.youtubeFunctionComboBox.setConverter(new YoutubeFunctionSelectionConverter());
        this.youtubeFunctionComboBox.valueProperty().bindBidirectional(this.youtubeFunctionSelected);

        // UI Listeners
        this.youtubeFunctionComboBox.valueProperty().addListener((observable,
                                                                  oldValue,
                                                                  newValue) -> {

            if (newValue == null)
            {
                this.youtubeFunctionText.set(YOUTUBE_VIDEO_URL);
                return;
            }
            switch (newValue)
            {
                case VideoMetadata:
                case VideoComments:
                case VideoDescription:
                case VideoSubtitleList:
                case VideoDownload:
                case VideoAudioDownload:
                    this.youtubeFunctionText.set(YOUTUBE_VIDEO_URL);
                    break;
                case ChannelVideoList:
                    this.youtubeFunctionText.set(YOUTUBE_CHANNEL_URL);
                    break;
                default:
                    throw new EnumConstantNotPresentException(YoutubeDownloaderRunType.class, newValue.toString());
            }
        });

        // Custom ListView Cells
        this.videoListView.setCellFactory((listView) -> new YoutubeVideoListCell());

        // Enable / Disable Run Button
        this.executeButton.disableProperty().bind(this.running);

        // Youtube Downloader Events: Route the downloader events back to this class' handler
        EventAggregator.GetEvent(YoutubeAudioDownloadCompleteEvent.class)
                       .Subscribe(response ->
        {
            // -> Main Thread
            FXApplicationHelpers.EnsureMainThread(this::OnAudioDownloaded, response);
        });
        EventAggregator.GetEvent(YoutubeChannelVideoListCompleteEvent.class)
                       .Subscribe(response ->
        {
            // -> Main Thread
            FXApplicationHelpers.EnsureMainThread(this::OnChannelVideoListDownloaded, response);
        });
        EventAggregator.GetEvent(YoutubeVideoCommentsCompleteEvent.class)
                       .Subscribe(response ->
        {
            // -> Main Thread
            FXApplicationHelpers.EnsureMainThread(this::OnVideoCommentsDownloaded, response);
        });
        EventAggregator.GetEvent(YoutubeVideoDescriptionCompleteEvent.class)
                       .Subscribe(response ->
        {
            // -> Main Thread
            FXApplicationHelpers.EnsureMainThread(this::OnVideoDescriptionDownloaded, response);
        });
        EventAggregator.GetEvent(YoutubeVideoMetadataCompleteEvent.class)
                       .Subscribe(response ->
        {
            // -> Main Thread
            FXApplicationHelpers.EnsureMainThread(this::OnVideoMetadataDownloaded, response);
        });
        EventAggregator.GetEvent(YoutubeVideoSubtitlesCompleteEvent.class)
                       .Subscribe(response ->
        {
            // -> Main Thread
            FXApplicationHelpers.EnsureMainThread(this::OnVideoSubtitlesDownloaded, response);
        });

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
    protected void onYoutubeExecuteClicked()
    {
        if (this.getRunning())
            return;

        if (this.youtubeURLText.getText().isBlank())
            return;

        if (this.youtubeFunctionSelected.get() == null)
            return;

        // Async Blocker
        this.setRunning(true);

        // -> Async Run (see EventAggregator subscription)
        CompletableFuture.runAsync(() -> {
           this.mainService.ExecuteRequest(CreateYoutubeDownloaderRequest());
        });
    }
    private <T> boolean HandleCompletedServiceExecution(YoutubeDownloaderResponse<T> response) throws IllegalStateException
    {
        // Async Blocker
        if (!this.getRunning())
            throw new IllegalStateException("Expected running state after youtube downloader execution");

        // Unblock
        this.setRunning(false);

        // Check Success / Failure
        if (!response.result)
            return false;

        return true;
    }
    private void OnAudioDownloaded(YoutubeDownloaderResponse<Path> response)
    {
        if (!HandleCompletedServiceExecution(response))
            return;


    }
    private void OnVideoSubtitlesDownloaded(YoutubeDownloaderResponse<List<Subtitles>> response)
    {
        if (!HandleCompletedServiceExecution(response))
            return;
    }
    private void OnVideoMetadataDownloaded(YoutubeDownloaderResponse<YoutubeDownloaderMetadata> response)
    {
        if (!HandleCompletedServiceExecution(response))
            return;
    }
    private void OnVideoDescriptionDownloaded(YoutubeDownloaderResponse<String> response)
    {
        if (!HandleCompletedServiceExecution(response))
            return;
    }
    private void OnVideoCommentsDownloaded(YoutubeDownloaderResponse<List<Comments>> response)
    {
        if (!HandleCompletedServiceExecution(response))
            return;
    }
    private void OnChannelVideoListDownloaded(YoutubeDownloaderResponse<List<String>> response)
    {
        if (!HandleCompletedServiceExecution(response))
            return;
    }

    protected YoutubeDownloaderRequest CreateYoutubeDownloaderRequest()
    {
        var request = new YoutubeDownloaderRequest();
        var urlOrId = this.youtubeURLText.getText();

        // Dual use text box (just to show.. this will likely get expanded)
        request.videoId = this.youtubeFunctionSelected.get() == YoutubeDownloaderRunType.ChannelVideoList ? urlOrId : urlOrId;
        request.channelId = this.youtubeFunctionSelected.get() == YoutubeDownloaderRunType.ChannelVideoList ? urlOrId : urlOrId;
        request.runType = this.youtubeFunctionSelected.get();

        return request;
    }
}
