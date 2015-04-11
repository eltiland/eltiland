package com.eltiland.ui.news.component;

import com.eltiland.bl.NewsManager;
import com.eltiland.exceptions.NewsException;
import com.eltiland.model.NewsItem;
import com.eltiland.session.EltilandSession;
import com.eltiland.ui.common.BaseEltilandPanel;
import com.eltiland.ui.common.CKEditorInline;
import com.eltiland.ui.common.components.UIConstants;
import com.eltiland.ui.common.components.dialog.ELTAlerts;
import com.eltiland.ui.common.components.label.DateLabel;
import com.eltiland.ui.news.page.NewsPage;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 * Panel represents news item.
 *
 * @see NewsItemPanel#setBodyShowed(boolean)
 */
public class NewsItemPanel extends BaseEltilandPanel<NewsItem> {
    @SpringBean
    private NewsManager newsManager;

    private boolean bodyShowed = false;
    private BookmarkablePageLink<NewsPage> archiveLink = new BookmarkablePageLink<NewsPage>("archiveLink", NewsPage.class) {
        @Override
        protected void onConfigure() {
            super.onConfigure();
            setVisible(isBodyShowed());
        }
    };

    private final Label body = new Label("body", new Model<String>());

    public NewsItemPanel(String id, IModel<NewsItem> model) {
        super(id, model);

        WebMarkupContainer itemContainer = new WebMarkupContainer("itemContainer");
        add(itemContainer);

        String type = "newsItemType";
        if (model.getObject().getAnnouncement()) {
            type = "announcementType";
        }

        itemContainer.add(new DateLabel("date", model.getObject().getDate()));
        itemContainer.add(new Label("type", new ResourceModel(type)));
        itemContainer.add(new Label("title", new Model<String>())
                .setDefaultModelObject(getModelObject().getTitle())
                .add(AttributeModifier.append("class", new AbstractReadOnlyModel<Object>() {
                    @Override
                    public Object getObject() {
                        if (isBodyShowed()) {
                            return UIConstants.CLASS_NEWS_ITEM_TITLE;
                        }
                        return "";
                    }
                })));
        body.setDefaultModelObject(getModelObject().getBody());
        body.setEscapeModelStrings(false);
        if (EltilandSession.get().getCurrentUser() != null) {
            if (EltilandSession.get().getCurrentUser().isSuperUser()) {
                body.add(new CKEditorInline() {
                    @Override
                    public void onChanged(String data, AjaxRequestTarget target) {
                        NewsItem item = getModelObject();
                        if (item.getBody().equals(data)) {
                            return;
                        }

                        item.setBody(data);
                        try {
                            newsManager.updateNewsItem(item);
                            ELTAlerts.renderOKPopup(getString("saved"), target);
                        } catch (NewsException e) {
                            ELTAlerts.renderErrorPopup(e.getMessage(), target);
                        }
                    }
                });
            }
        }

        itemContainer.add(body);
        itemContainer.add(archiveLink);
    }

    @Override
    protected void onConfigure() {
        super.onConfigure();
        body.setVisible(isBodyShowed());
        if (getModelObject().getAnnouncement()) {
            if (!isBodyShowed()) {
                add(AttributeModifier.append("class", UIConstants.CLASS_NEWS_ITEM_ANNOUNCEMENT));
            }
        }
    }

    /**
     * @return {@code true} if news item body is showed, {@code false} otherwise.
     */
    public boolean isBodyShowed() {
        return bodyShowed;
    }

    /**
     * Sets whether to show news item body.
     *
     * @param bodyShowed is body showed
     * @return {@code this}
     */
    public NewsItemPanel setBodyShowed(boolean bodyShowed) {
        this.bodyShowed = bodyShowed;
        return this;
    }
}
