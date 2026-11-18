package com.youtubearchiver.youtubearchiver.Utility;

import javafx.application.Platform;

import java.util.function.Consumer;

public class FXApplicationHelpers
{
    /// Applies function to main thread if it was supplied on another thread.
    /// @param consumer The function to invoke
    /// @param argument The argument for the function (consumer)
    /// @param <T> The type of argument
    public static <T> void EnsureMainThread(Consumer<T> consumer, T argument)
    {
        // This must be forwarded to the main thread
        if (!Platform.isFxApplicationThread())
        {
            Platform.runLater(() ->
            {
                consumer.accept(argument);
            });
        }
        else
            consumer.accept(argument);
    }
}
