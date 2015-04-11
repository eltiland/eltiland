package com.eltiland.ui.slider.plugin;

import com.eltiland.model.file.File;
import com.eltiland.ui.common.BaseEltilandPanel;
import com.eltiland.ui.common.components.avatar.CreateAvatarPanel;
import com.eltiland.ui.common.components.dialog.callback.IDialogProcessCallback;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.util.Properties;

class ImagePanel extends BaseEltilandPanel implements IDialogProcessCallback<File> {

    @SpringBean(name = "eltilandProperties")
    private Properties eltilandProps;


    private CreateAvatarPanel imagePanel = new CreateAvatarPanel("imagePanel") {
        @Override
        protected String getHeader() {
            return getString("addImageHeader");
        }

        @Override
        public String getAspectRatio() {
            return eltilandProps.getProperty("slider.aspect.ratio");
        }

        @Override
        public String getWidth() {
            return eltilandProps.getProperty("slider.width");
        }

        @Override
        public String getHeight() {
            return eltilandProps.getProperty("slider.height");
        }

        @Override
        protected float getQuality() {
            return 0.75f;
        }
    };

    public ImagePanel(String id) {
        super(id);

        Form form = new Form("form");
        add(form);

        form.add(imagePanel);
    }

    @Override
    public void setProcessCallback(IDialogActionProcessor<File> callback) {
        imagePanel.setProcessCallback(callback);
    }
}
