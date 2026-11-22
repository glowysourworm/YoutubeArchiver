package com.youtubearchiver.youtubearchiver.Dialog.Model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class GetChannelDialogModel extends DialogModelBase
{
    /// URL (or ID) for the Youtube channel
    public StringProperty ChannelUrl;

    public GetChannelDialogModel()
    {
        super("get-channel-dialog-view.fxml", "Youtube Channel (Search)", "Please enter the Url or Id of the channel you would like to add to your dataset");
        this.ChannelUrl = new SimpleStringProperty();
    }

    public String getChannelUrl()
    {
        return ChannelUrl.get();
    }
    public void setChannelUrl(String channelUrl)
    {
        this.ChannelUrl.set(channelUrl);
    }
}
