package com.youtubearchiver.youtubearchiver;

import YoutubeArchiverDatabase.Configuration;
import YoutubeArchiverDatabase.IYoutubeArchiverInitializer;
import YoutubeArchiverDatabase.Logging.LogMessage;
import YoutubeArchiverDatabase.Utility.ApplicationHelpers;

public class ControllerFactory
{
    static Configuration applicationConfiguration;

    public static void SetConfiguration(Configuration configuration)
    {
        applicationConfiguration = configuration;
    }

    /// Method to create controller(s) with default constructors.  TODO: Lookup dependency injection framework
    /// @param type Class type for the controller
    /// @return Initialized controller of type IYoutubeArchiverInitializer
    /// @param <T> Type of controller
    /// @throws Exception Unable to construct type, or the configuration was not yet set (see SetConfiguration)
    public static <T extends IYoutubeArchiverInitializer> IYoutubeArchiverInitializer CreateController(Class<?> type) throws Exception
    {
        if (applicationConfiguration == null)
            throw new Exception("Application configuration not yet set");

        try
        {
            for (var constructor : type.getConstructors())
            {
                if (constructor.getParameterTypes().length == 0)
                {
                    var controller = (IYoutubeArchiverInitializer)constructor.newInstance((Object[])null);

                    controller.Initialize(applicationConfiguration);
                    return controller;
                }
            }
        }
        catch (Exception e)
        {
            ApplicationHelpers.Log(LogMessage.Error("Error constructing (controller) type:  " + type.getName(), e));
            throw e;
        }

        throw new Exception("Unable to create controller of type:  " + type.getName());
    }
}
