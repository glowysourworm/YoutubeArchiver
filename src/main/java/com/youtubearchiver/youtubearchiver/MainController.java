package com.youtubearchiver.youtubearchiver;

import YoutubeArchiverDatabase.Model.YoutubeComment;
import YoutubeArchiverDatabase.Utility.PythonRunner;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

import java.util.ArrayList;

public class MainController {

    // UI Elements (main-view.fxml)
    @FXML private TextField youtubeURLText;
    @FXML private ListView<String> videoListView;
    @FXML private ListView<YoutubeComment> commentListView;
    @FXML private Button getButton;

    // Data Sources
    protected ObservableList<String> videoListSource;
    protected ObservableList<YoutubeComment> commentListSource;

    public MainController()
    {
        // Must wait for fxml code to compile (see @FXML initialize)
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
        var commentService = new PythonRunner();

        // Run comment extraction service
        var result = commentService.ExtractYoutubeComments(this.youtubeURLText.getText());

        // Populate Result Comments
        this.commentListSource.clear();
        this.commentListSource.addAll(result);
    }
}
