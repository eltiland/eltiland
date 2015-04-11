package com.eltiland.ui.library.view.kind;

import com.eltiland.model.library.LibraryRecord;
import com.eltiland.ui.common.BaseEltilandPanel;
import com.eltiland.ui.library.SearchData;
import com.eltiland.ui.library.view.kind.list.RecordListPanel;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.model.IModel;

/**
 * List view record panel.
 *
 * @author Aleksey Plotnikov.
 */
public abstract class RecordListViewPanel extends AbstractRecordViewPanel {

    private static int VIEW_COUNT = 10;

    /**
     * Panel ctor.
     *
     * @param id markup id.
     */
    public RecordListViewPanel(String id, IModel<SearchData> searchDataIModel) {
        super(id, searchDataIModel);
    }

    @Override
    protected int getViewCount() {
        return VIEW_COUNT;
    }

    @Override
    protected BaseEltilandPanel getPanel(String markupId, IModel<LibraryRecord> recordIModel) {
        return new RecordListPanel(markupId, recordIModel) {
            @Override
            protected void onChange(AjaxRequestTarget target) {
                RecordListViewPanel.this.onChange(target);
            }

            @Override
            protected void onChangeList(AjaxRequestTarget target) {
                recordModel.detach();
                recordCountModel.detach();
                target.add(listContainer);
                target.add(navigatorTop);
                target.add(navigatorDown);
            }
        };
    }
}
