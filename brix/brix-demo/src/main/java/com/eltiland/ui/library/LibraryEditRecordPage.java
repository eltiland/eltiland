package com.eltiland.ui.library;

import com.eltiland.bl.GenericManager;
import com.eltiland.model.library.LibraryRecord;
import com.eltiland.model.user.User;
import com.eltiland.session.EltilandSession;
import com.eltiland.ui.common.BaseEltilandPage;
import com.eltiland.ui.common.components.dialog.EltiStaticAlerts;
import com.eltiland.ui.common.model.GenericDBModel;
import com.eltiland.ui.library.panels.RecordPropertyPanel;
import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.request.http.flow.AbortWithHttpErrorCodeException;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletResponse;

/**
 * Edit library record page.
 *
 * @author Aleksey Plotnikov.
 */
public class LibraryEditRecordPage extends BaseEltilandPage {

    private static final Logger LOGGER = LoggerFactory.getLogger(LibraryEditRecordPage.class);

    @SpringBean
    private GenericManager genericManager;

    public static final String MOUNT_PATH = "/recordEdit";
    public static final String PARAM_ID = "id";

    private boolean isCreate = true;

    private IModel<User> currentUserModel = new LoadableDetachableModel<User>() {
        @Override
        protected User load() {
            return EltilandSession.get().getCurrentUser();
        }
    };

    public LibraryEditRecordPage(PageParameters parameters) {
        super(parameters);

        if (currentUserModel.getObject() == null) {
            throw new AbortWithHttpErrorCodeException(HttpServletResponse.SC_NOT_FOUND);
        }

        isCreate = parameters.get(PARAM_ID).toString() == null;

        IModel<LibraryRecord> recordModel = new GenericDBModel<>(LibraryRecord.class);

        if (isCreate) {
            add(new RecordPropertyPanel("propertyPanel") {
                @Override
                protected void onCreateRecord(AjaxRequestTarget target) {
                    if (currentUserModel.getObject().isSuperUser()) {
                        EltiStaticAlerts.registerOKPopup(getString("recordCreated"));
                    } else {
                        EltiStaticAlerts.registerOKPopup(getString("recordCreatedNotPublished"));
                    }
                    target.prependJavaScript("history.go(-2)");
                }
            });
        } else {
            Long id = parameters.get(PARAM_ID).toLong();
            LibraryRecord record = genericManager.getObject(LibraryRecord.class, id);
            if (record == null) {
                String errMsg = String.format("Cannot locate record by given ID");
                LOGGER.error(errMsg);
                throw new WicketRuntimeException(errMsg);
            } else {
                recordModel.setObject(record);
                add(new RecordPropertyPanel("propertyPanel", recordModel) {
                    @Override
                    protected void onSaveRecord(AjaxRequestTarget target) {
                        EltiStaticAlerts.registerOKPopup(getString("recordSaved"));
                        target.prependJavaScript("history.go(-2)");
                    }
                });
            }
        }
    }
}
