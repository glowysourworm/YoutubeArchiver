package com.youtubearchiver.youtubearchiver.Service;

import YoutubeArchiverDatabase.Configuration;
import YoutubeArchiverDatabase.Event.EventAggregator;
import YoutubeArchiverDatabase.Logging.LogMessage;
import YoutubeArchiverDatabase.Utility.*;
import YoutubeArchiverDatabase.Utility.UtilityModel.YoutubeDownloader.Comments;
import YoutubeArchiverDatabase.Utility.UtilityModel.YoutubeDownloader.Subtitles;
import YoutubeArchiverDatabase.Utility.UtilityModel.YoutubeDownloader.YoutubeDownloaderMetadata;
import YoutubeArchiverDatabase.Utility.UtilityModel.YoutubeDownloader.YoutubeDownloaderRunType;
import com.youtubearchiver.youtubearchiver.DataModel.YoutubeDownloaderRequest;
import com.youtubearchiver.youtubearchiver.DataModel.YoutubeDownloaderResponse;
import com.youtubearchiver.youtubearchiver.Event.*;

import java.nio.file.Path;
import java.util.List;

public class YoutubeChannelService implements InitializableComponent<Configuration>
{
    // Application Configuration
    private Configuration configuration;

    // Youtube Runners
    YoutubeAudioDownloadRunner youtubeAudioDownloadRunner;
    YoutubeChannelVideoListRunner  youtubeChannelVideoListRunner;
    YoutubeVideoCommentsRunner  youtubeVideoCommentsRunner;
    YoutubeVideoDescriptionRunner   youtubeVideoDescriptionRunner;
    YoutubeVideoDownloadRunner  youtubeVideoDownloadRunner;
    YoutubeVideoMetadataRunner  youtubeVideoMetadataRunner;
    YoutubeVideoSubtitlesRunner  youtubeVideoSubtitlesRunner;

    // Service Request: This service is meant to handle requests one at a time. There could
    //                  be several runner calls to get the response data. So, the initial
    //                  request must be saved here.
    YoutubeDownloaderRequest currentRequest;

    public YoutubeChannelService()
    {

    }

    public void Initialize(Configuration configuration)
    {
        if (IsInitialized())
            throw new IllegalStateException("Service Already Initialized");

        this.configuration = configuration;
        this.currentRequest = null;

        this.youtubeAudioDownloadRunner = new YoutubeAudioDownloadRunner(configuration);
        this.youtubeChannelVideoListRunner = new YoutubeChannelVideoListRunner(configuration);
        this.youtubeVideoCommentsRunner = new YoutubeVideoCommentsRunner(configuration);
        this.youtubeVideoDescriptionRunner = new YoutubeVideoDescriptionRunner(configuration);
        this.youtubeVideoDownloadRunner = new YoutubeVideoDownloadRunner(configuration);
        this.youtubeVideoMetadataRunner = new YoutubeVideoMetadataRunner(configuration);
        this.youtubeVideoSubtitlesRunner = new YoutubeVideoSubtitlesRunner(configuration);

        this.youtubeAudioDownloadRunner.AddListener(this::OnYoutubeAudioDownloadComplete);
        this.youtubeChannelVideoListRunner.AddListener(this::OnYoutubeChannelVideoListComplete);
        this.youtubeVideoCommentsRunner.AddListener(this::OnYoutubeVideoCommentsComplete);
        this.youtubeVideoDescriptionRunner.AddListener(this::OnYoutubeVideoDescriptionComplete);
        this.youtubeVideoDownloadRunner.AddListener(this::OnYoutubeVideoDownloadComplete);
        this.youtubeVideoMetadataRunner.AddListener(this::OnYoutubeVideoMetadataComplete);
        this.youtubeVideoSubtitlesRunner.AddListener(this::OnYoutubeVideoSubtitlesComplete);
    }

    public boolean IsInitialized()
    {
        return this.configuration != null;
    }

    private void OnYoutubeAudioDownloadComplete(Path audioFilePath)
    {
        // -> Publish Response (request (saved), payload, success / failure)
        EventAggregator.GetEvent(YoutubeVideoDownloadCompleteEvent.class)
                       .Publish(new YoutubeDownloaderResponse(this.currentRequest, audioFilePath, audioFilePath != null));
    }
    private void OnYoutubeChannelVideoListComplete(List<String> videoIds)
    {
        // -> Publish Response (request (saved), payload, success / failure)
        EventAggregator.GetEvent(YoutubeChannelVideoListCompleteEvent.class)
                       .Publish(new YoutubeDownloaderResponse(this.currentRequest, videoIds, videoIds != null));
    }
    private void OnYoutubeVideoCommentsComplete(List<Comments> comments)
    {
        // -> Publish Response (request (saved), payload, success / failure)
        EventAggregator.GetEvent(YoutubeVideoCommentsCompleteEvent.class)
                       .Publish(new YoutubeDownloaderResponse(this.currentRequest, comments, comments != null));
    }
    private void OnYoutubeVideoDescriptionComplete(String description)
    {
        // -> Publish Response (request (saved), payload, success / failure)
        EventAggregator.GetEvent(YoutubeVideoDescriptionCompleteEvent.class)
                       .Publish(new YoutubeDownloaderResponse(this.currentRequest, description, description != null));
    }
    private void OnYoutubeVideoDownloadComplete(Path videoFilePath)
    {
        // -> Publish Response (request (saved), payload, success / failure)
        EventAggregator.GetEvent(YoutubeVideoDownloadCompleteEvent.class)
                       .Publish(new YoutubeDownloaderResponse(this.currentRequest, videoFilePath, videoFilePath != null));
    }
    private void OnYoutubeVideoMetadataComplete(YoutubeDownloaderMetadata metadata)
    {
        // -> Publish Response (request (saved), payload, success / failure)
        EventAggregator.GetEvent(YoutubeVideoMetadataCompleteEvent.class)
                       .Publish(new YoutubeDownloaderResponse(this.currentRequest, metadata, metadata != null));
    }
    private void OnYoutubeVideoSubtitlesComplete(List<Subtitles> subtitles)
    {
        // -> Publish Response (request (saved), payload, success / failure)
        EventAggregator.GetEvent(YoutubeVideoSubtitlesCompleteEvent.class)
                       .Publish(new YoutubeDownloaderResponse(this.currentRequest, subtitles, subtitles != null));
    }

    public void ExecuteRequest(YoutubeDownloaderRequest request)
    {
        if (!this.IsInitialized())
            throw new IllegalStateException("Service Not Initialized");

        // IServiceRunner -> Notify()
        //
        // The result is handled in the listener code. The EventAggregator forwards the
        // result onward (once the runners have all completed)
        //

        try
        {
            this.currentRequest = request;

            switch (request.runType)
            {
                case VideoMetadata:
                    this.youtubeVideoMetadataRunner.Initialize(request.videoId);
                    this.youtubeVideoMetadataRunner.Execute();
                    break;
                case VideoComments:
                    this.youtubeVideoCommentsRunner.Initialize(request.videoId);
                    this.youtubeVideoCommentsRunner.Execute();
                    break;
                case VideoDescription:
                    this.youtubeVideoDescriptionRunner.Initialize(request.videoId);
                    this.youtubeVideoDescriptionRunner.Execute();
                    break;
                case VideoSubtitleList:
                    this.youtubeVideoSubtitlesRunner.Initialize(request.videoId);
                    this.youtubeVideoSubtitlesRunner.Execute();
                    break;
                case VideoDownload:
                    this.youtubeVideoDownloadRunner.Initialize(request.videoId);
                    this.youtubeVideoDownloadRunner.Execute();
                    break;
                case VideoAudioDownload:
                    this.youtubeAudioDownloadRunner.Initialize(request.videoId);
                    this.youtubeAudioDownloadRunner.Execute();
                    break;
                case ChannelVideoList:
                    this.youtubeChannelVideoListRunner.Initialize(request.channelId);
                    this.youtubeChannelVideoListRunner.Execute();
                    break;
                default:
                    throw new EnumConstantNotPresentException(YoutubeDownloaderRunType.class,  request.runType.toString());
            }
        }
        catch (Exception e)
        {
            ApplicationHelpers.Log(LogMessage.Error("Error executing youtube downloader", e));
        }
    }
}
