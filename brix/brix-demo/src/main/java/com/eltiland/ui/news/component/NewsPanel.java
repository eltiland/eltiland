package com.eltiland.ui.news.component;

import com.eltiland.bl.NewsManager;
import com.eltiland.model.NewsItem;
import com.eltiland.ui.common.BaseEltilandPanel;
import com.eltiland.ui.common.components.UIConstants;
import com.eltiland.ui.common.components.behavior.OnClickEventBehavior;
import com.eltiland.ui.common.components.datatable.EltiDataProviderBase;
import com.eltiland.ui.common.components.datatable.EltiDataTable;
import com.eltiland.ui.common.model.GenericDBModel;
import com.eltiland.ui.news.page.NewsPage;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.table.AbstractColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.ISortableDataProvider;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

/**
 * Shows news in descending order by date.
 * Shows all news by default.
 */
public class NewsPanel extends BaseEltilandPanel<List<NewsItem>> {

    @SpringBean
    private NewsManager newsManager;

    @SpringBean(name = "eltilandProperties")
    private Properties eltilandProps;

    private boolean isShortNewsList = false;
    private BookmarkablePageLink<NewsPage> archiveLink = new BookmarkablePageLink<NewsPage>("archiveLink", NewsPage.class) {
        @Override
        protected void onConfigure() {
            super.onConfigure();
            setVisible(isShortNewsList());
        }
    };

    private List<IColumn<NewsItem>> createColumn = Arrays.<IColumn<NewsItem>>asList(
            new AbstractColumn<NewsItem>(new ResourceModel("news")) {

                @Override
                public void populateItem(
                        Item<ICellPopulator<NewsItem>> cellItem,
                        String componentId,
                        IModel<NewsItem> rowModel) {
                    cellItem.add(AttributeAppender.append("class", "record-column"));
                    cellItem.add(new NewsItemPanel(
                            componentId,
                            new GenericDBModel<>(NewsItem.class, rowModel.getObject())) {

                        @Override
                        protected void onConfigure() {
                            super.onConfigure();
                            if (!isBodyShowed()) {
                                add(new OnClickEventBehavior() {
                                    @Override
                                    protected void onEvent(AjaxRequestTarget target) {
                                        PageParameters pageParameters = new PageParameters();
                                        pageParameters.add(NewsPage.PARAM_NEWS_ITEM, getModelObject().getId());
                                        setResponsePage(NewsPage.class, pageParameters);
                                    }
                                });
                            }
                        }
                    });
                }
            }
    );

    /**
     * Results data table.
     */
    private EltiDataTable<NewsItem> newsDataTable = new EltiDataTable<NewsItem>(
            "newsDataTable",
            createColumn,
            createDataProvider(),
            UIConstants.ROWS_PER_PAGE) {
        @Override
        public String getVariation() {
            return "div";
        }
    };

    /**
     * Default constructor
     *
     * @param id wicket id of the component
     */
    public NewsPanel(String id) {
        super(id);
        add(newsDataTable);
        add(archiveLink);
    }

    protected ISortableDataProvider<NewsItem> createDataProvider() {
        return new EltiDataProviderBase<NewsItem>() {
            int newsCountToLoad = Integer.decode(eltilandProps.getProperty("news.count"));

            @Override
            public Iterator iterator(int first, int count) {
                return newsManager.getNewsList(
                        first,
                        isShortNewsList() ? newsCountToLoad : count,
                        Arrays.asList("date", "id"),
                        false, null).iterator();
            }

            @Override
            public int size() {
                return isShortNewsList() ? newsCountToLoad : newsManager.getNewsListCount(null);
            }
        };
    }

    public boolean isShortNewsList() {
        return isShortNewsList;
    }

    public NewsPanel setShortNewsList(boolean shortNewsList) {
        this.isShortNewsList = shortNewsList;
        return this;
    }
}
