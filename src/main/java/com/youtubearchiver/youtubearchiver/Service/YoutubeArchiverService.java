package com.youtubearchiver.youtubearchiver.Service;

import YoutubeArchiverDatabase.Configuration;
import YoutubeArchiverDatabase.Event.EventAggregator;
import YoutubeArchiverDatabase.Logging.LogMessage;
import YoutubeArchiverDatabase.Utility.ApplicationHelpers;
import YoutubeArchiverDatabase.Utility.UtilityModel.YoutubeDownloader.YoutubeDownloaderRunType;
import YoutubeArchiverDatabase.Utility.YoutubeDownloaderRunner;
import com.youtubearchiver.youtubearchiver.DataModel.YoutubeDownloaderRequest;
import com.youtubearchiver.youtubearchiver.DataModel.YoutubeDownloaderResponse;
import com.youtubearchiver.youtubearchiver.Event.YoutubeDownloaderCompleteEvent;

public class YoutubeArchiverService implements InitializableComponent<Configuration>
{
    // Application Configuration
    private Configuration configuration;

    // Primary yt-dlp shell runner
    private YoutubeDownloaderRunner youtubeDownloaderRunner;

    public YoutubeArchiverService()
    {
        this.youtubeDownloaderRunner = null;
    }

    public void ExecuteRequest(YoutubeDownloaderRequest request)
    {
        if (!this.IsInitialized())
            throw new IllegalStateException("Service Not Initialized");

        try
        {
            Object payload = null;

            youtubeDownloaderRunner.Reset();

            switch (request.runType)
            {
                case VideoMetadata:
                    payload = youtubeDownloaderRunner.ExecuteGetMetadata(request.videoId);
                    break;
                case VideoComments:
                    payload = youtubeDownloaderRunner.ExecuteGetComments(request.videoId);
                    break;
                case VideoDescription:
                    payload = youtubeDownloaderRunner.ExecuteGetVideoDescription(request.videoId);
                    break;
                case VideoSubtitleList:
                    payload = youtubeDownloaderRunner.ExecuteGetSubtitleList(request.videoId);
                    break;
                case VideoDownload:
                    payload = youtubeDownloaderRunner.ExecuteDownloadVideo(request.videoId);
                    break;
                case VideoAudioDownload:
                    payload = youtubeDownloaderRunner.ExecuteDownloadAudio(request.videoId);
                    break;
                case ChannelVideoList:
                    payload = youtubeDownloaderRunner.ExecuteGetVideoList(request.channelId);
                    break;
                default:
                    throw new EnumConstantNotPresentException(YoutubeDownloaderRunType.class,  request.runType.toString());
            }

            // -> Publish Response (request (saved), payload, success / failure)
            EventAggregator.GetEvent(YoutubeDownloaderCompleteEvent.class)
                           .Publish(new YoutubeDownloaderResponse(request, payload, payload != null));
        }
        catch (Exception e)
        {
            ApplicationHelpers.Log(LogMessage.Error("Error executing youtube downloader", e));
        }
    }

    public void Initialize(Configuration configuration)
    {
        if (IsInitialized())
            throw new IllegalStateException("Service Already Initialized");

        this.configuration = configuration;

        this.youtubeDownloaderRunner = new YoutubeDownloaderRunner(this.configuration);
    }

    public boolean IsInitialized()
    {
        return this.configuration != null;
    }
}
