package com.eltiland.ui.course.plugin.tab.test;

import com.eltiland.bl.GenericManager;
import com.eltiland.model.course.test.UserTestAttempt;
import com.eltiland.ui.common.components.grid.ELTTable;
import com.eltiland.ui.common.components.grid.GridAction;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.PropertyColumn;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Data grid for attempts.
 *
 * @author Aleksey Plotnikov.
 */
public abstract class AttemptDataGrid extends ELTTable<UserTestAttempt> {

    @SpringBean
    private GenericManager genericManager;

    /**
     * Panel constructor.
     *
     * @param id           markup id.
     * @param maxRows      maximum count of rows to output.
     */
    public AttemptDataGrid(String id, int maxRows) {
        super(id, maxRows);
    }

    @Override
    protected List<IColumn<UserTestAttempt>> getColumns() {
        List<IColumn<UserTestAttempt>> columns = new ArrayList<>();

        columns.add(new PropertyColumn<UserTestAttempt>(
                new ResourceModel("nameColumn"), "user.name", "user.name"));
        columns.add(new PropertyColumn<UserTestAttempt>(
                new ResourceModel("courseColumn"), "course.name", "test.courseFull.name"));
        columns.add(new PropertyColumn<UserTestAttempt>(
                new ResourceModel("testColumn"), "test.name", "test.name"));

        return columns;
    }

    @Override
    protected void onClick(IModel<UserTestAttempt> rowModel, GridAction action, AjaxRequestTarget target) {
    }

    @Override
    protected boolean isSearching() {
        return true;
    }

    @Override
    protected String getSearchPlaceHolder() {
        return getString("searchUser");
    }

    @Override
    public String getSearchString() {
        return super.getSearchString();
    }
}