package com.eltiland.ui.common.components.label;

import com.eltiland.ui.common.BaseEltilandPanel;
import com.eltiland.ui.common.components.UIConstants;
import com.eltiland.utils.DateUtils;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import java.util.Date;

/**
 * Label can format date according to {@link DateLabel#}.
 *
 * @author Alexander Litvinenko
 */
public class DateLabel extends BaseEltilandPanel<Date> {

    private Label label = new Label("label", new Model<String>());

    public DateLabel(String id) {
        super(id);
        add(label.setRenderBodyOnly(true));
        add(AttributeAppender.append("class", UIConstants.CLASS_DATE));
    }

    public DateLabel(String id, IModel<Date> model) {
        this(id);
        setModel(model);
        label.setDefaultModelObject(DateUtils.formatDate(model.getObject()));
    }

    public DateLabel(String id, Date date) {
        this(id, Model.of(date));
    }

    @Override
    protected void onModelChanged() {
        super.onModelChanged();

        label.setDefaultModelObject(DateUtils.formatDate(getModelObject()));
    }
}
