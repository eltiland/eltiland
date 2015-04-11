package com.eltiland.ui.course.edit;

import com.eltiland.bl.GenericManager;
import com.eltiland.bl.course.ELTCourseItemManager;
import com.eltiland.exceptions.CourseException;
import com.eltiland.model.course2.content.ELTCourseItem;
import com.eltiland.ui.common.BaseEltilandPanel;
import com.eltiland.ui.common.components.button.icon.ButtonAction;
import com.eltiland.ui.common.components.button.icon.IconButton;
import com.eltiland.ui.common.components.dialog.Dialog;
import com.eltiland.ui.common.components.dialog.ELTAlerts;
import com.eltiland.ui.common.components.dialog.callback.IDialogUpdateCallback;
import com.eltiland.ui.course.CourseControlPage;
import com.eltiland.ui.course.control.data.panel.item.ItemPropertyPanel;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 * General course edit panel.
 *
 * @author Aleksey Plotnikov.
 */
public class AbstractCourseItemPanel<T extends ELTCourseItem> extends BaseEltilandPanel<T> {

    @SpringBean
    private GenericManager genericManager;
    @SpringBean
    private ELTCourseItemManager courseItemManager;

    private Label name;

    private IModel<String> nameModel = new LoadableDetachableModel<String>() {
        @Override
        protected String load() {
            return AbstractCourseItemPanel.this.getModelObject().getName();
        }
    };

    private Dialog<ItemPropertyPanel> itemPropertyPanelDialog =
            new Dialog<ItemPropertyPanel>("itemPropertyDialog", 440) {
                @Override
                public ItemPropertyPanel createDialogPanel(String id) {
                    return new ItemPropertyPanel(id);
                }

                @Override
                public void registerCallback(ItemPropertyPanel panel) {
                    super.registerCallback(panel);
                    panel.setUpdateCallback(new IDialogUpdateCallback.IDialogActionProcessor<ELTCourseItem>() {
                        @Override
                        public void process(IModel<ELTCourseItem> model, AjaxRequestTarget target) {
                            close(target);
                            try {
                                courseItemManager.update(model.getObject());
                                nameModel.detach();
                                target.add(name);
                            } catch (CourseException e) {
                                ELTAlerts.renderErrorPopup(e.getMessage(), target);
                            }
                        }
                    });
                }
            };

    public AbstractCourseItemPanel(String id, IModel<T> eltCourseItemIModel) {
        super(id, eltCourseItemIModel);

        name = new Label("name", nameModel);
        add(name.setOutputMarkupId(true));

        add(new IconButton("rename", new ResourceModel("rename.label"), ButtonAction.EDIT) {
            @Override
            protected void onClick(AjaxRequestTarget target) {
                itemPropertyPanelDialog.getDialogPanel().initData(
                        (IModel<ELTCourseItem>) AbstractCourseItemPanel.this.getModel());
                itemPropertyPanelDialog.show(target);
            }
        });

        add(new IconButton("back", new ResourceModel("back.label"), ButtonAction.BACK) {
            @Override
            protected void onClick(AjaxRequestTarget target) {
                ELTCourseItem item = AbstractCourseItemPanel.this.getModelObject();
                Long courseId = courseItemManager.getCourse(item).getId();

                throw new RestartResponseException(CourseControlPage.class,
                        new PageParameters().add(CourseControlPage.PARAM_ID, courseId));
            }
        });

        add(itemPropertyPanelDialog);
    }
}
