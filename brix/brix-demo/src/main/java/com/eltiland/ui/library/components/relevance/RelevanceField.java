package com.eltiland.ui.library.components.relevance;

import com.eltiland.ui.common.BaseEltilandPanel;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.FormComponentPanel;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;

import java.util.ArrayList;
import java.util.List;

/**
 * Field for editing relevance of the library record
 *
 * @author Aleksey Plotnikov.
 */
public class RelevanceField extends FormComponentPanel<Integer> {

    private static final String CSS = "static/css/library/relevance.css";

    private IModel<List<Integer>> valuesList = new LoadableDetachableModel<List<Integer>>() {
        @Override
        protected List<Integer> load() {
            List<Integer> result = new ArrayList<>();
            for (int i = 1; i <= 10; i++) {
                result.add(i);
            }
            return result;
        }
    };

    WebMarkupContainer listContainer = new WebMarkupContainer("list");

    public RelevanceField(String id, IModel<Integer> model) {
        super(id, model);

        add(listContainer.setOutputMarkupId(true));

        ListView<Integer> starList = new ListView<Integer>("starContainer", valuesList) {
            @Override
            protected void populateItem(ListItem<Integer> item) {
                item.add(new StarPanel("starPanel", item.getModel()) {
                    @Override
                    protected void onClick(AjaxRequestTarget target, int index) {
                        RelevanceField.this.setModelObject(index);
                        target.add(listContainer);
                    }
                });
            }
        };

        listContainer.add(starList);
    }

    private abstract class StarPanel extends BaseEltilandPanel<Integer> {
        public StarPanel(String id, IModel<Integer> integerIModel) {
            super(id, integerIModel);

            WebMarkupContainer container = new WebMarkupContainer("star");
            int value = RelevanceField.this.getModelObject();
            container.add(new AttributeAppender("class", new Model<>(
                    (value >= integerIModel.getObject()) ? "pressed" : "free"), " "));
            container.add(new AjaxEventBehavior("onclick") {
                @Override
                protected void onEvent(AjaxRequestTarget target) {
                    onClick(target, StarPanel.this.getModelObject());
                }
            });

            add(container);
        }

        protected abstract void onClick(AjaxRequestTarget target, int index);
    }

    @Override
    protected void convertInput() {
        super.convertInput();
        setConvertedInput(getModelObject());
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
        response.renderCSSReference(CSS);
    }
}
