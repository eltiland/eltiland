package com.eltiland.ui.faq.components;

import com.eltiland.bl.FaqCategoryManager;
import com.eltiland.model.faq.FaqCategory;
import com.eltiland.ui.common.BaseEltilandPanel;
import com.eltiland.ui.common.components.ResourcesUtils;
import com.eltiland.ui.common.model.GenericDBModel;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.util.ArrayList;
import java.util.List;

/**
 * DropDownList with faq categories
 *
 * @author Pavel Androschuk
 */
public abstract class FaqCategoryList extends BaseEltilandPanel {
    @SpringBean
    FaqCategoryManager faqCategoryManager;

    private DropDownChoice<FaqCategory> ddList = new DropDownChoice<FaqCategory>("category",
            new GenericDBModel<>(FaqCategory.class),
            new ArrayList<FaqCategory>());

    public FaqCategoryList(String id) {
        super(id);

        IModel<List<FaqCategory>> categories = new LoadableDetachableModel<List<FaqCategory>>() {
            @Override
            protected List<FaqCategory> load() {
                return faqCategoryManager.getList();
            }
        };
        ddList.setChoices(categories);

        if (categories.getObject().size() > 0) {
            ddList.setDefaultModelObject(categories.getObject().get(0));
        }

        ddList.add(
                new AjaxFormComponentUpdatingBehavior("onchange") {
                    protected void onUpdate(AjaxRequestTarget target) {
                        onCategoryUpdate(target, ddList.getModel());
                    }
                });

        Label label = new Label("label", new ResourceModel("categoryLabel"));

        add(label);
        add(ddList);
    }

    public IModel<FaqCategory> getCategoryModel() {
        return ddList.getModel();
    }

    public DropDownChoice<FaqCategory> getListObject() {
        return ddList;
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
        response.renderCSSReference(ResourcesUtils.CSS_FAQ_CATEGORY);
    }

    public abstract void onCategoryUpdate(AjaxRequestTarget target, IModel<FaqCategory> model);
}
