package com.eltiland.ui.library.admin;

import com.eltiland.bl.GenericManager;
import com.eltiland.bl.impl.integration.FileUtility;
import com.eltiland.bl.library.LibraryRecordManager;
import com.eltiland.exceptions.EltilandManagerException;
import com.eltiland.model.library.LibraryRecord;
import com.eltiland.model.library.LibraryVideoRecord;
import com.eltiland.ui.common.BaseEltilandPanel;
import com.eltiland.ui.common.components.ResourcesUtils;
import com.eltiland.ui.common.components.dialog.ELTAlerts;
import com.eltiland.ui.common.components.grid.ELTTable;
import com.eltiland.ui.common.components.grid.GridAction;
import com.eltiland.ui.library.panels.view.RecordViewPage;
import com.eltiland.utils.DateUtils;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.table.AbstractColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.PropertyColumn;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.resource.IResourceStream;
import org.apache.wicket.util.resource.ResourceStreamNotFoundException;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Panel for confirming published records.
 *
 * @author Aleksey Plotnikov.
 */
public class RecordConfirmPanel extends BaseEltilandPanel {

    protected static final Logger LOGGER = LoggerFactory.getLogger(RecordConfirmPanel.class);

    @SpringBean
    private LibraryRecordManager libraryRecordManager;
    @SpringBean
    private GenericManager genericManager;
    @SpringBean
    private FileUtility fileUtility;

    private final ELTTable<LibraryRecord> grid;

    /**
     * Panel constructor.
     *
     * @param id markup id.
     */
    public RecordConfirmPanel(String id) {
        super(id);

        grid = new ELTTable<LibraryRecord>("grid", 20) {
            @Override
            protected List<IColumn<LibraryRecord>> getColumns() {
                List<IColumn<LibraryRecord>> list = new ArrayList<>();
                list.add(new PropertyColumn<LibraryRecord>(new ResourceModel("nameColumn"), "name", "name"));
                list.add(new AbstractColumn<LibraryRecord>(new ResourceModel("typeColumn")) {
                    @Override
                    public void populateItem(Item<ICellPopulator<LibraryRecord>> cellItem,
                                             String componentId, IModel<LibraryRecord> rowModel) {
                        cellItem.add(new Label(componentId,
                                getString(rowModel.getObject().getClass().getSimpleName() + ".class")));
                    }
                });
                list.add(new AbstractColumn<LibraryRecord>(new ResourceModel("dateColumn"), "addDate") {
                    @Override
                    public void populateItem(Item<ICellPopulator<LibraryRecord>> cellItem,
                                             String componentId, IModel<LibraryRecord> rowModel) {
                        cellItem.add(new Label(componentId, DateUtils.formatDate(rowModel.getObject().getAddDate())));
                    }
                });
                list.add(new AbstractColumn<LibraryRecord>(new ResourceModel("authorColumn")) {
                    @Override
                    public void populateItem(Item<ICellPopulator<LibraryRecord>> cellItem,
                                             String componentId, IModel<LibraryRecord> rowModel) {
                        genericManager.initialize(rowModel.getObject(), rowModel.getObject().getPublisher());
                        cellItem.add(new Label(componentId, rowModel.getObject().getPublisher().getName()));
                    }
                });
                return list;
            }

            @Override
            protected Iterator getIterator(int first, int count) {
                return libraryRecordManager.getNotConfirmedList( first, count, getSort().getProperty(),
                        getSort().isAscending(), getSearchString()).iterator();
            }

            @Override
            protected int getSize() {
                return libraryRecordManager.getNotConfirmedRecordCount(getSearchString());
            }

            @Override
            protected List<GridAction> getGridActions(IModel<LibraryRecord> rowModel) {
                List<GridAction> actions = new ArrayList<>();

                if (rowModel.getObject() instanceof LibraryVideoRecord) {
                    actions.add(GridAction.PLAY);
                } else {
                    actions.add(GridAction.DOWNLOAD);
                }
                actions.add(GridAction.REMOVE);
                actions.add(GridAction.APPLY);

                return actions;
            }

            @Override
            protected String getActionTooltip(GridAction action) {
                switch (action) {
                    case DOWNLOAD:
                        return getString("download");
                    case REMOVE:
                        return getString("delete");
                    case APPLY:
                        return getString("apply");
                    default:
                        return null;
                }
            }

            @Override
            protected void onClick(final IModel<LibraryRecord> rowModel, GridAction action, AjaxRequestTarget target) {
                switch (action) {
                    case DOWNLOAD:
                        break;
                    case PLAY:
                        throw new RestartResponseException(RecordViewPage.class,
                                new PageParameters().add(RecordViewPage.PARAM_ID, rowModel.getObject().getId()));
                    case REMOVE:
                        try {
                            libraryRecordManager.deleteRecord(rowModel.getObject());
                        } catch (EltilandManagerException e) {
                            LOGGER.error("Cannot delete record", e);
                            throw new WicketRuntimeException(e);
                        }
                        target.add(grid);
                        ELTAlerts.renderOKPopup(getString("recordRemovedMessage"), target);
                        break;
                    case APPLY:
                        rowModel.getObject().setPublishedDate(DateUtils.getCurrentDate());
                        try {
                            libraryRecordManager.saveRecord(rowModel.getObject());
                        } catch (EltilandManagerException e) {
                            LOGGER.error("Cannot save record", e);
                            throw new WicketRuntimeException(e);
                        }
                        target.add(grid);
                        ELTAlerts.renderOKPopup(getString("recordAppliedMessage"), target);
                        break;
                    default:
                        break;
                }
            }

            @Override
            protected boolean hasConfirmation(GridAction action) {
                return action.equals(GridAction.REMOVE);
            }

            @Override
            protected String getFileName(IModel<LibraryRecord> rowModel) {
                genericManager.initialize(rowModel.getObject(), rowModel.getObject().getFileContent());
                return rowModel.getObject().getFileContent().getName();
            }

            @Override
            protected InputStream getInputStream(IModel<LibraryRecord> rowModel) throws ResourceStreamNotFoundException {
                genericManager.initialize(rowModel.getObject(), rowModel.getObject().getFileContent());
                genericManager.initialize(rowModel.getObject().getFileContent(),
                        rowModel.getObject().getFileContent().getBody());
                IResourceStream resourceStream = fileUtility.getFileResource(
                        rowModel.getObject().getFileContent().getBody().getHash());
                return resourceStream.getInputStream();
            }

            @Override
            protected boolean isSearching() {
                return true;
            }

            @Override
            protected String getSearchPlaceHolder() {
                return RecordConfirmPanel.this.getString("searchRecord");
            }
        };

        add(grid.setOutputMarkupId(true));
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
        response.renderCSSReference(ResourcesUtils.CSS_NEW_TABLE_STYLE);
    }
}
