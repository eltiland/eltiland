package com.eltiland.ui.common.components.grid;

import com.eltiland.model.Identifiable;
import com.eltiland.ui.common.BaseEltilandPanel;
import com.eltiland.ui.common.components.ReadonlyObjects;
import com.eltiland.ui.common.components.ResourcesUtils;
import com.eltiland.ui.common.components.behavior.AjaxDownload;
import com.eltiland.ui.common.components.behavior.ConfirmationDialogBehavior;
import com.eltiland.ui.common.components.behavior.TooltipBehavior;
import com.eltiland.ui.common.components.button.EltiAjaxLink;
import com.eltiland.ui.common.components.button.EltiAjaxSubmitLink;
import com.eltiland.ui.common.components.button.EltiSpinAjaxDecorator;
import com.eltiland.ui.common.components.datatable.EltiDataProviderBase;
import com.eltiland.ui.common.components.navigator.ELTAjaxPagingNavigator;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.IAjaxCallDecorator;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.sort.ISortStateLocator;
import org.apache.wicket.extensions.markup.html.repeater.data.table.AbstractColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.DataTable;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.util.SortParam;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.navigation.paging.IPageable;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.util.resource.AbstractResourceStream;
import org.apache.wicket.util.resource.IResourceStream;
import org.apache.wicket.util.resource.ResourceStreamNotFoundException;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * General ELT table control.
 *
 * @author Aleksey Plotnikov.
 */
public abstract class ELTTable<T extends Identifiable> extends Panel {
    /**
     * DataTable source.
     */
    private DataTable<T> table;
    /**
     * Header toolbar.
     */
    private DataTableHeader headerToolbar;

    private ActionPanel controlPanel;

    private Map<Long, Panel> actionPanels = new HashMap<>();

    private TextField<String> searchText = new TextField<>("textSearch", new Model<String>());

    private int maxRows;

    private IModel<Integer> countModel = new LoadableDetachableModel<Integer>() {
        @Override
        protected Integer load() {
            return getSize();
        }
    };

    IModel<String> navigatorLabelModel = new LoadableDetachableModel<String>() {
        @Override
        protected String load() {
            if (!navigatorLabel.isVisible()) {
                return "";
            }


            int fromRow = table.getCurrentPage() * table.getItemsPerPage() + 1;
            int toRow = 0;
            if (table.getPageCount() == table.getCurrentPage() + 1) {
                toRow = table.getItemCount();
            } else {
                toRow = fromRow + table.getItemsPerPage() - 1;
            }
            return String.format(getString("countTemplate"), fromRow, toRow, table.getItemCount());
        }
    };
    private Label navigatorLabel = new Label("navigatorLabel", navigatorLabelModel) {
        @Override
        public boolean isVisible() {
            return table.getPageCount() > 1;
        }
    };

    private boolean isTableVisible() {
        return countModel.getObject() > 0;
    }

    /**
     * Change visibility of table and its header
     */
    private void changeTableVisibility() {
        table.setVisible(countModel.getObject() > 0);
        headerToolbar.setVisible(table.isVisible());
    }


    /**
     * Panel constructor.
     *
     * @param id      markup id.
     * @param maxRows maximum count of rows to output.
     */
    public ELTTable(String id, final int maxRows) {
        super(id);
        this.maxRows = maxRows;

        setOutputMarkupPlaceholderTag(true);

        Form form = new Form("form");

        final List<IColumn<T>> columns = getColumns();

        IColumn<T> firstColumn = getFirstColumn();
        if (firstColumn != null) {
            columns.add(0, firstColumn);
        }

        AbstractColumn<T> actionColumn = new AbstractColumn<T>(ReadonlyObjects.EMPTY_DISPLAY_MODEL) {
            @Override
            public void populateItem(Item<ICellPopulator<T>> cellItem, String componentId, IModel<T> rowModel) {
                Panel panel = new ActionPanel(componentId, rowModel);
                panel.add(new AttributeModifier("style",
                        String.format("display:block;width:%dpx", getGridActions(rowModel).size() * 22)));
                actionPanels.put(rowModel.getObject().getId(), panel);
                cellItem.add(panel.setOutputMarkupPlaceholderTag(true));
            }
        };
        columns.add(actionColumn);

        //create table component and attach to panel view
        table = new DataTable<T>("dataTable", columns, new EltiDataProviderBase<T>() {
            @Override
            public Iterator iterator(int first, int count) {
                return getIterator(first, count);
            }

            @Override
            public int size() {
                return countModel.getObject();
            }
        }, maxRows) {
            @Override
            protected void onConfigure() {
                setVisible(isTableVisible());
            }
        };

        table.setOutputMarkupPlaceholderTag(true);
        form.add(table);

        //create sortable header toolbar and and attach to table
        headerToolbar = new DataTableHeader(this, (ISortStateLocator) table.getDataProvider()) {
            @Override
            protected void onConfigure() {
                setVisible(isTableVisible());
            }
        };
        headerToolbar.setOutputMarkupPlaceholderTag(true);
        table.addTopToolbar(headerToolbar);

        final Label notFoundedContainer = new Label("notFounded", getNotFoundedMessage()) {
            @Override
            public boolean isVisible() {
                return !isTableVisible();
            }
        };

        form.add(notFoundedContainer.setOutputMarkupPlaceholderTag(true));

        final RecordNavigator navigator = new RecordNavigator("navigator", table) {
            @Override
            protected void onEvent(AjaxRequestTarget target) {
                target.add(table);
                target.add(navigatorLabel);
                navigatorLabelModel.detach();
            }
        };

        form.add(navigatorLabel.setOutputMarkupPlaceholderTag(true));
        form.add(navigator.setOutputMarkupPlaceholderTag(true));

        WebMarkupContainer searchContainer = new WebMarkupContainer("searchField") {
            @Override
            public boolean isVisible() {
                return isSearching();
            }
        };

        form.add(searchContainer.setOutputMarkupPlaceholderTag(true));
        searchContainer.add(searchText.setOutputMarkupPlaceholderTag(true));

        if (getSearchPlaceHolder() != null) {
            searchText.add(new AttributeAppender("placeholder", new Model<>(getSearchPlaceHolder())));
        }

        EltiAjaxSubmitLink submitButton = new EltiAjaxSubmitLink("searchButton") {
            @Override
            protected void onSubmit(AjaxRequestTarget target, Form form) {
                onSearch(target);
                countModel.detach();
                target.add(form);
                navigatorLabel.detach();
            }

            @Override
            protected IAjaxCallDecorator getAjaxCallDecorator() {
                return null;
            }
        };

        searchContainer.add(submitButton.setOutputMarkupPlaceholderTag(true));
        form.setDefaultButton(submitButton);

        controlPanel = new ActionPanel("controlPanel") {
            @Override
            public boolean isVisible() {
                return isControlling();
            }
        };
        form.add(controlPanel.setOutputMarkupPlaceholderTag(true));
        form.setMultiPart(true);

        add(form.setOutputMarkupPlaceholderTag(true));
    }

    @Override
    protected void onBeforeRender() {
        countModel.detach();
        super.onBeforeRender();
    }

    public abstract class RecordNavigator extends ELTAjaxPagingNavigator {

        private IPageable pageable;

        public RecordNavigator(String id, IPageable pageable) {
            super(id, pageable);
            this.pageable = pageable;
        }

        @Override
        public boolean isVisible() {
            return table.getDataProvider().size() > maxRows;
        }

        @Override
        protected void onAjaxEvent(AjaxRequestTarget target) {
            super.onAjaxEvent(target);
            onEvent(target);
        }

        protected abstract void onEvent(AjaxRequestTarget target);
    }

    /**
     * You must override it to create columns of data table.
     *
     * @return array of the columns.
     */
    protected abstract List<IColumn<T>> getColumns();

    /**
     * @return first column, prepending all others.
     */
    protected IColumn<T> getFirstColumn() {
        return null;
    }


    /***************************/
    /* data provider stuff */
    /***************************/

    /**
     * You must override it to specify iterator of list of the entities.
     *
     * @param first index of the first item.
     * @param count count of the elements of the list.
     * @return iterator.
     */
    protected abstract Iterator getIterator(int first, int count);

    /**
     * You must override it to specify size of the list of the items.
     *
     * @return size of the list.
     */
    protected abstract int getSize();

    public SortParam getSort() {
        return ((EltiDataProviderBase<T>) table.getDataProvider()).getSort();
    }


    /**
     * @return data table inside control.
     */
    protected DataTable getTable() {
        return table;
    }

    /**
     * @return data table header panel.
     */
    protected DataTableHeader getTableHeader() {
        return headerToolbar;
    }

    /**
     * @return message, which will be shown when no records found.
     */
    protected String getNotFoundedMessage() {
        return getString("notFounded");
    }

    /**
     * @param rowModel row model
     * @return array of actions of the grid.
     */
    protected List<GridAction> getGridActions(IModel<T> rowModel) {
        return new ArrayList<>();
    }

    /**
     * @return TRUE if action will be available
     */
    protected boolean isActionVisible(GridAction action, IModel<T> rowModel) {
        return true;
    }

    /**
     * @return TRUE if control action will be available
     */
    protected boolean isControlActionVisible(GridAction action) {
        return true;
    }

    /**
     * @return Tooltip for action button
     */
    protected String getActionTooltip(GridAction action) {
        return null;
    }

    /**
     * @return TRUE if action has confirmation.
     */
    protected boolean hasConfirmation(GridAction action) {
        return false;
    }

    /***************************/
    /* download stuff */
    /***************************/

    /**
     * @return TRUE if action is Download
     */
    protected boolean isDownload(GridAction action) {
        return action.equals(GridAction.DOWNLOAD);
    }

    /**
     * @return file name
     */
    protected String getFileName(IModel<T> rowModel) {
        return "";
    }

    /**
     * @return input stream
     */
    protected InputStream getInputStream(IModel<T> rowModel) throws ResourceStreamNotFoundException {
        return null;
    }


    /**
     * Grid onclick handler.
     */
    protected abstract void onClick(IModel<T> rowModel, GridAction action, AjaxRequestTarget target);

    /***************************/
    /* search stuff */
    /***************************/

    /**
     * @return TRUE if search panel present;
     */
    protected boolean isSearching() {
        return false;
    }

    /**
     * @return place holder string for search string.
     */
    protected String getSearchPlaceHolder() {
        return null;
    }

    /**
     * @return search String
     */
    public String getSearchString() {
        return searchText.getModelObject();
    }

    /**
     * OnSearch event handler.
     */
    protected void onSearch(AjaxRequestTarget target) {
        target.add(table);
    }

    /***************************/
    /* control stuff */

    /**
     * ***********************
     */

    /**
     * Performs update of the control panel of the ELTTable.
     */
    public void updateControlPanel(AjaxRequestTarget target) {
        controlPanel.updateActions(target);
    }

    /**
     * @return TRUE if control panel present;
     */
    protected boolean isControlling() {
        return false;
    }

    /**
     * @return array of actions of the grid.
     */
    protected List<GridAction> getControlActions() {
        return new ArrayList<>();
    }

    /**
     * Internal action panel.
     */
    private class ActionPanel extends BaseEltilandPanel<T> {

        private WebMarkupContainer actionContainer = new WebMarkupContainer("actionContainer");

        private ListView<GridAction> actionList;

        /**
         * Panel ctor
         *
         * @param id markup id
         */
        private ActionPanel(String id) {
            super(id);
            add(actionContainer.setOutputMarkupPlaceholderTag(true));

            actionList = new ListView<GridAction>("actionList", getControlActions()) {
                @Override
                protected void populateItem(final ListItem<GridAction> item) {
                    Button button = new Button("action", new Model<>(item.getModelObject())) {
                        @Override
                        protected void onClick(GridAction action, AjaxRequestTarget target) {
                            ELTTable.this.onClick(null, action, target);
                        }

                        @Override
                        public boolean isVisible() {
                            return isControlActionVisible(item.getModelObject());
                        }
                    };
                    item.add(button);
                }
            };

            actionContainer.add(actionList);
        }

        /**
         * Panel ctor
         *
         * @param id       markup id
         * @param rowModel row model.
         */
        public ActionPanel(String id, final IModel<T> rowModel) {
            super(id);
            add(actionContainer.setOutputMarkupPlaceholderTag(true));

            actionList = new ListView<GridAction>("actionList",
                    (rowModel != null) ? getGridActions(rowModel) : getControlActions()) {
                @Override
                protected void populateItem(final ListItem<GridAction> item) {
                    Button button = new Button("action", new Model<>(item.getModelObject())) {
                        @Override
                        public boolean isVisible() {
                            return ELTTable.this.isActionVisible(item.getModelObject(), rowModel);
                        }

                        @Override
                        protected void onClick(GridAction action, AjaxRequestTarget target) {
                            ELTTable.this.onClick(rowModel, action, target);
                        }
                    };
                    if (isDownload(item.getModelObject())) {
                        button.ajaxDownload = new AjaxDownload() {
                            @Override
                            protected String getFileName() {
                                return ELTTable.this.getFileName(rowModel);
                            }

                            @Override
                            protected IResourceStream getResourceStream() {
                                return new AbstractResourceStream() {
                                    @Override
                                    public InputStream getInputStream() throws ResourceStreamNotFoundException {
                                        return ELTTable.this.getInputStream(rowModel);
                                    }

                                    @Override
                                    public void close() throws IOException {
                                    }
                                };
                            }
                        };
                        button.add(button.ajaxDownload);
                    }
                    item.add(button);
                }
            };

            actionContainer.add(actionList);
        }

        public void updateActions(AjaxRequestTarget target) {
            actionList.setModelObject(getControlActions());
            target.add(actionContainer);
        }
    }

    /**
     * Internal panel for button on action panel.
     */
    private abstract class Button extends BaseEltilandPanel<GridAction> {

        public AjaxDownload ajaxDownload;

        /**
         * Panel ctor
         *
         * @param id    markup id
         * @param model row model.
         */
        public Button(String id, IModel<GridAction> model) {
            super(id, model);

            final GridAction action = getModelObject();

            EltiAjaxLink button = new EltiAjaxLink("button") {
                @Override
                public void onClick(AjaxRequestTarget target) {
                    if (isDownload(action)) {
                        ajaxDownload.initiate(target);
                    }
                    Button.this.onClick(action, target);
                }

                @Override
                protected IAjaxCallDecorator getAjaxCallDecorator() {
                    return new EltiSpinAjaxDecorator(ELTTable.this);
                }
            };

            add(button.setOutputMarkupPlaceholderTag(true));

            WebMarkupContainer image = new WebMarkupContainer("image");
            image.add(new AttributeAppender("class", new Model<>(action.toString()), " "));
            button.add(image);

            String tooltip = getActionTooltip(action);
            if (tooltip != null) {
                button.add(new AttributeModifier("title", new Model<>(tooltip)));
                button.add(new TooltipBehavior());
            }

            if (hasConfirmation(action)) {
                button.add(new ConfirmationDialogBehavior());
            }
        }

        /**
         * Onclick handler.
         */
        protected abstract void onClick(GridAction action, AjaxRequestTarget target);

        @Override
        public void renderHead(IHeaderResponse response) {
            response.renderJavaScriptReference(ResourcesUtils.JS_INDICATOR);
            response.renderJavaScriptReference(ResourcesUtils.JS_INDICATOR_PACE);
        }
    }
}