package com.youtubearchiver.youtubearchiver.Controller;

import YoutubeArchiverDatabase.Configuration;
import YoutubeArchiverDatabase.Event.EventAggregator;
import YoutubeArchiverDatabase.Logging.LogMessage;
import YoutubeArchiverDatabase.Model.YoutubeArchiverEntityBase;
import YoutubeArchiverDatabase.Service.YoutubeArchiverDatabaseConnection;
import YoutubeArchiverDatabase.Service.YoutubeEntityMapper;
import YoutubeArchiverDatabase.Utility.ApplicationHelpers;
import YoutubeArchiverDatabase.Utility.UtilityModel.YoutubeDownloader.YoutubeDownloaderMetadata;
import YoutubeArchiverDatabase.Utility.UtilityModel.YoutubeDownloader.YoutubeDownloaderRunType;
import com.youtubearchiver.youtubearchiver.Controller.Interface.IYoutubeArchiverController;
import com.youtubearchiver.youtubearchiver.DataModel.YoutubeDownloaderRequest;
import com.youtubearchiver.youtubearchiver.DataModel.YoutubeDownloaderResponse;
import com.youtubearchiver.youtubearchiver.Dialog.Model.GetChannelDialogModel;
import com.youtubearchiver.youtubearchiver.Dialog.Service.DialogService;
import com.youtubearchiver.youtubearchiver.Event.YoutubeChannelVideoListCompleteEvent;
import com.youtubearchiver.youtubearchiver.Event.YoutubeVideoMetadataCompleteEvent;
import com.youtubearchiver.youtubearchiver.Service.YoutubeDownloaderService;
import com.youtubearchiver.youtubearchiver.UIModel.EntityTreeCell;
import com.youtubearchiver.youtubearchiver.UIModel.EntityTreeCellData;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

public class EntityController implements IYoutubeArchiverController
{
    // Configuration
    private Configuration configuration;

    // UI Elements
    @FXML protected TreeView<EntityTreeCellData> entityTreeView;
    @FXML private ListView<YoutubeArchiverEntityBase> entityListView;

    // Tree Items
    TreeItem<EntityTreeCellData> channelsItem = new TreeItem<>(new EntityTreeCellData("Channels"));
    TreeItem<EntityTreeCellData> videosItem = new TreeItem<>(new EntityTreeCellData("Videos"));
    TreeItem<EntityTreeCellData> usersItem = new TreeItem<>(new EntityTreeCellData("Users"));
    TreeItem<EntityTreeCellData> commentsItem = new TreeItem<>(new EntityTreeCellData("Comments"));
    TreeItem<EntityTreeCellData> commentHistoryItem = new TreeItem<>(new EntityTreeCellData("Comments (history)"));
    TreeItem<EntityTreeCellData> localCommentsItem = new TreeItem<>(new EntityTreeCellData("Comments (local)"));

    // Data Sources
    protected ObservableList<YoutubeArchiverEntityBase> entityListSource;

    // Services
    protected YoutubeDownloaderService youtubeDownloaderService;
    protected DialogService dialogService;

    public EntityController()
    {
        this.configuration = null;

        // Services:  We're going to perform some basic yt-dlp requests
        //
        // Event Subscriptions:  YoutubeChannelVideoListCompleteEvent, YoutubeVideoMetadataCompleteEvent

        EventAggregator.GetEvent(YoutubeChannelVideoListCompleteEvent.class)
                       .Subscribe((response) ->
                       {
                           // Main Thread
                           Platform.runLater(() -> OnYoutubeChannelListDownloadComplete(response));
                       });

        EventAggregator.GetEvent(YoutubeVideoMetadataCompleteEvent.class)
                       .Subscribe(response ->
                       {
                           // Main Thread
                           Platform.runLater(() -> OnYoutubeVideoMetadataComplete(response));
                       });
    }

    /// Initializes the component with the application configuration
    ///
    /// @param configuration Application configuration
    @Override
    public void Initialize(Configuration configuration)
    {
        this.configuration = configuration;

        // Services
        this.youtubeDownloaderService = new YoutubeDownloaderService();
        this.dialogService = new DialogService();

        // -> Initialize
        this.youtubeDownloaderService.Initialize(configuration);
        this.dialogService.Initialize(configuration);
    }

    /// Returns true if the component has already been initialized
    ///
    /// @return true / false indicating initialization
    @Override
    public boolean IsInitialized()
    {
        return this.configuration != null;
    }

    // UI Initialization
    @FXML
    protected void initialize()
    {
        // Create ObservableList<> implementation from ArrayList<> (assuming their bindings are already implemented)
        this.entityListSource = FXCollections.observableList(new ArrayList<YoutubeArchiverEntityBase>());

        // Apply Data Source
        this.entityListView.setItems(this.entityListSource);

        // Cell Factory
        this.entityTreeView.setCellFactory(TreeView -> new EntityTreeCell());

        // Youtube Archiver: Channels, Videos, ...
        //
        var root = new TreeItem<>(new  EntityTreeCellData("Youtube Archiver"));

        root.getChildren().add(this.channelsItem);
        root.getChildren().add(this.videosItem);
        root.getChildren().add(this.usersItem);
        root.getChildren().add(this.commentsItem);
        root.getChildren().add(this.commentHistoryItem);
        root.getChildren().add(this.localCommentsItem);

        this.entityTreeView.setRoot(root);
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
        this.dialogService.SetPrimaryStage(primaryStage);
    }

    public void getMyChannelsClicked(ActionEvent actionEvent)
    {
        // -> Google API (get my subscriptions)
    }

    public void getChannelClicked(ActionEvent actionEvent) throws IllegalStateException
    {
        try
        {
            // Model
            var model = new GetChannelDialogModel();

            // Show Modal
            this.dialogService.ShowModalDialog(model);

            // -> Result
            if (model.getConfirm())
            {
                var channelId = model.getChannelUrl();

                // -> yt-dlp (get channel video list)
                var request = new YoutubeDownloaderRequest();
                request.runType = YoutubeDownloaderRunType.ChannelVideoList;
                request.channelId = channelId;

                // -> Async Request
                this.youtubeDownloaderService.ExecuteRequestAsync(request);
            }
        }
        catch (Exception e)
        {
            throw new IllegalStateException("Unable to show modal dialog", e);
        }
    }

    // Youtube Channel Video List -> (Add/Update YoutubeChannel, get video metadata)
    private void OnYoutubeChannelListDownloadComplete(YoutubeDownloaderResponse<List<String>> response)
    {
        // Request Failed
        if (!response.result)
            return;

        for (var videoId : response.payload)
        {
            var request = new YoutubeDownloaderRequest();
            request.videoId = videoId;
            request.runType = YoutubeDownloaderRunType.VideoMetadata;

            // -> Async Request (Video Metadata)
            this.youtubeDownloaderService.ExecuteRequestAsync(request);
        }
    }

    // Youtube Video Metadata -> (Add/Update YoutubeChannel) (Add/Update YoutubeVideo)
    private void OnYoutubeVideoMetadataComplete(YoutubeDownloaderResponse<YoutubeDownloaderMetadata> response)
    {
        // Request Failed
        if (!response.result)
            return;

        try(var connection = new YoutubeArchiverDatabaseConnection(this.configuration))
        {
            var youtubeChannel = connection.GetChannel(response.payload.channelId);
            var youtubeVideo = connection.GetVideo(response.payload.id);

            // YoutubeChannel: Add (only)
            if (youtubeChannel == null)
            {
                youtubeChannel = YoutubeEntityMapper.CreateNew(response.payload.channelId, false);
                connection.AddUpdate(youtubeChannel);
            }

            // YoutubeVideo: Add / Update
            if (youtubeVideo == null)
                youtubeVideo = YoutubeEntityMapper.Map(youtubeChannel, response.payload);

            connection.AddUpdate(youtubeVideo);
        }
        catch ( Exception e)
        {
            ApplicationHelpers.Log(LogMessage.Error("Error updating Youtube Archiver Database:  " + e.getMessage(), e));
        }
    }
}
