package com.eltiland.ui.common.column.image;

import com.eltiland.bl.GenericManager;
import com.eltiland.model.file.File;
import com.eltiland.ui.common.BaseEltilandPanel;
import com.eltiland.ui.common.components.ReadonlyObjects;
import com.eltiland.ui.common.resource.StaticImage;
import com.inmethod.grid.column.AbstractColumn;
import org.apache.wicket.Component;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.model.IModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 * Column for output image from File (for admin interface).
 *
 * @author Aleksey Plotnikov.
 */
public abstract class ImageFileColumn<I, T> extends AbstractColumn<I, T> {

    @SpringBean
    private GenericManager genericManager;

    public ImageFileColumn(String columnId) {
        super(columnId, ReadonlyObjects.EMPTY_DISPLAY_MODEL);
    }

    @Override
    public Component newCell(WebMarkupContainer parent, String componentId, IModel<T> rowModel) {
        return new ImagePanel(componentId, getImageFile(rowModel));
    }

    private class ImagePanel extends BaseEltilandPanel<File> {
        public ImagePanel(String id, IModel<File> fileIModel) {
            super(id, fileIModel);
            add(new StaticImage("image", fileIModel.getObject().getId(), true));
        }
    }

    /**
     * @return file model with image for given entity model.
     */
    protected abstract IModel<File> getImageFile(IModel<T> rowModel);
}
