package com.youtubearchiver.youtubearchiver.Dialog.Controller;

import YoutubeArchiverDatabase.Configuration;
import com.youtubearchiver.youtubearchiver.Controller.Interface.IYoutubeArchiverController;
import com.youtubearchiver.youtubearchiver.Dialog.Model.DialogModelBase;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class DialogControllerBase implements IYoutubeArchiverController
{
    // Application Configuration
    private Configuration configuration;

    // Dialog Data Model (for all controllers derived from DialogControllerBase)
    private DialogModelBase dialogModel;

    @FXML AnchorPane childContainer;
    @FXML Text descriptionText;

    @FXML
    public void initialize()
    {

    }

    /// Sets the view content for the dialog. It is advised that you set AnchorPane properties
    /// for the child view so it takes up the space of the parent (AnchorPane).
    /// @param fxNode Any view whose controller inherits from DialogControllerBase
    public void SetDialogView(Node fxNode)
    {
        this.childContainer.getChildren().clear();
        this.childContainer.getChildren().add(fxNode);
    }

    /// Returns model object for this controller. This would be the root object needed by the
    /// view. (Dialog Controllers:  (for all controllers derived from DialogControllerBase))
    ///
    /// @return The object (root) of the view's model
    @Override
    public Object GetModel()
    {
        return this.dialogModel;
    }

    /// Dialog Controllers Only:  Sets the data model for the controller (for all controllers derived from DialogControllerBase)
    public void SetModel(DialogModelBase model)
    {
        this.dialogModel = model;
    }

    /// Sets primary stage from the main application
    ///
    /// @param primaryStage Primary stage - owner of dialog windows
    @Override
    public void SetPrimaryStage(Stage primaryStage)
    {
        throw new UnsupportedOperationException("Must inherit and use SetPrimaryStage method");
    }

    /// Extra initialization step for dialog controllers
    public void InitializeDialog()
    {
        this.descriptionText.setText(this.dialogModel.getDescription());
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
}
