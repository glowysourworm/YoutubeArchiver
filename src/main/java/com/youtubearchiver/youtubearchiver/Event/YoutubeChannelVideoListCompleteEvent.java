package com.youtubearchiver.youtubearchiver.Event;

import YoutubeArchiverDatabase.Event.RegisterableEvent;
import com.youtubearchiver.youtubearchiver.DataModel.YoutubeDownloaderResponse;

import java.util.List;

public class YoutubeChannelVideoListCompleteEvent extends RegisterableEvent<YoutubeDownloaderResponse<List<String>>>
{
}
