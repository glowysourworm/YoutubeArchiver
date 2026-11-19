package com.youtubearchiver.youtubearchiver.Service;

import YoutubeArchiverDatabase.Configuration;
import YoutubeArchiverDatabase.Model.YoutubeChannel;
import YoutubeArchiverDatabase.Service.YoutubeArchiverDatabaseConnection;

import java.util.List;

/// Primary Entity Service:  1) Handles get / set for entities, 2) Forwards database updates via
/// the EventAggregator
public class YoutubePrimaryEntityService
{
    // Application Configuration
    private final Configuration configuration;

    public YoutubePrimaryEntityService(final Configuration configuration)
    {
        this.configuration = configuration;
    }

    public List<YoutubeChannel> GetChannels()
    {
        var connection = new YoutubeArchiverDatabaseConnection(this.configuration);

        return connection.GetEntities();
    }
}
