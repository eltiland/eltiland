package com.eltiland.ui.library.view.kind;

import com.eltiland.model.library.LibraryRecord;
import com.eltiland.ui.common.BaseEltilandPanel;
import com.eltiland.ui.library.SearchData;
import com.eltiland.ui.library.view.kind.grid.RecordGridPanel;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.model.IModel;

/**
 * Grid view record panel.
 *
 * @author Aleksey Plotnikov.
 */
public class RecordGridViewPanel extends AbstractRecordViewPanel {

    private static int VIEW_COUNT = 50;

    /**
     * Panel constructor.
     *
     * @param id markup id.
     */
    public RecordGridViewPanel(String id, IModel<SearchData> searchDataIModel) {
        super(id, searchDataIModel);
    }

    @Override
    protected void onChange(AjaxRequestTarget target) {
    }

    @Override
    protected int getViewCount() {
        return VIEW_COUNT;
    }

    @Override
    protected BaseEltilandPanel getPanel(String markupId, IModel<LibraryRecord> recordIModel) {
        return new RecordGridPanel(markupId, recordIModel);
    }
}
