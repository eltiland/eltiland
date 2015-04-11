package com.eltiland.ui.library.view;

import com.eltiland.model.library.LibraryRecord;
import com.eltiland.model.user.User;
import com.eltiland.session.EltilandSession;
import com.eltiland.ui.common.BaseEltilandPanel;
import com.eltiland.ui.common.components.behavior.TooltipBehavior;
import com.eltiland.ui.common.components.button.icon.ButtonAction;
import com.eltiland.ui.common.components.button.icon.IconButton;
import com.eltiland.ui.common.components.dialog.ELTAlerts;
import com.eltiland.ui.library.LibraryAdminPage;
import com.eltiland.ui.library.LibraryEditRecordPage;
import com.eltiland.ui.library.LibraryPage;
import com.eltiland.ui.library.SearchData;
import com.eltiland.ui.library.components.search.SearchField;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.ResourceModel;

/**
 * Panel with search field for library.
 *
 * @author Aleksey Plotnikov.
 */
public class LibrarySearchPanel extends BaseEltilandPanel<SearchData> {

    private IModel<User> currentUserModel = new LoadableDetachableModel<User>() {
        @Override
        protected User load() {
            return EltilandSession.get().getCurrentUser();
        }
    };

    private SearchField searchField = new SearchField("searchField", getModel()) {
        @Override
        protected void onSearch(AjaxRequestTarget target,
                                String searchString, Class<? extends LibraryRecord> clazz) {
            LibrarySearchPanel.this.getModelObject().setSearchString(searchString);
            LibrarySearchPanel.this.getModelObject().setClazz(clazz);
            LibrarySearchPanel.this.getModelObject().redirect();
        }
    };

    /**
     * Panel constructor.
     *
     * @param id markup id.
     */
    public LibrarySearchPanel(String id, final IModel<SearchData> searchDataIModel) {
        super(id, searchDataIModel);

        WebMarkupContainer resetButton = new WebMarkupContainer("resetButton");
        resetButton.add(new AjaxEventBehavior("onclick") {
            @Override
            protected void onEvent(AjaxRequestTarget target) {
                setResponsePage(LibraryPage.class);
            }
        });

        resetButton.add(new AttributeModifier("title", new ResourceModel("resetTooltip")));
        resetButton.add(new TooltipBehavior());

        add(resetButton);

        add(new IconButton("addButton", new ResourceModel("add"), ButtonAction.ADD) {
            @Override
            protected void onClick(AjaxRequestTarget target) {
                if (currentUserModel.getObject() != null) {
                    throw new RestartResponseException(LibraryEditRecordPage.class);
                } else {
                    ELTAlerts.renderErrorPopup(getString("loginMessage"), target);
                }
            }
        });

        add(new IconButton("controlButton", new ResourceModel("control"), ButtonAction.SETTINGS) {
            @Override
            protected void onClick(AjaxRequestTarget target) {
                throw new RestartResponseException(LibraryAdminPage.class);
            }

            @Override
            public boolean isVisible() {
                return currentUserModel.getObject() != null && currentUserModel.getObject().isSuperUser();
            }
        });

        add(searchField);
    }

    /**
     * @return searchField.
     */
    public SearchField getSearchField() {
        return searchField;
    }
}
