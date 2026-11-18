package com.youtubearchiver.youtubearchiver.UIModel;

import YoutubeArchiverDatabase.Utility.UtilityModel.YoutubeDownloader.YoutubeDownloaderRunType;
import javafx.util.StringConverter;

public class YoutubeFunctionSelectionConverter extends StringConverter<YoutubeDownloaderRunType>
{
    @Override
    public String toString(YoutubeDownloaderRunType value)
    {
        // NOT SURE WHERE THIS NULL CAME FROM
        if (value == null)
            return "(Please select a value)";

        // I would throw the application level exception
        return value.getDescription();
    }

    @Override
    public YoutubeDownloaderRunType fromString(String string)
    {
        throw new UnsupportedOperationException("Not supported (required)");
    }
}
