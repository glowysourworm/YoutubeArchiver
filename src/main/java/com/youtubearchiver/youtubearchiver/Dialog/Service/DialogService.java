package com.youtubearchiver.youtubearchiver.Dialog.Service;

import YoutubeArchiverDatabase.Configuration;
import YoutubeArchiverDatabase.IYoutubeArchiverInitializer;
import com.youtubearchiver.youtubearchiver.Controller.Interface.IYoutubeArchiverController;
import com.youtubearchiver.youtubearchiver.ControllerFactory;
import com.youtubearchiver.youtubearchiver.Dialog.Controller.DialogControllerBase;
import com.youtubearchiver.youtubearchiver.Dialog.Model.DialogModelBase;
import com.youtubearchiver.youtubearchiver.MainApplication;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicReference;

public class DialogService implements IYoutubeArchiverInitializer
{
    private Configuration configuration;
    private Stage primaryStage;

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

    /// Sets primary stage from the main application
    /// @param primaryStage Primary stage - owner of dialog windows
    public void SetPrimaryStage(Stage primaryStage)
    {
        this.primaryStage = primaryStage;
    }

    /// FXML LOADER PATTERN:  The FXMLLoader does not allow for a controller inheritance
    ///                       pattern
    private <T extends IYoutubeArchiverController> T GetController(Class<?> type)
    {
        var dialogController = new AtomicReference<T>();

        try
        {
            dialogController.set((T)ControllerFactory.CreateController(type));

            return dialogController.get();
        }
        catch (Exception e)
        {
            // This will contain the error detail. We need to throw
            throw new IllegalStateException(e);
        }
    }

    public <T extends IYoutubeArchiverController> T ShowModalDialog(DialogModelBase model) throws IOException
    {
        if (!this.IsInitialized())
            throw new IllegalStateException("Dialog Service must be initialized first.");

        // Procedure:  This is simulating an inheritance pattern for the View/Model/Controller dialog
        //             service.
        //
        //             We have to "trick" the FXMLLoader in order to do this by removing the
        //             fx:controller from the base view (see FXML file); setting the controller
        //             manually from the child class' controller; and then calling load().
        //
        // 1) Create FXMLLoader instances for each view (Parent -> Child) (They must be separate)
        // 2) Set the Child's controller factory (this will be the source of our dialog controller)
        // 3) Child -> FX Initialize
        // 3) Set the Parent's controller after from the now created Child controller
        // 4) Parent -> FX Initialize
        // 5) Set Controller's model, and (child) view (from the child loader), and call InitializeDialog()
        // 6) Add Event listeners to dialog buttons*
        // 7) Finally, set the Stage and show the dialog window as normal
        //
        //
        // NOTE*  The "base" DialogControllerBase doesn't actually operate on the FXML. This is a
        //        serious drawback.. But, this pseudo-pattern can be handled if we wire up the
        //        dialog event buttons here in this method.
        //

        var loader = new FXMLLoader(MainApplication.class.getResource("dialog-view-base.fxml"));
        var loaderChild = new FXMLLoader(MainApplication.class.getResource(model.getViewName()));

        // Dialog Controller:  Must use child controller to get inheritance
        //                     along with the view "inheritance" pattern!
        //
        loaderChild.setControllerFactory(this::GetController);

        var childRoot = (Parent)loaderChild.load();
        var controller = (DialogControllerBase)loaderChild.getController();

        // -> Set Controller (we must override the Parent's controller) (INHERITANCE!)
        loader.setController(controller);

        // FX initialize() (the absence of the fx:controller attribute will make sure we use the one we just set)
        //
        var root = (Parent)loader.load();

        // -> Controller:  Initialization (extra step after FXML initialize)
        //                 SetDialogView (embed the child view into the parent view)
        //
        controller.SetModel(model);
        controller.SetDialogView(childRoot);
        controller.InitializeDialog();

        // Finally, show window
        Stage dialogStage = new Stage();
        dialogStage.setTitle(model.getTitle());
        dialogStage.setScene(new Scene(root));

        // Confirm / Cancel
        var okButton = root.lookup("#OkButton");
        var cancelButton=  root.lookup("#CancelButton");

        okButton.addEventHandler(ActionEvent.ACTION,  event ->
        {
            model.setConfirm(true);
            dialogStage.close();
        });
        cancelButton.addEventHandler(ActionEvent.ACTION,  event ->
        {
            model.setCancel(true);
            dialogStage.close();
        });

        // Block interaction with other windows in the app
        dialogStage.initModality(Modality.APPLICATION_MODAL);
        dialogStage.initOwner(this.primaryStage);
        dialogStage.showAndWait();



        // Controller will give access to data model
        return (T) controller;
    }
}
