package com.eltiland.ui.common.components.avatar;

import com.eltiland.model.file.File;
import com.eltiland.ui.common.components.button.EltiAjaxLink;
import com.eltiland.ui.common.components.dialog.Dialog;
import com.eltiland.ui.common.components.dialog.callback.IDialogCloseCallback;
import com.eltiland.ui.common.components.dialog.callback.IDialogProcessCallback;
import com.eltiland.ui.course.components.CourseFileIconPanel;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.FormComponentPanel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.util.Properties;

/**
 * Panel for editing icon of the course.
 *
 * @author Aleksey Plotnikov.
 */
public class CourseIconEditPanel extends FormComponentPanel<File> {
    @SpringBean(name = "eltilandProperties")
    private Properties eltilandProps;

    /**
     * Avatar creation dialog.
     */
    private Dialog<CreateAvatarPanel> dialog = new Dialog<CreateAvatarPanel>("changeAvatarDialog", 400) {
        @Override
        public CreateAvatarPanel createDialogPanel(String id) {
            return new CreateAvatarPanel(id) {
                @Override
                public String getAspectRatio() {
                    return eltilandProps.getProperty("avatar.course.aspect.ratio");
                }

                @Override
                public String getWidth() {
                    return eltilandProps.getProperty("avatar.course.width");
                }

                @Override
                public String getHeight() {
                    return eltilandProps.getProperty("avatar.course.height");
                }

                @Override
                protected String getHeader() {
                    return getString("courseHeader");
                }
            };
        }

        @Override
        public void registerCallback(CreateAvatarPanel panel) {
            super.registerCallback(panel);

            panel.setProcessCallback(new IDialogProcessCallback.IDialogActionProcessor<File>() {
                @Override
                public void process(IModel<File> fileModel, AjaxRequestTarget target) {
                    File file = fileModel.getObject();

                    imageModel.setObject(file);
                    iconPanel.replaceData(imageModel, nameModel);
                    target.add(iconPanel);
                    close(target);
                }
            });

            panel.setCloseCallback(new IDialogCloseCallback.IDialogActionProcessor() {
                @Override
                public void process(AjaxRequestTarget target) {
                    close(target);
                }
            });
        }
    };

    private WebMarkupContainer iconContainer = new WebMarkupContainer("iconContainer");

    private IModel<File> imageModel = new LoadableDetachableModel<File>() {
        @Override
        protected File load() {
            return null;
        }
    };

    private IModel<String> nameModel = new Model<>();
    private CourseFileIconPanel iconPanel;

    /**
     * Panel constructor.
     *
     * @param id              markup id.
     * @param model           file model.
     * @param courseNameModel course name model.
     */
    public CourseIconEditPanel(String id, IModel<File> model, IModel<String> courseNameModel) {
        super(id, model);

        imageModel.setObject(model.getObject());
        nameModel.setObject(courseNameModel.getObject());
        iconPanel = new CourseFileIconPanel("iconPanel", model, courseNameModel);

        add(new EltiAjaxLink("changeLink") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                dialog.show(target);
            }
        });

        add(dialog);
        add(iconPanel.setOutputMarkupId(true));

        setOutputMarkupId(true);
    }


    /**
     * @return icon file.
     */
    public File getIconFile() {
        return imageModel.getObject();
    }
}
