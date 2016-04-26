package com.eltiland.ui.course.edit;

import com.eltiland.bl.GenericManager;
import com.eltiland.bl.course.CourseItemContentManager;
import com.eltiland.bl.course.ELTCourseItemManager;
import com.eltiland.exceptions.CourseException;
import com.eltiland.model.course2.content.google.CourseItemContent;
import com.eltiland.model.course2.content.google.ELTContentCourseItem;
import com.eltiland.model.course2.content.google.ELTGoogleCourseItem;
import com.eltiland.ui.common.CKEditorFull;
import com.eltiland.ui.common.components.ResourcesUtils;
import com.eltiland.ui.common.components.dialog.ELTAlerts;
import com.eltiland.ui.common.model.GenericDBModel;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.IModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 * Panel for editing course element, based of google.
 *
 * @author Aleksey Plotnikov.
 */
public class ContentCourseItemPanel extends AbstractCourseItemPanel<ELTContentCourseItem> {

    @SpringBean
    private ELTCourseItemManager courseItemManager;
    @SpringBean
    private GenericManager genericManager;
    @SpringBean
    private CourseItemContentManager courseItemContentManager;

    private CKEditorFull editor = new CKEditorFull("content", null);

    public ContentCourseItemPanel(String id, final IModel<ELTContentCourseItem> eltCourseItemIModel) {
        super(id, eltCourseItemIModel);

        Form form = new Form("form");
        add(form);

        form.add(editor);

        genericManager.initialize(getModelObject(), getModelObject().getContent());
        CourseItemContent content = ContentCourseItemPanel.this.getModelObject().getContent();

        if (content != null) {
            editor.setData(content.getBody());
        }

        form.add(new AbstractDocActionPanel("control",
                new GenericDBModel<>(ELTGoogleCourseItem.class, getModelObject())) {
            @Override
            protected void onClick(AjaxRequestTarget target) {
                genericManager.initialize(ContentCourseItemPanel.this.getModelObject(),
                        ContentCourseItemPanel.this.getModelObject().getContent());

                String data = editor.getData();
                CourseItemContent content = ContentCourseItemPanel.this.getModelObject().getContent();

                if (content == null) {
                    content = new CourseItemContent();
                    content.setBody(data);
                    try {
                        courseItemContentManager.create(content);
                    } catch (CourseException e) {
                        ELTAlerts.renderErrorPopup(e.getMessage(), target);
                    }
                    ContentCourseItemPanel.this.getModelObject().setContent(content);
                } else {
                    content.setBody(data);
                    try {
                        courseItemContentManager.update(content);
                    } catch (CourseException e) {
                        ELTAlerts.renderErrorPopup(e.getMessage(), target);
                    }
                }

                try {
                    courseItemManager.update(ContentCourseItemPanel.this.getModelObject());
                } catch (CourseException e) {
                    ELTAlerts.renderErrorPopup(e.getMessage(), target);
                }

                ELTAlerts.renderOKPopup(getString("saveMessage"), target);
            }
        });
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
        response.renderCSSReference(ResourcesUtils.CSS_ICONPANEL);
    }
}
