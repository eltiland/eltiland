package com.eltiland.ui.faq.components;

import com.eltiland.bl.FaqManager;
import com.eltiland.model.faq.Faq;
import com.eltiland.ui.common.BaseEltilandPanel;
import org.apache.wicket.ajax.markup.html.navigation.paging.AjaxPagingNavigator;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.PageableListView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.util.List;
import java.util.Properties;

/**
 * FAQ tab.
 *
 * @author Aleksey Plotnikov.
 */
public class FaqTab extends BaseEltilandPanel {
    @SpringBean
    private FaqManager faqManager;
    @SpringBean(name = "eltilandProperties")
    private Properties eltilandProps;

    private Class<? extends Faq> faqClass;

    private int qaCount;
    private int allCount;

    private IModel<List<Faq>> faqList = new LoadableDetachableModel<List<Faq>>() {
        @Override
        protected List<Faq> load() {
            return faqManager.getFaqList(null);
        }
    };

    public FaqTab(String id, Class<? extends Faq> faqClass) {
        super(id);
        setOutputMarkupId(true);
        this.faqClass = faqClass;
        this.qaCount = Integer.parseInt(eltilandProps.getProperty("faq.count"));
        this.allCount = faqManager.getFaqCount(null, null);

        PageableListView list = new PageableListView<Faq>("qaRepeater", faqList, qaCount) {
            @Override
            protected void populateItem(ListItem<Faq> item) {
                item.add(new FaqPanel("qaPanel", item.getModel()));
            }


        };
        add(list);

        add(new AjaxPagingNavigator("navigator", list) {
            @Override
            public boolean isVisible() {
                return allCount > qaCount;
            }
        });
    }
}
