package com.youtubearchiver.youtubearchiver.UIModel;

import javafx.geometry.Insets;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import org.kordamp.ikonli.fontawesome.FontAwesome;
import org.kordamp.ikonli.javafx.FontIcon;

public class EntityTreeCell extends TreeCell<EntityTreeCellData>
{
    private final HBox closedIcon;
    private final HBox openIcon;

    private final Color closedFill = Color.color(0, 0, 0, 0.75);
    private final Color openFill = Color.color(0, 0, 0, 0.50);
    private final Color closedFillHover = Color.color(0.5, 0.5, 0.5, 0.75);
    private final Color openFillHover = Color.color(0.8, 0.8, 0.8, 0.75);
    private final Color closedStroke = Color.color(1, 1, 1, 0.75);
    private final Color openStroke = Color.color(0.8, 0.8, 0.8, 0.75);

    public EntityTreeCell()
    {
        this.closedIcon = new HBox();
        this.openIcon = new HBox();

        // Cell Margin
        this.closedIcon.setPadding(new Insets(2));
        this.openIcon.setPadding(new Insets(2));

        var closedGlyph = new FontIcon(FontAwesome.DATABASE);
        var openGlyph = new FontIcon(FontAwesome.DATABASE);

        closedGlyph.setIconSize(14);
        openGlyph.setIconSize(14);

        closedGlyph.setStroke(this.closedStroke);
        openGlyph.setStroke(this.openStroke);

        closedGlyph.setFill(this.closedFill);
        openGlyph.setFill(this.openFill);

        closedGlyph.setStrokeWidth(1);
        openGlyph.setStrokeWidth(1);

        // Mouse Event Styling
        closedGlyph.setOnMouseEntered(event -> ((FontIcon)event.getTarget()).setFill(closedFillHover));
        openGlyph.setOnMouseEntered(event -> ((FontIcon)event.getTarget()).setFill(openFillHover));

        closedGlyph.setOnMouseClicked(event -> ((FontIcon)event.getTarget()).setFill(closedFillHover));
        openGlyph.setOnMouseClicked(event -> ((FontIcon)event.getTarget()).setFill(openFillHover));

        closedGlyph.setOnMouseExited(event -> ((FontIcon)event.getTarget()).setFill(closedFill));
        openGlyph.setOnMouseExited(event -> ((FontIcon)event.getTarget()).setFill(openFill));

        this.closedIcon.getChildren().add(closedGlyph);
        this.openIcon.getChildren().add(openGlyph);
    }

    @Override
    public void updateItem(EntityTreeCellData item, boolean empty)
    {
        super.updateItem(item, empty);

        if (empty || item == null)
        {
            setText(null);
            setGraphic(null);
            setDisclosureNode(null);
        }
        else
        {
            setText(item.name);
            setGraphic(null);

            TreeItem<EntityTreeCellData> treeItem = getTreeItem();

            if (!treeItem.isLeaf())
            {
                setDisclosureNode(treeItem.isExpanded() ? openIcon : closedIcon);
            }
            else
            {
                setDisclosureNode(null);
                setGraphic(openIcon);
            }
        }
    }
}
