package com.eltiland.ui.google.buttons;

import com.eltiland.model.google.GoogleDriveFile;
import com.eltiland.ui.common.BaseEltilandPanel;
import com.eltiland.ui.common.components.ResourcesUtils;
import com.eltiland.ui.common.components.button.EltiAjaxLink;
import com.eltiland.ui.common.components.dialog.ELTAlerts;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.IAjaxCallDecorator;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.model.IModel;

/**
 * Google print button control.
 *
 * @author Aleksey Plotnikov.
 */
public abstract class GooglePrintButton extends BaseEltilandPanel<GoogleDriveFile> {
    public GooglePrintButton(String id, IModel<GoogleDriveFile> googleDriveFileIModel) {
        super(id, googleDriveFileIModel);

        add(new EltiAjaxLink("printButton") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                Long limit = getLimit();
                if (limit == null || limit == 0) {
                    target.appendJavaScript("printCourseItemContent()");
                } else {
                    if (getCurrentPrint(target) < limit) {
                        target.appendJavaScript("printCourseItemContent()");
                        onAfterPrint(target);
                    } else {
                        ELTAlerts.renderErrorPopup(getString("error.limit.print"), target);
                    }
                }
            }

            @Override
            protected IAjaxCallDecorator getAjaxCallDecorator() {
                return null;
            }
        });
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
        response.renderCSSReference(ResourcesUtils.CSS_ICONPANEL);
    }

    protected abstract Long getCurrentPrint(AjaxRequestTarget target);

    protected abstract void onAfterPrint(AjaxRequestTarget target);

    protected abstract Long getLimit();
}
