package com.eltiland.ui.library.view;

import com.eltiland.ui.common.BaseEltilandPanel;
import com.eltiland.ui.library.LibraryView;
import com.eltiland.ui.library.SearchData;
import com.eltiland.ui.library.view.kind.RecordGalleryViewPanel;
import com.eltiland.ui.library.view.kind.RecordGridViewPanel;
import com.eltiland.ui.library.view.kind.RecordListViewPanel;
import com.eltiland.ui.library.view.panel.RecordSortPanel;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.model.IModel;

/**
 * Library main panel.
 *
 * @author Aleksey Plotnikov.
 */
public abstract class LibraryMainPanel extends BaseEltilandPanel<SearchData> {

    /**
     * Panel constructor.
     *
     * @param id markup id.
     */
    public LibraryMainPanel(String id, IModel<SearchData> searchDataModel) {
        super(id, searchDataModel);

        final WebMarkupContainer contentContainer = new WebMarkupContainer("contentContainer");

        final RecordListViewPanel recordsListPanel = new RecordListViewPanel("content", getModel()) {
            @Override
            protected void onChange(AjaxRequestTarget target) {
                LibraryMainPanel.this.onChange(target);
            }
        };

        final RecordGalleryViewPanel recordGalleryViewPanel = new RecordGalleryViewPanel("content", getModel());
        final RecordGridViewPanel recordGridViewPanel = new RecordGridViewPanel("content", getModel());

        add(new RecordSortPanel("paramPanel", getModel()) {
            @Override
            protected String getCurrentSearch() {
                return getCurrentValue();
            }
        });

        add(contentContainer);

        if (getModelObject().getView().equals(LibraryView.LIST)) {
            contentContainer.add(recordsListPanel);
        } else if (getModelObject().getView().equals(LibraryView.GALLERY)) {
            contentContainer.add(recordGalleryViewPanel);
        } else if (getModelObject().getView().equals(LibraryView.GRID)) {
            contentContainer.add(recordGridViewPanel);
        }
    }

    protected abstract void onChange(AjaxRequestTarget target);

    protected abstract String getCurrentValue();
}
