package com.youtubearchiver.youtubearchiver;

import YoutubeArchiverDatabase.Configuration;
import YoutubeArchiverDatabase.Utility.ApplicationHelpers;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import org.hibernate.cfg.Environment;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class MainApplication extends Application 
{
    // ENVIRONMENT VARIABLE(S)
    public final static String PYTHON_SCRIPT_FOLDER_KEY = "PYTHON_SCRIPT_FOLDER";

    /// Primary configuration file for the application (user-end)
    private final static String CONFIGURATION_FILE = "youtube-archiver.json";
    private final static String CONFIGURATION_YOUTUBE_DATA_DIR_DEFAULT = "youtube-data";

    /// Primary (user-end) configuration for the application
    private Configuration configuration;
    
    // Main Scene (passed in at the "start" method)
    private Scene mainScene;

    private boolean initializationError;

    @Override
    public void init() throws Exception
    {
        super.init();

        this.initializationError = false;

        // Environment Variable(s) Check
        try
        {
            var scriptFolder = System.getenv(PYTHON_SCRIPT_FOLDER_KEY);

            if (!Files.exists(Path.of(scriptFolder)))
                throw new Exception("Missing / Invalid Environment Variable:  " + PYTHON_SCRIPT_FOLDER_KEY);
        }
        catch (Exception e)
        {
            this.initializationError = true;

            e.printStackTrace();
        }

        // Read Configuration
        try (var fileBuffer = new FileReader(CONFIGURATION_FILE))
        {
            // Jackson:  Java <-> JSON API (premier Java JSON API; using tools v3.x.x)
            var mapper = new ObjectMapper();

            // no...THIS one will work.
            mapper = mapper.rebuild()
                    .configure(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES, false)
                    .build();

            var fileContents = fileBuffer.readAllAsString();

            // Read Configuration (local)
            this.configuration = mapper.reader()
                                       .forType(Configuration.class)
                                       .readValue(fileContents);

            // Verify data
            System.out.println(this.configuration);

            // Defaults
            if (this.configuration.youtubeDataDirectory.isBlank())
            {
                this.configuration.youtubeDataDirectory = CONFIGURATION_YOUTUBE_DATA_DIR_DEFAULT;
                System.out.printf("Youtube Data Directory (not found):  defaulting to %1s", this.configuration.youtubeDataDirectory);
            }

            // Data Directories:  youtubeDataDirectory
            var youtubeDataDirectory = ApplicationHelpers.ResolvePath(this.configuration.youtubeDataDirectory);

            if (!Files.exists(youtubeDataDirectory))
                Files.createDirectory(youtubeDataDirectory);
        }
        catch (IOException e)
        {
            System.out.println("Error reading youtube-archiver.json (please check run directory)");

            e.printStackTrace();

            // Default Configuration
            this.configuration = new Configuration();
        }
    }

    @Override
    public void start(Stage mainStage) throws IOException
    {
        // Initialization Result:  Can now be shown to the user
        if (this.initializationError)
        {
            var alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Missing Environment Variable(s)");
            alert.setHeaderText("Some environment variables are required for this application");
            alert.setContentText(PYTHON_SCRIPT_FOLDER_KEY + ":  This must point to your python's script directory");
            alert.showAndWait();

            System.exit(1);
        }

        FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("main-view.fxml"));

        // Load Control Tree (instantiates controllers)
        var parent = (Parent)fxmlLoader.load();

        // Returns main controller
        var mainController = (MainController)fxmlLoader.getController();

        // -> Set Configuration
        mainController.setConfiguration(this.configuration);

        // Create Scene
        this.mainScene = new Scene(parent, 800, 600);
        mainStage.setTitle("Main View!");
        mainStage.setScene(this.mainScene);
        mainStage.setMinWidth(600);
        mainStage.setMinHeight(400);

        // -> Show() (main window)
        mainStage.show();
    }
}
