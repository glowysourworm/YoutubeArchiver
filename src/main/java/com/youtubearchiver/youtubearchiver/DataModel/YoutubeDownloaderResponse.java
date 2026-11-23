package com.youtubearchiver.youtubearchiver.DataModel;

public class YoutubeDownloaderResponse<T>
{
    public final T payload;
    public final boolean result;

    public YoutubeDownloaderResponse(T payload, boolean result)
    {
        this.payload = payload;
        this.result = result;
    }
}
