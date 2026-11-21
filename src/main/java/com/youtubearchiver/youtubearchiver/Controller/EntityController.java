package com.youtubearchiver.youtubearchiver.Controller;

import YoutubeArchiverDatabase.Configuration;
import YoutubeArchiverDatabase.Model.YoutubeArchiverEntityBase;
import YoutubeArchiverDatabase.IYoutubeArchiverInitializer;
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

import java.util.ArrayList;

public class EntityController implements IYoutubeArchiverInitializer
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
    }

    /// Returns true if the component has already been initialized
    ///
    /// @return true / false indicating initialization
    @Override
    public boolean IsInitialized()
    {
        return this.configuration != null;
    }

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

    public void getMyChannelsClicked(ActionEvent actionEvent)
    {
        // -> Google API (get my subscriptions)
    }

    public void getChannelClicked(ActionEvent actionEvent)
    {

    }
}
