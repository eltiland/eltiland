package com.eltiland.ui.library;

import com.eltiland.ui.common.BaseEltilandPage;
import com.eltiland.ui.common.components.ResourcesUtils;
import com.eltiland.ui.library.view.LibraryFilterPanel;
import com.eltiland.ui.library.view.LibraryMainPanel;
import com.eltiland.ui.library.view.LibrarySearchPanel;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;

/**
 * Library main page.
 *
 * @author Aleksey Plotnikov.
 */
public class LibraryPage extends BaseEltilandPage {

    public static final String MOUNT_PATH = "/library";

    public LibraryPage(PageParameters parameters) {

        IModel<SearchData> searchDataModel = new Model<>(new SearchData(parameters));

        final LibrarySearchPanel searchPanel = new LibrarySearchPanel("searchPanel", searchDataModel);
        final LibraryFilterPanel filterPanel = new LibraryFilterPanel("filterPanel", searchDataModel) {
            @Override
            protected String getCurrentSearch() {
                return searchPanel.getSearchField().getCurrentValue();
            }
        };
        filterPanel.setOutputMarkupId(true);

        add(new LibraryMainPanel("mainPanel", searchDataModel) {
            @Override
            protected void onChange(AjaxRequestTarget target) {
                target.add(filterPanel);
            }

            @Override
            protected String getCurrentValue() {
                return searchPanel.getSearchField().getCurrentValue();
            }
        });

        add(searchPanel);
        add(filterPanel);
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
        response.renderCSSReference(ResourcesUtils.CSS_LIBRARY);
    }
}
