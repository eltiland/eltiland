package com.eltiland.ui.faq;

import com.eltiland.bl.FaqApprovalManager;
import com.eltiland.bl.FaqCategoryManager;
import com.eltiland.bl.FaqManager;
import com.eltiland.model.faq.Faq;
import com.eltiland.model.faq.FaqCategory;
import com.eltiland.ui.common.TwoColumnPage;
import com.eltiland.ui.common.components.ResourcesUtils;
import com.eltiland.ui.common.components.button.EltiAjaxSubmitLink;
import com.eltiland.ui.faq.components.FaqCategoryList;
import com.eltiland.ui.faq.components.FaqPanel;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.navigation.paging.AjaxPagingNavigator;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.PageableListView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.util.List;
import java.util.Properties;

/**
 * Eltiland Questions-Answers page.
 *
 * @author Aleksey Plotnikov.
 */
public class FaqPage extends TwoColumnPage {

    private static final String SEARCH_CSS = "static/css/library/search.css";

    @SpringBean
    private FaqApprovalManager faqApprovalManager;
    @SpringBean
    private FaqManager faqManager;
    @SpringBean
    private FaqCategoryManager faqCategoryManager;
    @SpringBean(name = "eltilandProperties")
    private Properties eltilandProps;

    private int qaCount;

    private FaqCategoryList category = new FaqCategoryList("category") {
        @Override
        public void onCategoryUpdate(AjaxRequestTarget target, IModel<FaqCategory> model) {
            onChange(target);
        }
    };

    TextField<String> search = new TextField<>("searchText", new Model<String>());

    private IModel<List<Faq>> faqList = new LoadableDetachableModel<List<Faq>>() {
        @Override
        protected List<Faq> load() {
            List<Faq> data = faqManager.findByText(category.getCategoryModel().getObject(), search.getModelObject());
            search.setModelObject("");
            return data;
        }
    };

    private IModel<Integer> allCountModel = new LoadableDetachableModel<Integer>() {
        @Override
        protected Integer load() {
            return faqManager.findByText(category.getCategoryModel().getObject(), search.getModelObject()).size();
        }
    };

    private Form container = new Form("container");

    public static final String MOUNT_PATH = "/faq";

    /**
     * Page constructor.
     */
    public FaqPage() {
        super();
        setOutputMarkupId(true);

        add(container.setOutputMarkupId(true));

        this.qaCount = Integer.parseInt(eltilandProps.getProperty("faq.count"));

        final PageableListView list = new PageableListView<Faq>("qaRepeater", faqList, qaCount) {
            @Override
            protected void populateItem(ListItem<Faq> item) {
                item.add(new FaqPanel("qaPanel", item.getModel()));
            }
        };

        EltiAjaxSubmitLink<String> button = new EltiAjaxSubmitLink<String>("searchAction") {
            @Override
            protected void onSubmit(AjaxRequestTarget target, Form components) {
                onChange(target);
            }
        };

        Label searchLabel = new Label("searchLabel", new ResourceModel("searchLabel"));

        container.add(searchLabel);
        container.add(search);
        container.add(button);
        container.add(list.setOutputMarkupId(true));
        container.add(category.setOutputMarkupId(true));
        container.add(search);

        container.add(new AjaxPagingNavigator("navigator", list) {
            @Override
            public boolean isVisible() {
                return allCountModel.getObject() > qaCount;
            }
        });
    }

    private void onChange(AjaxRequestTarget target) {
        allCountModel.detach();
        faqList.detach();
        target.add(container);
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
        response.renderCSSReference(ResourcesUtils.CSS_QA);
        response.renderCSSReference(SEARCH_CSS);
    }
}
