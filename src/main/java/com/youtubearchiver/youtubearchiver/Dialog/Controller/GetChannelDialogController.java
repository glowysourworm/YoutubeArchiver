package com.youtubearchiver.youtubearchiver.Dialog.Controller;

import YoutubeArchiverDatabase.Configuration;
import com.youtubearchiver.youtubearchiver.Dialog.Model.GetChannelDialogModel;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class GetChannelDialogController extends DialogControllerBase
{
    // Primary Stage:  For child dialog windows
    private Stage primaryStage;

    @FXML private TextField textInput;

    public GetChannelDialogController()
    {
    }

    @FXML
    public void initialize()
    {
        // UI-Only Code here!  Accessing the model won't work until it's been initialized.
    }

    /// Initializes the component with the application configuration
    ///
    /// @param configuration Application configuration
    @Override
    public void Initialize(Configuration configuration)
    {

    }

    /// Dialog requires the model - which is not ready until initialization is complete for the FX stack
    @Override
    public void InitializeDialog()
    {
        super.InitializeDialog();

        // Our model is now ready
        var model = (GetChannelDialogModel)this.GetModel();

        // UI Bindings
        this.textInput.textProperty().bindBidirectional(model.ChannelUrl);
    }

    /// Returns true if the component has already been initialized
    ///
    /// @return true / false indicating initialization
    @Override
    public boolean IsInitialized()
    {
        return true;
    }

    /// Sets primary stage from the main application
    ///
    /// @param primaryStage Primary stage - owner of dialog windows
    @Override
    public void SetPrimaryStage(Stage primaryStage)
    {
        this.primaryStage = primaryStage;
    }
}
