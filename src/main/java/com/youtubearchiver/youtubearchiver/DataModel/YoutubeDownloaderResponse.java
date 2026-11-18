package com.youtubearchiver.youtubearchiver.DataModel;

public class YoutubeDownloaderResponse
{
    public final YoutubeDownloaderRequest savedRequest;
    public final Object payload;
    public final boolean result;

    public YoutubeDownloaderResponse(YoutubeDownloaderRequest request, Object payload, boolean result)
    {
        this.savedRequest = request;
        this.payload = payload;
        this.result = result;
    }
}
