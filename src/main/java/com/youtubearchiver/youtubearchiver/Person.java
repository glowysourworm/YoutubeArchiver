package com.youtubearchiver.youtubearchiver;

import javafx.beans.binding.ObjectBinding;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Person {

    private StringProperty firstName = new SimpleStringProperty(this, "firstName", "(Initial Value)");
    private StringProperty lastName = new SimpleStringProperty(this, "lastName", "");

    public String getFirstName()
    {
        return firstName.get();
    }
    public String getLastName()
    {
        return lastName.get();
    }
    public void setFirstName(String value)
    {
        firstName.setValue(value);
    }
    public void setLastName(String value)
    {
        lastName.setValue(value);
    }

    public StringProperty firstNameProperty()
    {
        return firstName;
    }
    public StringProperty lastNameProperty()
    {
        return lastName;
    }
}
