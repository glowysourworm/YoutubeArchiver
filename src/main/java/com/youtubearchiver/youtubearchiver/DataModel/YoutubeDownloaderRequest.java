package com.youtubearchiver.youtubearchiver.DataModel;

import YoutubeArchiverDatabase.Utility.UtilityModel.YoutubeDownloader.YoutubeDownloaderRunType;

/// Data to call the YoutubeDownloaderRunner
public class YoutubeDownloaderRequest
{
    public YoutubeDownloaderRunType runType;
    public String videoId;
    public String channelId;
}
