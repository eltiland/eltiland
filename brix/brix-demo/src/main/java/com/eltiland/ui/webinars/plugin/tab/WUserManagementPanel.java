package com.eltiland.ui.webinars.plugin.tab;

import com.eltiland.bl.user.UserManager;
import com.eltiland.model.user.User;
import com.eltiland.model.webinar.Webinar;
import com.eltiland.ui.common.BaseEltilandPanel;
import com.eltiland.ui.common.components.ReadonlyObjects;
import com.eltiland.ui.common.components.button.EltiAjaxLink;
import com.eltiland.ui.common.components.button.EltiAjaxSubmitLink;
import com.eltiland.ui.common.components.datagrid.EltiDefaultDataGrid;
import com.eltiland.ui.common.components.dialog.Dialog;
import com.eltiland.ui.common.model.GenericDBListModel;
import com.eltiland.ui.common.model.GenericDBModel;
import com.eltiland.ui.webinars.plugin.tab.components.UserWebinarListPanel;
import com.eltiland.ui.webinars.plugin.tab.components.WebinarSelector;
import com.inmethod.grid.IDataSource;
import com.inmethod.grid.IGridColumn;
import com.inmethod.grid.IGridSortState;
import com.inmethod.grid.column.AbstractColumn;
import com.inmethod.grid.column.PropertyColumn;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.brixcms.workspace.Workspace;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Webinars user management panel.
 *
 * @author Aleksey Plotnikov
 */
public class WUserManagementPanel extends BaseEltilandPanel<Workspace> {

    @SpringBean
    private UserManager userManager;

    private TextField<String> searchField = new TextField("searchField", new Model<String>());
    private final EltiDefaultDataGrid<WebinarUserDataSource, User> grid;

    private IModel<List<Webinar>> filterWebinars = new GenericDBListModel<>(Webinar.class);

    private EltiAjaxSubmitLink searchButton = new EltiAjaxSubmitLink("searchButton") {
        @Override
        protected void onSubmit(AjaxRequestTarget target, Form form) {
            target.add(grid);
        }
    };

    private Dialog<UserWebinarListPanel> listPanelDialog = new Dialog<UserWebinarListPanel>("webinarListDialog", 420) {
        @Override
        public UserWebinarListPanel createDialogPanel(String id) {
            return new UserWebinarListPanel(id);
        }
    };

    /**
     * Panel constructor.
     *
     * @param id              panel's ID.
     * @param workspaceIModel workspace model.
     */
    public WUserManagementPanel(String id, IModel<Workspace> workspaceIModel) {
        super(id, workspaceIModel);

        List<IGridColumn<WebinarUserDataSource, User>> columns = new ArrayList<>();

        columns.add(new PropertyColumn<WebinarUserDataSource, User, String>
                (new ResourceModel("nameColumn"), "name", "name") {
            @Override
            public int getInitialSize() {
                return 320;
            }
        });
        columns.add(new PropertyColumn<WebinarUserDataSource, User, String>
                (new ResourceModel("emailColumn"), "email", "email") {
            @Override
            public int getInitialSize() {
                return 200;
            }
        });
        columns.add(new AbstractColumn<WebinarUserDataSource, User>(
                "actionColumn", ReadonlyObjects.EMPTY_DISPLAY_MODEL) {
            @Override
            public Component newCell(WebMarkupContainer parent, String componentId, final IModel<User> rowModel) {
                return new ActionPanel(componentId) {
                    @Override
                    protected void onClick(AjaxRequestTarget target) {
                        listPanelDialog.getDialogPanel().initUserData(rowModel.getObject());
                        listPanelDialog.show(target);
                    }
                };
            }
        });

        grid = new EltiDefaultDataGrid<>("grid", new Model<>(new WebinarUserDataSource()), columns);
        add(grid.setOutputMarkupId(true));

        add(new FilterPanel("filterPanel") {
            @Override
            protected void onApply(List<Webinar> webinars, AjaxRequestTarget target) {
                filterWebinars.setObject(webinars);
                target.add(grid);
            }
        });

        Form form = new Form("form");
        add(form);
        form.add(searchButton);
        form.add(searchField);
        add(listPanelDialog);
    }

    private class WebinarUserDataSource implements IDataSource<User> {

        @Override
        public void query(IQuery query, IQueryResult<User> result) {
            String searchString = searchField.getModelObject();
            int count = userManager.getUserCountOnWebinars(searchString, filterWebinars.getObject());
            result.setTotalCount(count);

            if (count < 1) {
                result.setItems(Collections.<User>emptyIterator());
            }

            String sortProperty = "id";
            boolean isAscending = false;

            if (!query.getSortState().getColumns().isEmpty()) {
                IGridSortState.ISortStateColumn sortingColumn = query.getSortState().getColumns().get(0);
                sortProperty = sortingColumn.getPropertyName();
                isAscending = sortingColumn.getDirection() == IGridSortState.Direction.ASC;
            }

            result.setItems(userManager.getUserListOnWebinars(searchString, query.getFrom(), query.getCount(),
                    sortProperty, isAscending, filterWebinars.getObject()).iterator());
        }

        @Override
        public IModel<User> model(User object) {
            return new GenericDBModel<>(User.class, object);
        }

        @Override
        public void detach() {
        }
    }

    private abstract class ActionPanel extends BaseEltilandPanel {

        public ActionPanel(String id) {
            super(id);
            add(new EltiAjaxLink("webinarLink") {
                @Override
                public void onClick(AjaxRequestTarget target) {
                    ActionPanel.this.onClick(target);
                }
            });
        }

        protected abstract void onClick(AjaxRequestTarget target);
    }

    private abstract class FilterPanel extends BaseEltilandPanel {

        private List<WebinarSelector> selectors = new ArrayList<>(5);
        private short count = 1;

        private Form form = new Form("form");

        protected FilterPanel(String id) {
            super(id);

            form.setMultiPart(true);
            add(form);

            for (int i = 0; i < 5; i++) {
                selectors.add(new WebinarSelector("selector" + String.valueOf(i + 1),
                        ReadonlyObjects.EMPTY_DISPLAY_MODEL, new GenericDBModel<>(Webinar.class)));
            }

            form.add(new EltiAjaxSubmitLink("applyButton") {
                @Override
                protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                    List<Webinar> webinars = new ArrayList<>();
                    for (WebinarSelector selector : selectors) {
                        if (selector.getModelObject() != null) {
                            webinars.add(selector.getModelObject());
                        }
                    }

                    onApply(webinars, target);
                }
            });

            form.add(new EltiAjaxLink("addLink") {
                @Override
                public void onClick(AjaxRequestTarget target) {
                    if (count < 5) {
                        count++;
                        selectors.get(count - 1).setVisible(true);
                        target.add(selectors.get(count - 1));
                    }
                }
            });

            for (int i = 0; i < 5; i++) {
                if (i > 0) {
                    selectors.get(i).setVisible(false);
                }
                form.add(selectors.get(i).setOutputMarkupPlaceholderTag(true));
            }
        }

        protected abstract void onApply(List<Webinar> webinars, AjaxRequestTarget target);
    }
}
