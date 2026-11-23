package com.youtubearchiver.youtubearchiver.Service;

import YoutubeArchiverDatabase.Configuration;
import YoutubeArchiverDatabase.Event.EventAggregator;
import YoutubeArchiverDatabase.IYoutubeArchiverInitializer;
import YoutubeArchiverDatabase.Logging.LogMessage;
import YoutubeArchiverDatabase.Utility.*;
import YoutubeArchiverDatabase.Utility.UtilityModel.YoutubeDownloader.Comments;
import YoutubeArchiverDatabase.Utility.UtilityModel.YoutubeDownloader.Subtitles;
import YoutubeArchiverDatabase.Utility.UtilityModel.YoutubeDownloader.YoutubeDownloaderMetadata;
import YoutubeArchiverDatabase.Utility.UtilityModel.YoutubeDownloader.YoutubeDownloaderRunType;
import com.youtubearchiver.youtubearchiver.DataModel.YoutubeDownloaderRequest;
import com.youtubearchiver.youtubearchiver.DataModel.YoutubeDownloaderResponse;
import com.youtubearchiver.youtubearchiver.Event.*;
import javafx.application.Platform;

import java.nio.file.Path;
import java.util.ArrayDeque;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;

public class YoutubeDownloaderService implements IYoutubeArchiverInitializer
{
    // Application Configuration
    private Configuration configuration;

    // Execution Queue
    private Queue<YoutubeDownloaderRequest> requestQueue;

    // Async Blocking
    private final boolean working;
    private final int workLimit;
    private final AtomicInteger workCount;

    public YoutubeDownloaderService()
    {

        this.requestQueue = new ArrayDeque<>();
        this.working = false;
        this.workLimit = 1;
        this.workCount = new AtomicInteger(0);
    }

    public void Initialize(Configuration configuration)
    {
        if (IsInitialized())
            throw new IllegalStateException("Service Already Initialized");

        this.configuration = configuration;
    }

    public boolean IsInitialized()
    {
        return this.configuration != null;
    }

    private void OnYoutubeAudioDownloadComplete(Path audioFilePath)
    {
        Platform.runLater(() ->
        {
            // Work Count
            this.workCount.decrementAndGet();

            // -> Publish Response (request (saved), payload, success / failure)
            EventAggregator.GetEvent(YoutubeVideoDownloadCompleteEvent.class)
                           .Publish(new YoutubeDownloaderResponse(audioFilePath, audioFilePath != null));

            CheckWork();
        });
    }
    private void OnYoutubeChannelVideoListComplete(List<String> videoIds)
    {
        Platform.runLater(() ->
        {
            // Work Count
            this.workCount.decrementAndGet();

            // -> Publish Response (request (saved), payload, success / failure)
            EventAggregator.GetEvent(YoutubeChannelVideoListCompleteEvent.class)
                           .Publish(new YoutubeDownloaderResponse(videoIds, videoIds != null));

            CheckWork();
        });
    }
    private void OnYoutubeVideoCommentsComplete(List<Comments> comments)
    {
        Platform.runLater(() ->
        {
            // Work Count
            this.workCount.decrementAndGet();

            // -> Publish Response (request (saved), payload, success / failure)
            EventAggregator.GetEvent(YoutubeVideoCommentsCompleteEvent.class)
                           .Publish(new YoutubeDownloaderResponse(comments, comments != null));

            CheckWork();
        });
    }
    private void OnYoutubeVideoDescriptionComplete(String description)
    {
        Platform.runLater(() ->
        {
            // Work Count
            this.workCount.decrementAndGet();

            // -> Publish Response (request (saved), payload, success / failure)
            EventAggregator.GetEvent(YoutubeVideoDescriptionCompleteEvent.class)
                           .Publish(new YoutubeDownloaderResponse(description, description != null));

            CheckWork();
        });
    }
    private void OnYoutubeVideoDownloadComplete(Path videoFilePath)
    {
        Platform.runLater(() ->
        {
            // Work Count
            this.workCount.decrementAndGet();

            // -> Publish Response (request (saved), payload, success / failure)
            EventAggregator.GetEvent(YoutubeVideoDownloadCompleteEvent.class)
                           .Publish(new YoutubeDownloaderResponse(videoFilePath, videoFilePath != null));

            CheckWork();
        });
    }
    private void OnYoutubeVideoMetadataComplete(YoutubeDownloaderMetadata metadata)
    {
        Platform.runLater(() ->
        {
            // Work Count
            this.workCount.decrementAndGet();

            // -> Publish Response (request (saved), payload, success / failure)
            EventAggregator.GetEvent(YoutubeVideoMetadataCompleteEvent.class)
                           .Publish(new YoutubeDownloaderResponse(metadata, metadata != null));

            CheckWork();
        });
    }
    private void OnYoutubeVideoSubtitlesComplete(List<Subtitles> subtitles)
    {
        Platform.runLater(() ->
        {
            // Work Count
            this.workCount.decrementAndGet();

            // -> Publish Response (request (saved), payload, success / failure)
            EventAggregator.GetEvent(YoutubeVideoSubtitlesCompleteEvent.class)
                           .Publish(new YoutubeDownloaderResponse(subtitles, subtitles != null));

            CheckWork();
        });
    }

    public void ExecuteRequest(YoutubeDownloaderRequest request)
    {
        if (!this.IsInitialized())
            throw new IllegalStateException("Service Not Initialized");

        // Working...
        if (this.working || !this.requestQueue.isEmpty())
            throw new IllegalStateException("Request queue is non-empty. The service is running async operations!");

        ExecuteRequestImpl(request);
    }

    public void ExecuteRequestAsync(YoutubeDownloaderRequest request)
    {
        if (!this.IsInitialized())
            throw new IllegalStateException("Service Not Initialized");

        // Enqueue
        this.requestQueue.add(request);

        CheckWork();
    }

    private void CheckWork()
    {
        // Main Thread (here)

        if (!this.requestQueue.isEmpty() &&
            !this.working &&
             this.workCount.get() < this.workLimit)
        {
            // Work Count
            this.workCount.incrementAndGet();

            // Dequeue
            var workItem = this.requestQueue.poll();

            // -> Async
            ExecuteRequestAsyncImpl(workItem);
        }
    }

    private void ExecuteRequestAsyncImpl(YoutubeDownloaderRequest request)
    {
        // Run our execution method asynchronously
        var asyncRunner = new CompletableFuture<Void>();

        asyncRunner.completeAsync(() ->
        {
            // -> Event Listeners (-> CheckWork())
            this.ExecuteRequestImpl(request);
            return null;
        });
    }


    private void ExecuteRequestImpl(YoutubeDownloaderRequest request)
    {
        // IServiceRunner -> Notify()
        //
        // The result is handled in the listener code. The EventAggregator forwards the
        // result onward (once the runners have completed)
        //

        try
        {
            switch (request.runType)
            {
                case VideoMetadata:
                {
                    var runner = new YoutubeVideoMetadataRunner(this.configuration);
                    runner.AddListener(this::OnYoutubeVideoMetadataComplete);
                    runner.Initialize(request.videoId);
                    runner.Execute();
                }
                break;
                case VideoComments:
                {
                    var runner = new YoutubeVideoCommentsRunner(this.configuration);
                    runner.AddListener(this::OnYoutubeVideoCommentsComplete);
                    runner.Initialize(request.videoId);
                    runner.Execute();
                }
                break;
                case VideoDescription:
                {
                    var runner = new YoutubeVideoDescriptionRunner(this.configuration);
                    runner.AddListener(this::OnYoutubeVideoDescriptionComplete);
                    runner.Initialize(request.videoId);
                    runner.Execute();
                }
                break;
                case VideoSubtitleList:
                {
                    var runner = new YoutubeVideoSubtitlesRunner(this.configuration);
                    runner.AddListener(this::OnYoutubeVideoSubtitlesComplete);
                    runner.Initialize(request.videoId);
                    runner.Execute();
                }
                break;
                case VideoDownload:
                {
                    var runner = new YoutubeVideoDownloadRunner(this.configuration);
                    runner.AddListener(this::OnYoutubeVideoDownloadComplete);
                    runner.Initialize(request.videoId);
                    runner.Execute();
                }
                break;
                case VideoAudioDownload:
                {
                    var runner = new YoutubeAudioDownloadRunner(this.configuration);
                    runner.AddListener(this::OnYoutubeAudioDownloadComplete);
                    runner.Initialize(request.videoId);
                    runner.Execute();
                }
                break;
                case ChannelVideoList:
                {
                    var runner = new YoutubeChannelVideoListRunner(this.configuration);
                    runner.AddListener(this::OnYoutubeChannelVideoListComplete);
                    runner.Initialize(request.channelId);
                    runner.Execute();
                }
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
