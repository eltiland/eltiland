package com.eltiland.ui.library;

import com.eltiland.model.library.LibraryRecord;
import com.eltiland.model.user.User;
import com.eltiland.session.EltilandSession;
import com.eltiland.ui.common.BaseEltilandPage;
import com.eltiland.ui.common.components.ResourcesUtils;
import com.eltiland.ui.common.components.button.EltiSpinAjaxDecorator;
import com.eltiland.ui.common.components.button.back.BackButton;
import com.eltiland.ui.common.components.button.icon.ButtonAction;
import com.eltiland.ui.common.components.button.icon.IconButton;
import com.eltiland.ui.library.admin.CollectionPanel;
import com.eltiland.ui.library.admin.RecordConfirmPanel;
import com.eltiland.ui.tags.plugin.GeneralTagPanel;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.IAjaxCallDecorator;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.panel.EmptyPanel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.request.http.flow.AbortWithHttpErrorCodeException;

import javax.servlet.http.HttpServletResponse;

/**
 * Library administration page.
 *
 * @author Aleksey Plotnikov.
 */
public class LibraryAdminPage extends BaseEltilandPage {
    public static final String MOUNT_PATH = "/libraryAdmin";

    private WebMarkupContainer contentContainer = new WebMarkupContainer("contentContainer");

    /**
     * Page ctor.
     */
    public LibraryAdminPage() {
        User currentUser = EltilandSession.get().getCurrentUser();
        if (currentUser == null || !(currentUser.isSuperUser())) {
            throw new AbortWithHttpErrorCodeException(HttpServletResponse.SC_NOT_FOUND);
        }

        add(new IconButton("tagButton", new ResourceModel("tagAction"), ButtonAction.TAG) {
            @Override
            protected void onClick(AjaxRequestTarget target) {
                contentContainer.replace(new GeneralTagPanel("content", LibraryRecord.class));
                target.add(contentContainer);
            }

            @Override
            protected IAjaxCallDecorator getAjaxCallDecorator() {
                return new EltiSpinAjaxDecorator(contentContainer);
            }
        });

        add(new IconButton("collectionButton", new ResourceModel("collectionAction"), ButtonAction.COLLECTION) {
            @Override
            protected void onClick(AjaxRequestTarget target) {
                contentContainer.replace(new CollectionPanel("content"));
                target.add(contentContainer);
            }
        });

        add(new IconButton("recordsButton", new ResourceModel("recordAction"), ButtonAction.SETTINGS) {
            @Override
            protected void onClick(AjaxRequestTarget target) {
                contentContainer.replace(new RecordConfirmPanel("content"));
                target.add(contentContainer);
            }
        });

        add(new BackButton("backButton"));
        add(contentContainer.setOutputMarkupId(true));
        contentContainer.add(new EmptyPanel("content"));
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
        response.renderCSSReference(ResourcesUtils.CSS_LIBRARY_ADMIN);
    }
}
