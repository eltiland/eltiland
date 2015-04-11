package com.eltiland.ui.magazine;

import com.eltiland.bl.FileManager;
import com.eltiland.bl.GenericManager;
import com.eltiland.model.file.File;
import com.eltiland.model.magazine.Magazine;
import com.eltiland.ui.common.TwoColumnPage;
import com.eltiland.ui.common.components.avatar.AvatarPreviewPanel;
import com.eltiland.ui.common.components.button.back.BackButton;
import com.eltiland.ui.common.model.GenericDBModel;
import com.eltiland.utils.UrlUtils;
import org.apache.wicket.Application;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.basic.MultiLineLabel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 * Page for output detail information of magazine.
 *
 * @author Aleksey Plotnikov.
 */
public class MagazineAboutPage extends TwoColumnPage {

    @SpringBean
    private GenericManager genericManager;
    @SpringBean
    private FileManager fileManager;

    public static String MOUNT_PATH = "magDetails";

    public static final String PARAM_ID = "id";

    private AvatarPreviewPanel coverPanel = new AvatarPreviewPanel("cover",
            UrlUtils.StandardIcons.ICONS_IMAGE) {
        @Override
        protected boolean isDescriptionVisible() {
            return false;
        }

        @Override
        protected boolean outputFullVersion() {
            return true;
        }
    };

    public MagazineAboutPage(PageParameters parameters) {
        super(parameters);

        if (parameters.isEmpty()) {
            throw new WicketRuntimeException("parameters must not be empty");
        }
        Long id = parameters.get(PARAM_ID).toLong();

        //Check input parameter
        if (id == null) {
            throw new RestartResponseException(Application.get().getApplicationSettings().getInternalErrorPage());
        }

        Magazine magazine = genericManager.getObject(Magazine.class, id);
        if (magazine == null) {
            throw new RestartResponseException(Application.get().getApplicationSettings().getInternalErrorPage());
        }

        add(new Label("name", magazine.getName()));
        add(new BackButton("backButton"));
        add(coverPanel);
        if (magazine.getCover() != null) {
            File file = fileManager.getFileById(magazine.getCover().getId());
            coverPanel.initEditMode(new GenericDBModel<>(File.class, file));
        }
        add(new Label("topic", String.format(getString("topic"), magazine.getTopic())));
        add(new MultiLineLabel("about", magazine.getAbout()).setEscapeModelStrings(false));
    }
}
