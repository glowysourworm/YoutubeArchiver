package com.youtubearchiver.youtubearchiver.Controller.Interface;

import YoutubeArchiverDatabase.IYoutubeArchiverInitializer;
import javafx.stage.Stage;

public interface IYoutubeArchiverController extends IYoutubeArchiverInitializer
{
    /// Returns model object for this controller. This would be the root object needed by the
    /// view. (TODO: Think of a way to handle templates for the model)
    /// @return The object (root) of the view's model
    Object GetModel();

    /// Sets primary stage from the main application
    /// @param primaryStage Primary stage - owner of dialog windows
    void SetPrimaryStage(Stage primaryStage);
}
