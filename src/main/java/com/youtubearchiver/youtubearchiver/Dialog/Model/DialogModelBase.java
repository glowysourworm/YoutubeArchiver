package com.youtubearchiver.youtubearchiver.Dialog.Model;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public abstract class DialogModelBase
{
    public SimpleBooleanProperty Confirm;
    public SimpleBooleanProperty Cancel;
    public StringProperty Title;

    /// The description of the reason for the dialog (what are these fields for? Where can I get the help doc?)
    public StringProperty Description;

    /// The name of the .fxml view to create (this should be just the name + extension, not the path)
    public StringProperty ViewName;

    public DialogModelBase(String viewName, String title, String description)
    {
        this.Title = new SimpleStringProperty(title);
        this.Description = new SimpleStringProperty(description);
        this.Confirm = new SimpleBooleanProperty(false);
        this.Cancel = new SimpleBooleanProperty(false);
        this.ViewName = new SimpleStringProperty(viewName);
    }

    public boolean getConfirm()
    {
        return this.Confirm.getValue();
    }
    public boolean getCancel()
    {
        return this.Cancel.getValue();
    }
    public String getTitle()
    {
        return this.Title.getValue();
    }
    public String getDescription()
    {
        return this.Description.getValue();
    }
    public String getViewName()
    {
        return this.ViewName.getValue();
    }

    public void setConfirm(boolean value)
    {
        this.Confirm.setValue(value);
    }
    public void setCancel(boolean value)
    {
        this.Cancel.setValue(value);
    }
    public void setTitle(String value)
    {
        this.Title.setValue(value);
    }
    public void setDescription(String value)
    {
        this.Description.setValue(value);
    }
    public void setViewName(String value)
    {
        throw new IllegalCallerException("Not allowed to set view name. Please see the constructor");
    }
}
