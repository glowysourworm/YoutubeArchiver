package com.youtubearchiver.youtubearchiver.Controller;

import YoutubeArchiverDatabase.Configuration;
import YoutubeArchiverDatabase.Model.YoutubeArchiverEntityBase;
import com.youtubearchiver.youtubearchiver.Controller.Interface.IYoutubeArchiverController;
import com.youtubearchiver.youtubearchiver.Dialog.Model.GetChannelDialogModel;
import com.youtubearchiver.youtubearchiver.Dialog.Service.DialogService;
import com.youtubearchiver.youtubearchiver.Service.YoutubeDownloaderService;
import com.youtubearchiver.youtubearchiver.Service.YoutubePrimaryEntityService;
import com.youtubearchiver.youtubearchiver.UIModel.EntityTreeCell;
import com.youtubearchiver.youtubearchiver.UIModel.EntityTreeCellData;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.stage.Stage;

import java.util.ArrayList;

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
    protected YoutubePrimaryEntityService youtubePrimaryEntityService;
    protected DialogService dialogService;

    public EntityController()
    {
        this.configuration = null;
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
        this.youtubePrimaryEntityService = new YoutubePrimaryEntityService();
        this.dialogService = new DialogService();

        // -> Initialize
        this.youtubeDownloaderService.Initialize(configuration);
        this.youtubePrimaryEntityService.Initialize(configuration);
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
            var channelId = model.getChannelUrl();
        }
        catch (Exception e)
        {
            throw new IllegalStateException("Unable to show modal dialog", e);
        }

    }
}
