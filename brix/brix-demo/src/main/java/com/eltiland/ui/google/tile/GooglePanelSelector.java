package com.eltiland.ui.google.tile;

import com.eltiland.model.google.GooglePage;
import com.eltiland.ui.common.BaseEltilandPanel;
import com.eltiland.ui.common.components.button.EltiAjaxSubmitLink;
import com.eltiland.ui.common.components.datagrid.EltiDefaultDataGrid;
import com.eltiland.ui.common.components.dialog.ELTDialogPanel;
import com.eltiland.ui.common.components.dialog.callback.IDialogSelectCallback;
import com.eltiland.ui.google.components.GoogleDataSource;
import com.inmethod.grid.IGridColumn;
import com.inmethod.grid.column.AbstractColumn;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Panel for selection google page.
 *
 * @author Aleksey Plotnikov.
 */
public class GooglePanelSelector extends ELTDialogPanel implements IDialogSelectCallback<GooglePage> {

    private IDialogActionProcessor<GooglePage> callback;
    private EltiDefaultDataGrid<GoogleDataSource, GooglePage> grid;

    private Form form2 = new Form("form");

    private TextField<String> searchField = new TextField("searchField", new Model<String>());
    private EltiAjaxSubmitLink searchButton = new EltiAjaxSubmitLink("searchButton") {
        @Override
        protected void onSubmit(AjaxRequestTarget target, Form form) {
            target.add(grid);
        }
    };

    public GooglePanelSelector(String id) {
        super(id);

        List<IGridColumn<GoogleDataSource, GooglePage>> columns = new ArrayList<>();

        columns.add(new AbstractColumn<GoogleDataSource, GooglePage>(
                "nameColumn", new ResourceModel("nameLabel"), "name") {
            @Override
            public Component newCell(WebMarkupContainer parent, String componentId, IModel<GooglePage> rowModel) {
                return new NamePanel(componentId, rowModel);
            }

            @Override
            public int getInitialSize() {
                return 350;
            }
        });

        grid = new EltiDefaultDataGrid<GoogleDataSource, GooglePage>("grid", new GoogleDataSource() {
            @Override
            public TextField getSearchField() {
                return searchField;
            }
        }, columns) {
            @Override
            protected boolean onCellClicked(AjaxRequestTarget target, IModel<GooglePage> rowModel,
                                            IGridColumn<GoogleDataSource, GooglePage> column) {
                callback.process(rowModel, target);
                return super.onCellClicked(target, rowModel, column);
            }
        };
        form.add(grid);

        form.add(form2);
        form2.add(searchField);
        form2.add(searchButton);
    }

    @Override
    protected String getHeader() {
        return getString("selectHeader");
    }

    @Override
    protected List<EVENT> getActionList() {
        return new ArrayList<>();
    }

    @Override
    protected void eventHandler(EVENT event, AjaxRequestTarget target) {
    }

    @Override
    public void setSelectCallback(IDialogActionProcessor<GooglePage> callback) {
        this.callback = callback;
    }

    private class NamePanel extends BaseEltilandPanel<GooglePage> {
        protected NamePanel(String id, IModel<GooglePage> googlePageIModel) {
            super(id, googlePageIModel);
            add(new Label("label", getModelObject().getName()));
        }
    }

    @Override
    protected boolean actionSelector(EVENT event) {
        return false;
    }
}
