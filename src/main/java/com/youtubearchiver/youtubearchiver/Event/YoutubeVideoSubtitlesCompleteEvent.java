package com.youtubearchiver.youtubearchiver.Event;

import YoutubeArchiverDatabase.Event.RegisterableEvent;
import YoutubeArchiverDatabase.Utility.UtilityModel.YoutubeDownloader.Subtitles;
import com.youtubearchiver.youtubearchiver.DataModel.YoutubeDownloaderResponse;

import java.util.List;

public class YoutubeVideoSubtitlesCompleteEvent extends RegisterableEvent<YoutubeDownloaderResponse<List<Subtitles>>>
{
}
