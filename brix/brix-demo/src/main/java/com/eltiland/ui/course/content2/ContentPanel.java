package com.eltiland.ui.course.content2;

import com.eltiland.bl.GenericManager;
import com.eltiland.model.course2.content.google.ELTContentCourseItem;
import com.eltiland.utils.StringUtils;
import org.apache.wicket.markup.html.basic.MultiLineLabel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 * Panel for output simple content of element.
 *
 * @author Alex Plotnikov
 */
public class ContentPanel extends AbstractCourseContentPanel<ELTContentCourseItem> {

    @SpringBean
    private GenericManager genericManager;

    public ContentPanel(String id, IModel<ELTContentCourseItem> eltGoogleCourseItemIModel) {
        super(id, eltGoogleCourseItemIModel);

        genericManager.initialize(getModelObject(), getModelObject().getContent());
        String data = (getModelObject().getContent() != null) ?
                getModelObject().getContent().getBody() : StringUtils.EMPTY_STRING;
        MultiLineLabel content = new MultiLineLabel("content", new Model<>(data));
        add(content);
        content.setEscapeModelStrings(false);
    }
}
