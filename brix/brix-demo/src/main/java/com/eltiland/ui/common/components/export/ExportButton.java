package com.eltiland.ui.common.components.export;

import com.eltiland.model.export.Exportable;
import com.eltiland.ui.common.BaseEltilandPanel;
import com.eltiland.ui.common.components.button.EltiAjaxLink;
import com.eltiland.ui.common.components.dialog.Dialog;
import org.apache.wicket.ajax.AjaxRequestTarget;

/**
 * Export to CSV button.
 *
 * @author Aleksey Plotnikov.
 */
public class ExportButton extends BaseEltilandPanel {

    private Class<? extends Exportable> clazz;

    private Dialog<ExportPeriodPanel> periodDialog = new Dialog<ExportPeriodPanel>("periodDialog", 320) {
        @Override
        public ExportPeriodPanel createDialogPanel(String id) {
            return new ExportPeriodPanel(id, clazz);
        }
    };

    public ExportButton(String id, Class<? extends Exportable> clazz) {
        super(id);
        this.clazz = clazz;

        add(new EltiAjaxLink("downloadButton") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                periodDialog.show(target);
            }
        });

        add(periodDialog);
    }
}
