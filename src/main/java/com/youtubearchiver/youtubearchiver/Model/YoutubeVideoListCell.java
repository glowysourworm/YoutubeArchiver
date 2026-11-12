package com.youtubearchiver.youtubearchiver.Model;

import YoutubeArchiverDatabase.Model.YoutubeVideo;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;

import java.util.MissingResourceException;

public class YoutubeVideoListCell extends ListCell<YoutubeVideo>
{
    private final static String PLACEHOLDER_RESOURCE_PATH = "/images/video-placeholder.jpg";

    private final GridPane grid = new GridPane();
    private final ImageView videoImageView = new ImageView();
    private final Label titleLabel = new Label();
    private final Label descriptionLabel = new Label();
    private final Label videoUrlLabel = new Label();

    private final int imageWidth = 80;
    private final int imageHeight = 60;

    public YoutubeVideoListCell()
    {

        var column0 = new ColumnConstraints();
        var column1 = new ColumnConstraints();

        var row0 = new RowConstraints();
        var row1 = new RowConstraints();
        var row2 = new RowConstraints();

        // Column 0 (Image)
        column0.setMinWidth(this.imageWidth);
        column0.setMaxWidth(this.imageWidth);

        // Column 1 (Url)
        column1.setHgrow(Priority.ALWAYS);

        this.videoImageView.setFitWidth(this.imageWidth);
        this.videoImageView.setFitHeight(this.imageHeight);
        this.videoImageView.setSmooth(true);

        this.grid.getColumnConstraints().addAll(column0, column1);
        this.grid.getRowConstraints().addAll(row0, row1, row2);

        this.grid.add(this.videoImageView, 0, 0, 1, 3);
        this.grid.add(this.titleLabel, 1, 0);
        this.grid.add(this.videoUrlLabel, 1, 1);
        this.grid.add(this.descriptionLabel, 1, 2);
    }

    private Image getImageResource(String resourceUrl) throws MissingResourceException
    {
        var stream = getClass().getResourceAsStream(resourceUrl);
        if (stream != null)
        {
            return new Image(stream, this.imageWidth, this.imageHeight, true, true);
        }
        else
            throw new MissingResourceException("Missing package resource", getClass().getName(), resourceUrl);
    }

    private Image getWebImage(String url)
    {
        return new Image(url, this.imageWidth, this.imageHeight, true, true);
    }


    @Override
    protected void updateItem(YoutubeVideo item, boolean empty)
    {
        super.updateItem(item, empty);

        if (empty || item == null)
        {
            setText(null);
            setGraphic(null);
        }
        else
        {
            setText(null);
            setGraphic(grid);
            this.titleLabel.setText(item.getTitle());

            var oldImageUrl = this.videoImageView.getImage() == null ? null : this.videoImageView.getImage().getUrl();
            var newImageUrl = item.getImageURL();

            // Video URL Changed
            if (oldImageUrl == null)
            {
                // Delete Current Image
                this.videoImageView.setImage(null);

                // Placeholder
                if (newImageUrl == null || newImageUrl.isBlank())
                    this.videoImageView.setImage(getImageResource(PLACEHOLDER_RESOURCE_PATH));

                // Video Image
                else
                    this.videoImageView.setImage(getWebImage(newImageUrl));
            }
            else if (!oldImageUrl.equals(newImageUrl))
            {
                // Delete Current Image
                this.videoImageView.setImage(null);

                // Video Image
                if (newImageUrl != null)
                {
                    this.videoImageView.setImage(getWebImage(newImageUrl));
                }

                // Default Placeholder
                else
                {
                    this.videoImageView.setImage(getImageResource(PLACEHOLDER_RESOURCE_PATH));
                }
            }
        }
    }
}
