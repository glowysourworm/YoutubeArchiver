package com.youtubearchiver.youtubearchiver.Controller;

import YoutubeArchiverDatabase.Configuration;
import YoutubeArchiverDatabase.IYoutubeArchiverInitializer;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TabPane;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import org.kordamp.ikonli.fontawesome.FontAwesome;
import org.kordamp.ikonli.javafx.FontIcon;

import java.util.Arrays;

public class MainController implements IYoutubeArchiverInitializer
{
    // Configuration
    protected Configuration configuration;

    // UI Elements (main-view.fxml)
    @FXML private SplitPane mainSplitPane;
    @FXML private VBox bottomPane;
    @FXML private AnchorPane outputView;
    @FXML private Button toggleConsoleButton;
    @FXML private ToolBar bottomToolbar;
    @FXML private TabPane mainTabPane;

    // UI Handling
    private boolean showingOutput;
    private double dividerPosition;

    public MainController()
    {
        // Must wait for fxml code to compile (see @FXML initialize)

        this.showingOutput = true;
    }

    /// Sets application configuration - must be done during startup; and before user gets
    /// to the UI.
    /// @param configuration Primary (readonly configuration)
    public void Initialize(final Configuration configuration)
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
        // UI Handling
        this.dividerPosition = 0.0;

        // Split Pane Dividers:  Hide / Show Console requires a listener
        this.mainSplitPane
            .getDividers()
            .forEach(divider ->
        {
            // This was necessary because of the CSS handlers that don't work for locking the divider
            //
            divider.positionProperty()
                    .addListener((observable, oldValue, newValue) ->
                    {
                        // Show
                        if (this.showingOutput)
                            this.mainSplitPane.setDividerPositions(newValue.doubleValue());

                        // Hide (the divider position is probably thought of as UI coordinates)
                        else
                            this.mainSplitPane.setDividerPositions(1);
                    });
        });

        // UI Styling
        var icon = new FontIcon(FontAwesome.TERMINAL);
        icon.setStroke(Color.BLACK);
        icon.setFill(Color.BLACK);

        this.toggleConsoleButton.setGraphic(icon);
    }

    @FXML
    protected void onToggleConsoleClicked()
    {
        var nextDividerPosition = 0.0;

        // -> Hide
        if (this.showingOutput)
        {
            // Removes the console
            this.bottomPane.getChildren().remove(this.outputView);

            // Save divider position
            this.dividerPosition = Arrays.stream(this.mainSplitPane.getDividerPositions()).findFirst().getAsDouble();

            // Lock the divider
            this.showingOutput = !this.showingOutput;

            // Collapse
            nextDividerPosition = 1.0;
        }

        // -> Show
        else
        {
            // Removes the console
            this.bottomPane.getChildren().add(this.outputView);

            // Unlock the divider
            this.showingOutput = !this.showingOutput;

            // Restore
            nextDividerPosition = this.dividerPosition;
        }

        // -> Divider Position (calls handler)
        this.mainSplitPane.setDividerPositions(nextDividerPosition);
    }
}
