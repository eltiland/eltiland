package com.eltiland.ui.google.buttons;

import com.eltiland.model.google.GoogleDriveFile;
import com.eltiland.ui.common.BaseEltilandPanel;
import com.eltiland.ui.common.components.ResourcesUtils;
import com.eltiland.ui.common.components.button.EltiAjaxLink;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.IAjaxCallDecorator;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.model.IModel;

/**
 * Google print button control.
 *
 * @author Aleksey Plotnikov.
 */
public class GooglePrintButton extends BaseEltilandPanel<GoogleDriveFile> {
    public GooglePrintButton(String id, IModel<GoogleDriveFile> googleDriveFileIModel) {
        super(id, googleDriveFileIModel);

        add(new EltiAjaxLink("printButton") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                target.appendJavaScript("printCourseItemContent()");
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
}
