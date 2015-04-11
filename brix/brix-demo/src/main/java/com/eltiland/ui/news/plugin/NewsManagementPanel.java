package com.eltiland.ui.news.plugin;

import com.eltiland.bl.NewsManager;
import com.eltiland.exceptions.EltilandManagerException;
import com.eltiland.exceptions.NewsException;
import com.eltiland.model.NewsItem;
import com.eltiland.ui.common.BaseEltilandPanel;
import com.eltiland.ui.common.CKEditorFull;
import com.eltiland.ui.common.CKEditorInline;
import com.eltiland.ui.common.column.PropertyWrapColumn;
import com.eltiland.ui.common.column.date.EditableDatePropertyColumn;
import com.eltiland.ui.common.components.PreviewPanel;
import com.eltiland.ui.common.components.ReadonlyObjects;
import com.eltiland.ui.common.components.ResourcesUtils;
import com.eltiland.ui.common.components.UIConstants;
import com.eltiland.ui.common.components.behavior.ConfirmationDialogBehavior;
import com.eltiland.ui.common.components.button.EltiAjaxSubmitLink;
import com.eltiland.ui.common.components.datepicker.DatePickerField;
import com.eltiland.ui.common.components.dialog.Dialog;
import com.eltiland.ui.common.components.dialog.ELTAlerts;
import com.eltiland.ui.common.components.dialog.ELTDialogPanel;
import com.eltiland.ui.common.components.dialog.EltiStaticAlerts;
import com.eltiland.ui.common.components.dialog.callback.IDialogSimpleNewCallback;
import com.eltiland.ui.common.components.dialog.callback.IDialogSimpleUpdateCallback;
import com.eltiland.ui.common.components.feedbacklabel.ELTFeedbackLabel;
import com.eltiland.ui.common.components.grid.ELTTable;
import com.eltiland.ui.common.components.grid.GridAction;
import com.eltiland.ui.common.components.textfield.ELTDateField;
import com.eltiland.ui.common.components.textfield.ELTTextArea;
import com.eltiland.ui.common.components.textfield.ELTTextField;
import com.eltiland.ui.common.model.GenericDBModel;
import com.inmethod.grid.IDataSource;
import com.inmethod.grid.IGridColumn;
import com.inmethod.grid.IGridSortState;
import com.inmethod.grid.SizeUnit;
import com.inmethod.grid.column.CheckBoxColumn;
import com.inmethod.grid.column.editable.EditablePropertyColumn;
import com.inmethod.grid.column.editable.SubmitCancelColumn;
import com.inmethod.grid.datagrid.DataGrid;
import com.inmethod.grid.datagrid.DefaultDataGrid;
import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.table.AbstractColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.PropertyColumn;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.*;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.validation.validator.StringValidator;
import org.brixcms.workspace.Workspace;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Panel manages news. Can delete, create, edit news items.
 */
public class NewsManagementPanel extends BaseEltilandPanel<Workspace> {
    @SpringBean
    private NewsManager newsManager;

    private ELTTable<NewsItem> grid;

    private Dialog<PreviewPanel> previewDialog = new Dialog<PreviewPanel>("previewDialog",
            UIConstants.DIALOG_BIG_WIDTH) {
        @Override
        public PreviewPanel createDialogPanel(String id) {
            return new PreviewPanel(id);
        }
    };

    private Dialog<EditNewsPanel> addNewsItem = new Dialog<EditNewsPanel>("addNewsItem",
            UIConstants.DIALOG_POPUP_WIDTH) {
        @Override
        public EditNewsPanel createDialogPanel(String id) {
            return new EditNewsPanel(id, addNewsItem);
        }

        @Override
        public void registerCallback(EditNewsPanel panel) {
            super.registerCallback(panel);
            panel.setSimpleNewCallback(new IDialogSimpleNewCallback.IDialogActionProcessor<NewsItem>() {

                @Override
                public void process(IModel<NewsItem> model, AjaxRequestTarget target) {
                    try {
                        newsManager.createNewsItem(model.getObject());
                        EltiStaticAlerts.registerOKPopup(getString("createdSuccessfullyMessage"));
                    } catch (NewsException e) {
                        EltiStaticAlerts.registerErrorPopup(e.getMessage());
                    }
                    close(target);
                    target.add(grid);
                }
            });
            panel.setSimpleUpdateCallback(new IDialogSimpleUpdateCallback.IDialogActionProcessor<NewsItem>() {
                @Override
                public void process(IModel<NewsItem> model, AjaxRequestTarget target) {
                    try {
                        newsManager.updateNewsItem(model.getObject());
                        EltiStaticAlerts.registerOKPopup(getString("editedSuccessfullyMessage"));
                    } catch (NewsException e) {
                        EltiStaticAlerts.registerErrorPopup(e.getMessage());
                    }
                    close(target);
                    target.add(grid);
                }
            });
        }
    };

    public NewsManagementPanel(String id, IModel<Workspace> model) {
        super(id, model);

        grid = new ELTTable<NewsItem>("grid", 10) {
            @Override
            protected boolean isSearching() {
                return true;
            }

            @Override
            protected String getActionTooltip(GridAction action) {
                switch (action) {
                    case PREVIEW:
                        return getString("previewTooltip");
                    case EDIT:
                        return getString("editTooltip");
                    case REMOVE:
                        return getString("removeTooltip");
                    default:
                        return "";
                }
            }

            @Override
            protected List<IColumn<NewsItem>> getColumns() {
                List<IColumn<NewsItem>> columns = new ArrayList<>();

                columns.add(new PropertyColumn<NewsItem>(new ResourceModel("titleColumn"), "title", "title"));
                columns.add(new PropertyColumn<NewsItem>(new ResourceModel("dateColumn"), "date", "date"));
                columns.add(new AbstractColumn<NewsItem>(new ResourceModel("typeColumn"), "announcement") {
                    @Override
                    public void populateItem(Item<ICellPopulator<NewsItem>> components, String s,
                                             IModel<NewsItem> newsItemIModel) {
                        String data = newsItemIModel.getObject().getAnnouncement() ? getString("isAnnounce") :
                                getString("isNews");
                        components.add(new Label(s, new Model<>(data)));
                    }
                });
                return columns;
            }

            @Override
            protected Iterator getIterator(int first, int count) {
                return newsManager.getNewsList(first, count, getSort().getProperty(),
                        getSort().isAscending(), getSearchString()).iterator();
            }

            @Override
            protected int getSize() {
                return newsManager.getNewsListCount(getSearchString());
            }

            @Override
            protected void onClick(IModel<NewsItem> rowModel, GridAction action, AjaxRequestTarget target) {
                switch (action) {
                    case PREVIEW:
                        previewDialog.getDialogPanel().setData(Model.of(rowModel.getObject().getBody()));
                        previewDialog.show(target);

                        break;
                    case EDIT:
                        addNewsItem.getDialogPanel().setItemModel(rowModel);
                        addNewsItem.show(target);

                        break;
                    case ADD:
                        addNewsItem.getDialogPanel().prepare();
                        addNewsItem.show(target);

                        break;
                    case REMOVE:
                        try {
                            newsManager.deleteNewsItem(rowModel.getObject());
                        } catch (NewsException e) {
                            ELTAlerts.renderErrorPopup(e.getMessage(), target);
                        }

                        target.add(grid);

                        break;
                }
            }

            @Override
            protected List<GridAction> getControlActions() {
                return Arrays.asList(GridAction.ADD);
            }

            @Override
            protected boolean isControlling() {
                return true;
            }

            @Override
            protected List<GridAction> getGridActions(IModel<NewsItem> rowModel) {
                return Arrays.asList(GridAction.PREVIEW, GridAction.EDIT, GridAction.REMOVE);
            }

            @Override
            protected boolean hasConfirmation(GridAction action) {
                switch (action) {
                    case REMOVE:
                        return true;
                    default:
                        return false;
                }
            }
        };

        add(grid);
        add(addNewsItem);
        add(previewDialog);
    }

    private class EditNewsPanel extends ELTDialogPanel implements IDialogSimpleNewCallback<NewsItem>,
            IDialogSimpleUpdateCallback<NewsItem> {
        private IModel<NewsItem> itemModel;
        private IDialogSimpleNewCallback.IDialogActionProcessor<NewsItem> newCallback;
        private IDialogSimpleUpdateCallback.IDialogActionProcessor<NewsItem> updateCallback;

        private ELTTextArea title = new ELTTextArea("title", new ResourceModel("title"),
                new Model<String>(), true);
        private ELTDateField dateField = new ELTDateField("dateField", new ResourceModel("dateField"),
                new Model<Date>(), true);
        private CKEditorFull body;
        private Label annLabel = new Label("annLabel", new ResourceModel("announcement"));
        private CheckBox announcement = new CheckBox("announcement", new Model<>(false));


        public EditNewsPanel(String id, Dialog dialog) {
            super(id);
            form.add(title);
            form.add(dateField);
            form.add(annLabel);
            form.add(announcement);
            body = new CKEditorFull("body", dialog);
            form.add(body);
        }

        public void prepare() {
            itemModel = null;
            title.setModelObject("");
            dateField.setModelObject(null);
            announcement.setModelObject(false);
            body.setData("");
        }

        public void setItemModel(IModel<NewsItem> model) {
            itemModel = model;
            NewsItem item = itemModel.getObject();
            title.setModelObject(item.getTitle());
            dateField.setModelObject(item.getDate());
            announcement.setModelObject(item.getAnnouncement());
            body.setData(item.getBody());
        }

        @Override
        protected String getHeader() {
            return itemModel == null ? NewsManagementPanel.this.getString("createNewsPanelHeader") :
                    NewsManagementPanel.this.getString("editNewsPanelHeader");
        }

        @Override
        protected List<EVENT> getActionList() {
            return Arrays.asList(EVENT.Save);
        }

        @Override
        protected void eventHandler(EVENT event, AjaxRequestTarget target) {
            switch (event) {
                case Save:
                    boolean updating = false;
                    NewsItem newsItem;
                    if (itemModel == null) {
                        newsItem = new NewsItem();
                        itemModel = new GenericDBModel<>(NewsItem.class, newsItem);
                    } else {
                        updating = true;
                        newsItem = itemModel.getObject();
                    }

                    newsItem.setTitle(title.getModelObject());
                    newsItem.setDate(dateField.getModelObject());
                    newsItem.setBody(body.getData());
                    newsItem.setAnnouncement(announcement.getModelObject());

                    if (updating) {
                        updateCallback.process(itemModel, target);
                    } else {
                        newCallback.process(itemModel, target);
                    }

                    break;
            }
        }

        @Override
        public void setSimpleNewCallback(IDialogSimpleNewCallback.IDialogActionProcessor<NewsItem> callback) {
            this.newCallback = callback;
        }

        @Override
        public void setSimpleUpdateCallback(IDialogSimpleUpdateCallback.IDialogActionProcessor<NewsItem> callback) {
            this.updateCallback = callback;
        }
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        response.renderCSSReference(ResourcesUtils.CSS_TOOLTIP);
        response.renderCSSReference(ResourcesUtils.CSS_TOOLTUP_BOX);
        response.renderCSSReference(ResourcesUtils.CSS_NEW_TABLE_STYLE);
    }
}
