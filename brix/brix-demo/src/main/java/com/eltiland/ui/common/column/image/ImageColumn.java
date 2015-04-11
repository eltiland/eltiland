package com.eltiland.ui.common.column.image;


import com.eltiland.model.IWithAvatar;
import com.eltiland.ui.common.components.ReadonlyObjects;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.table.AbstractColumn;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;

/**
 * @author knorr
 * @version 1.0
 * @since 8/14/12
 */
public class ImageColumn<T extends IWithAvatar> extends AbstractColumn<T> {

    public ImageColumn() {
        super(ReadonlyObjects.EMPTY_DISPLAY_MODEL);
    }

    @Override
    public String getCssClass() {
        return "elti-image-column";
    }

    @Override
    public void populateItem(Item<ICellPopulator<T>> cellItem, String componentId, IModel<T> rowModel) {
        T teacher = rowModel.getObject();
        cellItem.add(new ImagePanel(componentId, teacher.getAvatar(), true));
    }

}