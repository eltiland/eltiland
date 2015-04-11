package com.eltiland.ui.library.admin;

import com.eltiland.bl.GenericManager;
import com.eltiland.bl.impl.integration.IndexCreator;
import com.eltiland.bl.library.LibraryCollectionManager;
import com.eltiland.exceptions.ConstraintException;
import com.eltiland.exceptions.EltilandManagerException;
import com.eltiland.model.library.LibraryCollection;
import com.eltiland.ui.common.BaseEltilandPanel;
import com.eltiland.ui.common.components.ResourcesUtils;
import com.eltiland.ui.common.components.dialog.Dialog;
import com.eltiland.ui.common.components.dialog.ELTAlerts;
import com.eltiland.ui.common.components.dialog.callback.IDialogNewCallback;
import com.eltiland.ui.common.components.dialog.callback.IDialogUpdateCallback;
import com.eltiland.ui.common.components.grid.GridAction;
import com.eltiland.ui.common.model.GenericDBModel;
import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * Record panel.
 *
 * @author Aleksey Plotnikov.
 */
public class CollectionPanel extends BaseEltilandPanel {

    protected static final Logger LOGGER = LoggerFactory.getLogger(CollectionPanel.class);

    @SpringBean
    private LibraryCollectionManager libraryCollectionManager;
    @SpringBean
    private GenericManager genericManager;
    @SpringBean
    private IndexCreator indexCreator;

    private final CollectionGridTable grid;
    private final CollectionGridTable subGrid;

    private WebMarkupContainer subCollectionContainer = new WebMarkupContainer("subCollectionContainer");

    private IModel<LibraryCollection> currentCollectionModel = new GenericDBModel<>(LibraryCollection.class);
    private IModel<String> currentCollectionName = new LoadableDetachableModel<String>() {
        @Override
        protected String load() {
            return (currentCollectionModel.getObject() != null) ?
                    String.format(getString("childrenLabel"), currentCollectionModel.getObject().getName()) : "";
        }
    };
    private IModel<List<LibraryCollection>> subCollectionsModel =
            new LoadableDetachableModel<List<LibraryCollection>>() {
                @Override
                protected List<LibraryCollection> load() {
                    if (currentCollectionModel.getObject() != null) {
                        LibraryCollection collection = genericManager.getObject(
                                LibraryCollection.class, currentCollectionModel.getObject().getId());
                        genericManager.initialize(collection, collection.getSubCollections());
                        return new ArrayList<>(collection.getSubCollections());
                    } else {
                        return new ArrayList<>();
                    }
                }
            };

    private Dialog<CollectionPropertyPanel> collectionPropertyPanelDialog =
            new Dialog<CollectionPropertyPanel>("collectionDialog", 320) {
                @Override
                public CollectionPropertyPanel createDialogPanel(String id) {
                    return new CollectionPropertyPanel(id);
                }

                @Override
                public void registerCallback(CollectionPropertyPanel panel) {
                    super.registerCallback(panel);
                    panel.setNewCallback(new IDialogNewCallback.IDialogActionProcessor<LibraryCollection>() {
                        @Override
                        public void process(IModel<LibraryCollection> model, AjaxRequestTarget target) {
                            if (currentCollectionModel.getObject() != null) {
                                model.getObject().setParent(currentCollectionModel.getObject());
                            }
                            try {
                                libraryCollectionManager.createCollection(model.getObject());
                            } catch (EltilandManagerException e) {
                                LOGGER.error("Cannot create collection", e);
                                throw new WicketRuntimeException(e);
                            }
                            close(target);
                            ELTAlerts.renderOKPopup(getString("newCollectionMessage"), target);
                            target.add(grid);
                            subCollectionsModel.detach();
                            target.add(subGrid);
                        }
                    });
                    panel.setUpdateCallback(new IDialogUpdateCallback.IDialogActionProcessor<LibraryCollection>() {
                        @Override
                        public void process(IModel<LibraryCollection> model, AjaxRequestTarget target) {
                            try {
                                genericManager.update(model.getObject());
                                indexCreator.doRebuildIndex(LibraryCollection.class);
                            } catch (ConstraintException e) {
                                LOGGER.error("Cannot update collection", e);
                                throw new WicketRuntimeException(e);
                            }
                            close(target);
                            ELTAlerts.renderOKPopup(getString("updateCollectionMessage"), target);
                            target.add(grid);
                            target.add(subGrid);
                        }
                    });
                }
            };

    /**
     * Panel constructor.
     *
     * @param id markup id.
     */
    public CollectionPanel(String id) {
        super(id);

        add(collectionPropertyPanelDialog);

        grid = new CollectionGridTable("grid", 20) {
            @Override
            protected List<GridAction> getGridActions(IModel<LibraryCollection> rowModel) {
                return new ArrayList<>(Arrays.asList(GridAction.CHILDREN, GridAction.EDIT, GridAction.REMOVE));
            }

            @Override
            protected String getActionTooltip(GridAction action) {
                if (action.equals(GridAction.ADD)) {
                    return getString("newCollection");
                } else return super.getActionTooltip(action);
            }

            @Override
            protected Iterator getIterator(int first, int count) {
                return libraryCollectionManager.getLibraryCollectionList(first, count,
                        getSort().getProperty(), getSort().isAscending(), getSearchString(), null).iterator();
            }

            @Override
            protected int getSize() {
                return libraryCollectionManager.getLibraryCollectionCount(getSearchString(), null, true);
            }

            @Override
            protected String getNotFoundedMessage() {
                return getString("notFounded");
            }

            @Override
            protected void onClick(IModel<LibraryCollection> rowModel, GridAction action, AjaxRequestTarget target) {
                switch (action) {
                    case ADD:
                        currentCollectionModel.setObject(null);
                        subCollectionContainer.setVisible(false);
                        target.add(subCollectionContainer);

                        collectionPropertyPanelDialog.getDialogPanel().initCreateMode();
                        collectionPropertyPanelDialog.show(target);
                        break;
                    case EDIT:
                        collectionPropertyPanelDialog.getDialogPanel().initEditMode(rowModel);
                        collectionPropertyPanelDialog.show(target);
                        break;
                    case REMOVE:
                        try {
                            genericManager.initialize(rowModel.getObject(), rowModel.getObject().getSubCollections());
                            for (LibraryCollection subCollection : rowModel.getObject().getSubCollections()) {
                                libraryCollectionManager.removeCollection(subCollection);
                            }
                            LibraryCollection toDelete = genericManager.getObject(
                                    LibraryCollection.class, rowModel.getObject().getId());
                            libraryCollectionManager.removeCollection(toDelete);
                        } catch (EltilandManagerException e) {
                            LOGGER.error("Cannot delete collection", e);
                            throw new WicketRuntimeException(e);
                        }

                        target.add(grid);
                        subCollectionContainer.setVisible(false);
                        target.add(subCollectionContainer);
                        ELTAlerts.renderOKPopup(getString("removeCollectionMessage"), target);
                        break;
                    case CHILDREN:
                        currentCollectionModel.setObject(rowModel.getObject());
                        currentCollectionName.detach();
                        subCollectionsModel.detach();
                        subCollectionContainer.setVisible(true);
                        target.add(subCollectionContainer);
                        break;
                    default:
                        break;
                }
            }
        };

        add(subCollectionContainer);
        subCollectionContainer.setVisible(false);
        subCollectionContainer.setOutputMarkupPlaceholderTag(true);

        subGrid = new CollectionGridTable("subGrid", 20) {
            @Override
            protected List<GridAction> getGridActions(IModel<LibraryCollection> rowModel) {
                return new ArrayList<>(Arrays.asList(GridAction.EDIT, GridAction.REMOVE));
            }

            @Override
            protected Iterator getIterator(int first, int count) {
                return libraryCollectionManager.getLibraryCollectionList(first, count, getSort().getProperty(),
                        getSort().isAscending(), getSearchString(),
                        currentCollectionModel.getObject()).iterator();
            }

            @Override
            protected int getSize() {
                return libraryCollectionManager.getLibraryCollectionCount(getSearchString(),
                        currentCollectionModel.getObject(), false);
            }

            @Override
            protected String getNotFoundedMessage() {
                return getString("subNotFounded");
            }

            @Override
            protected void onClick(IModel<LibraryCollection> rowModel, GridAction action, AjaxRequestTarget target) {
                switch (action) {
                    case ADD:
                        collectionPropertyPanelDialog.getDialogPanel().initCreateMode();
                        collectionPropertyPanelDialog.show(target);
                        break;
                    case EDIT:
                        collectionPropertyPanelDialog.getDialogPanel().initEditMode(rowModel);
                        collectionPropertyPanelDialog.show(target);
                        break;
                    case REMOVE:
                        try {
                            libraryCollectionManager.removeCollection(rowModel.getObject());
                        } catch (EltilandManagerException e) {
                            LOGGER.error("Cannot delete collection", e);
                            throw new WicketRuntimeException(e);
                        }
                        subCollectionsModel.detach();
                        target.add(subGrid);
                        ELTAlerts.renderOKPopup(getString("removeCollectionMessage"), target);
                        break;
                    default:
                        break;
                }
            }

            @Override
            protected String getActionTooltip(GridAction action) {
                if (action.equals(GridAction.ADD)) {
                    return getString("newSubCollection");
                } else return super.getActionTooltip(action);
            }
        };

        subCollectionContainer.add(new Label("subCollectionLabel", currentCollectionName));
        subCollectionContainer.add(subGrid.setOutputMarkupPlaceholderTag(true));

        add(grid.setOutputMarkupId(true));
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
        response.renderCSSReference(ResourcesUtils.CSS_NEW_TABLE_STYLE);
    }
}
