package com.eltiland.ui.google.pages;

import com.eltiland.bl.GenericManager;
import com.eltiland.model.google.GoogleDriveFile;
import com.eltiland.ui.common.BaseEltilandPage;
import com.eltiland.ui.common.components.button.back.BackButton;
import com.eltiland.ui.google.ELTGoogleDriveEditor;
import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.http.flow.AbortWithHttpErrorCodeException;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletResponse;

/**
 * Abstract edit/view google page.
 *
 * @author Aleksey Plotnikov
 */
public abstract class AbstractGooglePage extends BaseEltilandPage {
    @SpringBean
    private GenericManager genericManager;

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractGooglePage.class);

    /**
     * Profile id page parameter.
     */
    public static final String PARAM_ID = "id";

    /**
     * Page constructor.
     *
     * @param parameters page params.
     */
    public AbstractGooglePage(PageParameters parameters) {
        super(parameters);

        if (!parameters.getNamedKeys().contains(PARAM_ID)) {
            String errMsg = String.format("Mandatory parameter: %s doesn't passed", PARAM_ID);
            LOGGER.error(errMsg);
            throw new WicketRuntimeException(errMsg);
        }

        GoogleDriveFile file = genericManager.getObject(GoogleDriveFile.class, parameters.get(PARAM_ID).toLong());
        if (file == null) {
            throw new AbortWithHttpErrorCodeException(HttpServletResponse.SC_NOT_FOUND);
        }

        add(new BackButton("backButton"));

        add(new ELTGoogleDriveEditor("editor", new Model<>(file), getMode(), getType()));
    }

    /**
     * @return edit/view mode.
     */
    public abstract ELTGoogleDriveEditor.MODE getMode();

    /**
     * @return type of document.
     */
    public abstract GoogleDriveFile.TYPE getType();
}
