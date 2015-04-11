package com.eltiland.ui.course.components;

import com.eltiland.bl.GenericManager;
import com.eltiland.model.file.File;
import com.eltiland.ui.common.BaseEltilandPanel;
import com.eltiland.ui.common.model.GenericDBModel;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.resource.ByteArrayResource;
import org.apache.wicket.request.resource.IResource;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 * Course icon file panel.
 *
 * @author Aleksey Plotnikov.
 */
public class CourseFileIconPanel extends BaseEltilandPanel<File> {

    @SpringBean
    private GenericManager genericManager;

    private WebMarkupContainer iconContainer = new WebMarkupContainer("iconContainer");

    /**
     * Panel constructor.
     *
     * @param id         markup id
     * @param fileIModel file model.
     * @param nameModel  model of the name of course, used when icon is null.
     */
    public CourseFileIconPanel(String id, IModel<File> fileIModel, IModel<String> nameModel) {
        super(id, fileIModel);

        add(iconContainer.setOutputMarkupId(true));
        iconContainer.add(getPanel(fileIModel, nameModel));
    }

    /**
     * Data replacer (for edit panel)
     *
     * @param fileIModel file model.
     * @param nameModel  model of the name of course, used when icon is null.
     */
    public void replaceData(IModel<File> fileIModel, IModel<String> nameModel) {
        iconContainer.replace(getPanel(fileIModel, nameModel));
    }

    private BaseEltilandPanel getPanel(IModel<File> fileIModel, IModel<String> nameModel) {
        File icon = fileIModel.getObject();
        return (icon != null) ?
                (new ImagePanel("image", new GenericDBModel<>(File.class, icon))) :
                (new DefaultPanel("image", nameModel));
    }

    private class DefaultPanel extends BaseEltilandPanel<String> {
        private DefaultPanel(String id, IModel<String> stringIModel) {
            super(id, stringIModel);

            add(new Label("courseName", String.format(getString("name"), stringIModel.getObject())));
        }
    }

    private class ImagePanel extends BaseEltilandPanel<File> {
        protected ImagePanel(String id, IModel<File> fileIModel) {
            super(id, fileIModel);

            File icon = fileIModel.getObject();
            genericManager.initialize(icon, icon.getPreviewBody());
            genericManager.initialize(icon, icon.getBody());
            genericManager.initialize(icon.getPreviewBody(), icon.getPreviewBody().getBody());

            IResource resource = new ByteArrayResource(icon.getType(), icon.getPreviewBody().getBody(), icon.getName());

            add(new Image("image", resource));
        }
    }
}
