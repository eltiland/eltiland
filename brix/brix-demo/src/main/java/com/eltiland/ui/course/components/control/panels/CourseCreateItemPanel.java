package com.eltiland.ui.course.components.control.panels;

import com.eltiland.model.course.*;
import com.eltiland.model.course.test.TestCourseItem;
import com.eltiland.ui.common.BaseEltilandPanel;
import com.eltiland.ui.common.components.button.EltiAjaxSubmitLink;
import com.eltiland.ui.common.components.dialog.callback.IDialogNewCallback;
import com.eltiland.ui.common.components.form.FormRequired;
import com.eltiland.ui.common.components.select.ELTSelectField;
import com.eltiland.ui.common.components.textfield.ELTTextField;
import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Panel for creating course item.
 *
 * @author Aleksey PLotnikov.
 */
public class CourseCreateItemPanel extends BaseEltilandPanel<CourseItem> implements IDialogNewCallback<CourseItem> {

    private static final Logger LOGGER = LoggerFactory.getLogger(CourseCreateItemPanel.class);

    private IDialogActionProcessor<CourseItem> newCallback;

    private ELTSelectField<Class<? extends CourseItem>> elementSelector =
            new ELTSelectField<Class<? extends CourseItem>>("selectElement", new ResourceModel("addElementLabel"),
                    new Model<Class<? extends CourseItem>>(FolderCourseItem.class)) {

                @Override
                protected IModel<List<Class<? extends CourseItem>>> getChoiceListModel() {
                    return new LoadableDetachableModel<List<Class<? extends CourseItem>>>() {
                        @Override
                        protected List<Class<? extends CourseItem>> load() {
                            return new ArrayList<>(Arrays.asList(
                                    FolderCourseItem.class,
                                    DocumentCourseItem.class,
                                    PresentationCourseItem.class,
                                    TestCourseItem.class,
                                    VideoCourseItem.class,
                                    WebinarCourseItem.class));
                        }
                    };
                }

                @Override
                protected IChoiceRenderer<Class<? extends CourseItem>> getChoiceRenderer() {
                    return new IChoiceRenderer<Class<? extends CourseItem>>() {
                        @Override
                        public Object getDisplayValue(Class<? extends CourseItem> object) {
                            return getString(object.getSimpleName() + ".label");
                        }

                        @Override
                        public String getIdValue(Class<? extends CourseItem> object, int index) {
                            return getString(object.getSimpleName() + ".label");
                        }
                    };
                }
            };

    private ELTTextField<String> elementName =
            new ELTTextField<>("elementName", new ResourceModel("elementNameLabel"),
                    new Model<String>(), String.class, true);


    public CourseCreateItemPanel(String id) {
        super(id);

        Form form = new Form("form");
        add(form);

        form.add(new FormRequired("required"));
        form.add(new EltiAjaxSubmitLink("createButton") {
            @Override
            protected void onSubmit(AjaxRequestTarget target, Form form) {
                Class<? extends CourseItem> clazz = elementSelector.getModelObject();

                CourseItem item = null;
                try {
                    item = clazz.newInstance();
                } catch (InstantiationException | IllegalAccessException e) {
                    LOGGER.error("Cannot create course item", e);
                    throw new WicketRuntimeException("Cannot create course item", e);
                }
                item.setName(elementName.getModelObject());

                newCallback.process(new Model<>(item), target);
            }
        });

        elementSelector.setNullValid(false);
        form.add(elementSelector);
        form.add(elementName);
    }

    @Override
    public void setNewCallback(IDialogActionProcessor<CourseItem> callback) {
        this.newCallback = callback;
    }
}
