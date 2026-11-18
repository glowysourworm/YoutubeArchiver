package com.youtubearchiver.youtubearchiver.Event;

import YoutubeArchiverDatabase.Event.RegisterableEvent;
import YoutubeArchiverDatabase.Utility.UtilityModel.YoutubeDownloader.Comments;
import com.youtubearchiver.youtubearchiver.DataModel.YoutubeDownloaderResponse;

import java.util.List;

public class YoutubeVideoCommentsCompleteEvent extends RegisterableEvent<YoutubeDownloaderResponse<List<Comments>>>
{
}
