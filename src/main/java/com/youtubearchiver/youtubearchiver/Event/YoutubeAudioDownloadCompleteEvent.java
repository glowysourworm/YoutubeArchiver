package com.youtubearchiver.youtubearchiver.Event;

import YoutubeArchiverDatabase.Event.RegisterableEvent;
import com.youtubearchiver.youtubearchiver.DataModel.YoutubeDownloaderResponse;

import java.nio.file.Path;

public class YoutubeAudioDownloadCompleteEvent extends RegisterableEvent<YoutubeDownloaderResponse<Path>>
{
}
