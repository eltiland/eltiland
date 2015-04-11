package com.eltiland.ui.news.page;

import com.eltiland.bl.NewsManager;
import com.eltiland.model.NewsItem;
import com.eltiland.ui.common.TwoColumnPage;
import com.eltiland.ui.common.model.TransientReadOnlyModel;
import com.eltiland.ui.news.component.NewsItemPanel;
import com.eltiland.ui.news.component.NewsPanel;
import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.string.StringValueConversionException;

/**
 * Page shows all news in descending order if no news item id provided or shows news item information otherwise.
 */
public class NewsPage extends TwoColumnPage {
    public static final String PARAM_NEWS_ITEM = "id";

    @SpringBean
    private NewsManager newsManager;

    private WebMarkupContainer container = new WebMarkupContainer("newsContainer");

    public NewsPage(PageParameters parameters) {
        add(container.setOutputMarkupId(true));
        if (parameters.getNamedKeys().contains(PARAM_NEWS_ITEM)) {

            try {
                final long id = parameters.get(PARAM_NEWS_ITEM).toLong();
                NewsItem newsItem = newsManager.getNewsItem(id);
                if (newsItem == null) {
                    throw new WicketRuntimeException("News not found!");
                }
                NewsItemPanel newsItemPanel = new NewsItemPanel(
                        "newsContainer",
                        new TransientReadOnlyModel<>(newsItem))
                        .setBodyShowed(true);
                replace(newsItemPanel);

            } catch (StringValueConversionException e) {
                throw new WicketRuntimeException("Incorrect id format!");
            }
        } else {
            NewsPanel panel = new NewsPanel("newsContainer");
            replace(panel);
        }
    }
}