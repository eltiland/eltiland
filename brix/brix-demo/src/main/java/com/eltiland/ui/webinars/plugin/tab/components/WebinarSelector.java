package com.eltiland.ui.webinars.plugin.tab.components;

import com.eltiland.bl.GenericManager;
import com.eltiland.model.webinar.Webinar;
import com.eltiland.ui.common.components.select.ELTSelectField;
import com.eltiland.ui.common.model.GenericDBListModel;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.model.IModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.util.List;

/**
 * Webinar selector.
 *
 * @author Aleksey Plotnikov.
 */
public class WebinarSelector extends ELTSelectField<Webinar> {
    @SpringBean
    private GenericManager genericManager;

    public WebinarSelector(String id, IModel<String> headerModel, IModel<Webinar> model) {
        super(id, headerModel, model);
    }

    public WebinarSelector(String id, IModel<String> headerModel, IModel<Webinar> model, boolean isRequired) {
        super(id, headerModel, model, isRequired);
    }

    @Override
    protected IModel<List<Webinar>> getChoiceListModel() {
        return new GenericDBListModel<>(Webinar.class, genericManager.getEntityList(Webinar.class, "id"));
    }

    @Override
    protected IChoiceRenderer<Webinar> getChoiceRenderer() {
        return new IChoiceRenderer<Webinar>() {
            @Override
            public Object getDisplayValue(Webinar object) {
                return object.getName();
            }

            @Override
            public String getIdValue(Webinar object, int index) {
                return object.getId().toString();
            }
        };
    }
}
