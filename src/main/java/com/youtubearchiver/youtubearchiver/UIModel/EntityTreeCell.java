package com.youtubearchiver.youtubearchiver.UIModel;

import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.paint.Paint;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.fontawesome.FontAwesome;

public class EntityTreeCell extends TreeCell<EntityTreeCellData>
{
    private final FontIcon closedIcon = new  FontIcon(FontAwesome.DATABASE);
    private final FontIcon openIcon = new  FontIcon(FontAwesome.DATABASE);

    public EntityTreeCell()
    {
        closedIcon.setIconSize(12);
        openIcon.setIconSize(12);

        closedIcon.setStroke(Paint.valueOf("#FF000000"));
        openIcon.setStroke(Paint.valueOf("#FF000000"));

        closedIcon.setFill(Paint.valueOf("#DD0000FF"));
        openIcon.setFill(Paint.valueOf("#DD00FF00"));
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
            TreeItem<EntityTreeCellData> treeItem = getTreeItem();

            if (treeItem != null && !treeItem.isLeaf())
            {
                setDisclosureNode(treeItem.isExpanded() ? openIcon : closedIcon);
            }
            else
            {
                setDisclosureNode(null);
            }
        }
    }
}
