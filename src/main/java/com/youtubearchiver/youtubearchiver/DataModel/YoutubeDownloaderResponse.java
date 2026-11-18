package com.youtubearchiver.youtubearchiver.DataModel;

public class YoutubeDownloaderResponse<T>
{
    public final YoutubeDownloaderRequest savedRequest;
    public final T payload;
    public final boolean result;

    public YoutubeDownloaderResponse(YoutubeDownloaderRequest request, T payload, boolean result)
    {
        this.savedRequest = request;
        this.payload = payload;
        this.result = result;
    }
}
