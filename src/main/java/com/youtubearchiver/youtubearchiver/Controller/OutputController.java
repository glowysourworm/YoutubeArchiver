package com.youtubearchiver.youtubearchiver.Controller;

import YoutubeArchiverDatabase.Event.EventAggregator;
import YoutubeArchiverDatabase.Event.LogMessageEvent;
import YoutubeArchiverDatabase.Utility.ApplicationHelpers;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import org.controlsfx.control.StatusBar;

public class OutputController
{
    @FXML private TextArea outputTextArea;
    @FXML private StatusBar statusBar;

    protected int textLineCounter;

    public OutputController()
    {

    }

    @FXML
    protected void initialize()
    {
        // Output Log: Append text messages from the log
        EventAggregator.GetEvent(LogMessageEvent.class).Subscribe(message ->
        {
            // o(@.^)@  <-- Java Psycho..  When you "send" a delegate to the main thread it is common
            //                             to add data.. like a real function..! Why is there no parameter
            //                             available?
            Platform.runLater(() ->
            {
                // When was message changed?  There could've been some reference data modified.  Obviously,
                // we can handle that here.. But, why is there no parameter? The Java backend would be
                // dealing with threading issues related to the core.
                //
                // I suspect they did not want to deal with context switching for performance reasons. Both
                // MSFT and Java have never restructured their backends after binding was introduced. They
                // have to learn the hardest part of pri-maddona-ing:
                //
                // Giving up control
                //
                // Let's work together and rebuild C++, using a Java-based compiler toolset, reducing the
                // number of language features, ridding the kernel bugs of the past, and completing an
                // actually modular compiler to perform native compiling.
                //
                // GNU Standards! Meetings are daily at 9am sharp! All in attendance are the team!  ^_^
                //
                appendLogMessage(message);
            });
        });

        // Initialize Log:  There are already log messages on the output
        ApplicationHelpers.GetLogs().forEach(message ->
        {
            appendLogMessage(ApplicationHelpers.GetLogger().FormatMessage(message));
        });
    }

    private void appendLogMessage(String message)
    {
        // Remove oldest line of text
        if (this.textLineCounter++ > 1000)
        {
            var firstLineIndex = this.outputTextArea.getText().indexOf('\n');

            // Remove (top-most) line of text
            this.outputTextArea.deleteText(0, firstLineIndex);
        }

        // -> Autoscroll (to bottom)
        this.outputTextArea.appendText(message + "\n");
        this.statusBar.setText(message);
    }
}
