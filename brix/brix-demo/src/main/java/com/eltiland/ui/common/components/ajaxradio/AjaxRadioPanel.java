package com.eltiland.ui.common.components.ajaxradio;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.RadioGroup;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;

import java.io.Serializable;
import java.util.List;

/**
 * Ajax radiobox panel
 *
 * @author Aleksey Plotnikov
 */
public abstract class AjaxRadioPanel<T extends Serializable> extends Panel {

    private static final long serialVersionUID = 1L;

    private RadioGroup<T> group;

    protected abstract void onRadioSelect(AjaxRequestTarget target, T newSelection);

    protected AjaxRadioPanel(String id) {
        super(id);
    }

    public AjaxRadioPanel(String id, List<T> items, T currentSelection, String labelPropertyExpression) {
        super(id);
        add(buildForm(items, currentSelection, labelPropertyExpression));
    }

    protected Form<T> buildForm(List<T> items, T currentSelection, String labelPropertyExpression) {
        Form<T> form = new Form<>("form");
        group = new RadioGroup<>("radioGroup", new Model<>(currentSelection));

        group.add(newRadios(group, items, labelPropertyExpression));
        form.add(group);
        return form;
    }

    private Component newRadios(final RadioGroup<T> group, List<T> items, final String labelPropertyExpression) {
        return new ListView<T>("radioButtons", items) {

            private static final long serialVersionUID = 1L;

            protected void populateItem(ListItem<T> item) {
                item.add(newAjaxRadioCell(group, item));
                item.add(new Label("label", new PropertyModel<T>(item.getModel(), labelPropertyExpression)));
            }
        };
    }

    private AjaxRadio<T> newAjaxRadioCell(final RadioGroup<T> group, ListItem<T> item) {
        return new AjaxRadio<T>("radio", item.getModel()) {

            private static final long serialVersionUID = 1L;

            @Override
            public void onAjaxEvent(AjaxRequestTarget target) {
                onRadioSelect(target, group.getModelObject());
            }
        };
    }

    public RadioGroup<T> getRadioGroup() {
        return group;
    }
}