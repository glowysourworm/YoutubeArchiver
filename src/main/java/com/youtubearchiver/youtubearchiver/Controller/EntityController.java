package com.youtubearchiver.youtubearchiver.Controller;

import com.youtubearchiver.youtubearchiver.UIModel.EntityTreeCell;
import com.youtubearchiver.youtubearchiver.UIModel.EntityTreeCellData;
import javafx.fxml.FXML;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;

public class EntityController
{
    @FXML protected TreeView<EntityTreeCellData> entityTreeView;

    // Tree Items
    TreeItem<EntityTreeCellData> channelsItem = new TreeItem<>(new EntityTreeCellData("Channels"));
    TreeItem<EntityTreeCellData> videosItem = new TreeItem<>(new EntityTreeCellData("Videos"));
    TreeItem<EntityTreeCellData> usersItem = new TreeItem<>(new EntityTreeCellData("Users"));
    TreeItem<EntityTreeCellData> commentsItem = new TreeItem<>(new EntityTreeCellData("Comments"));
    TreeItem<EntityTreeCellData> commentHistoryItem = new TreeItem<>(new EntityTreeCellData("Comments (history)"));
    TreeItem<EntityTreeCellData> localCommentsItem = new TreeItem<>(new EntityTreeCellData("Comments (local)"));

    public EntityController()
    {

    }

    @FXML
    protected void initialize()
    {
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
}
