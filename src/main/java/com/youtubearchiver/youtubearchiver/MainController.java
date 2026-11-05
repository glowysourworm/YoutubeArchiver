package com.youtubearchiver.youtubearchiver;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ResourceBundle;

public class MainController implements Initializable {

    @FXML protected TextField firstNameText;
    @FXML protected TextField lastNameText;

    protected Person person;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        person = new Person();

        firstNameText.textProperty().bindBidirectional(person.firstNameProperty());
        lastNameText.textProperty().bindBidirectional(person.lastNameProperty());
    }

    @FXML
    protected void onShowButtonClick() {

        String content = String.format("Person: %1s, %2s", person.getLastName(), person.getFirstName());

        Alert alert = new Alert(Alert.AlertType.NONE, content, ButtonType.OK);
        alert.setHeaderText("Personal Information");
        alert.setTitle("Youtube Archiver");
        alert.showAndWait();
    }
}
