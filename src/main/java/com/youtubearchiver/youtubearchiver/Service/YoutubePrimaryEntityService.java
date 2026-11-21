package com.youtubearchiver.youtubearchiver.Service;

import YoutubeArchiverDatabase.Configuration;
import YoutubeArchiverDatabase.Model.YoutubeChannel;
import YoutubeArchiverDatabase.Service.YoutubeArchiverDatabaseConnection;
import YoutubeArchiverDatabase.IYoutubeArchiverInitializer;

import java.util.List;

/// Primary Entity Service:  1) Handles get / set for entities, 2) Forwards database updates via
/// the EventAggregator
public class YoutubePrimaryEntityService implements IYoutubeArchiverInitializer
{
    // Application Configuration
    private Configuration configuration;

    public List<YoutubeChannel> GetChannels()
    {
        var connection = new YoutubeArchiverDatabaseConnection(this.configuration);

        return connection.GetEntities();
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
