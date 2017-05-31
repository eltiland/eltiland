package com.eltiland.ui.google.plugin;

import com.eltiland.bl.GenericManager;
import com.eltiland.bl.drive.GoogleDriveManager;
import com.eltiland.exceptions.ConstraintException;
import com.eltiland.exceptions.EltilandManagerException;
import com.eltiland.exceptions.GoogleDriveException;
import com.eltiland.model.google.ELTGooglePermissions;
import com.eltiland.model.google.GoogleDriveFile;
import com.eltiland.model.google.GooglePage;
import com.eltiland.ui.common.BaseEltilandPanel;
import com.eltiland.ui.common.column.PropertyWrapColumn;
import com.eltiland.ui.common.components.ReadonlyObjects;
import com.eltiland.ui.common.components.behavior.ConfirmationDialogBehavior;
import com.eltiland.ui.common.components.behavior.TooltipBehavior;
import com.eltiland.ui.common.components.button.EltiAjaxLink;
import com.eltiland.ui.common.components.datagrid.EltiDefaultDataGrid;
import com.eltiland.ui.common.components.dialog.Dialog;
import com.eltiland.ui.common.components.dialog.ELTAlerts;
import com.eltiland.ui.common.components.dialog.callback.IDialogNewCallback;
import com.eltiland.ui.common.components.dialog.callback.IDialogUpdateCallback;
import com.eltiland.ui.common.components.loadingPanel.ELTLoadingPanel;
import com.eltiland.ui.common.model.GenericDBModel;
import com.eltiland.ui.google.ELTGoogleDriveEditor;
import com.eltiland.ui.google.components.GoogleDataSource;
import com.inmethod.grid.IGridColumn;
import com.inmethod.grid.column.AbstractColumn;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.IAjaxCallDecorator;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.EmptyPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.brixcms.workspace.Workspace;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Panel for management of the google pages.
 *
 * @author Aleksey Plotnikov.
 */
public class GoogleManagementPanel extends BaseEltilandPanel<Workspace> {
    @SpringBean
    private GenericManager genericManager;
    @SpringBean
    private GoogleDriveManager googleDriveManager;

    private static final Logger LOGGER = LoggerFactory.getLogger(GoogleManagementPanel.class);

    private final EltiDefaultDataGrid<GoogleDataSource, GooglePage> grid;

    private Dialog<GooglePropertyPanel> googlePropertyPanelDialog =
            new Dialog<GooglePropertyPanel>("googlePropertyPageDialog", 317) {
                @Override
                public GooglePropertyPanel createDialogPanel(String id) {
                    return new GooglePropertyPanel(id);
                }

                @Override
                public void registerCallback(GooglePropertyPanel panel) {
                    super.registerCallback(panel);
                    panel.setNewCallback(new IDialogNewCallback.IDialogActionProcessor<GooglePage>() {
                        @Override
                        public void process(IModel<GooglePage> model, AjaxRequestTarget target) {
                            GoogleDriveFile file;
                            GooglePage page = model.getObject();

                            try {
                                file = googleDriveManager.createEmptyDoc(page.getName(), GoogleDriveFile.TYPE.DOCUMENT);
                            } catch (GoogleDriveException e) {
                                LOGGER.error("Error while creating Google Drive document", e);
                                throw new WicketRuntimeException("Error while creating Google Drive document", e);
                            }

                            page.setContent(file);
                            try {
                                genericManager.saveNew(page);
                            } catch (ConstraintException e) {
                                LOGGER.error("Error while creating Google page entity", e);
                                throw new WicketRuntimeException("Error while creating Google page entity", e);
                            }

                            close(target);
                            target.add(grid);
                        }
                    });

                    panel.setUpdateCallback(new IDialogUpdateCallback.IDialogActionProcessor<GooglePage>() {
                        @Override
                        public void process(IModel<GooglePage> model, AjaxRequestTarget target) {
                            GooglePage page = model.getObject();

                            try {
                                genericManager.update(page);
                            } catch (ConstraintException e) {
                                LOGGER.error("Error while saving Google page entity", e);
                                throw new WicketRuntimeException("Error while saving Google page entity", e);
                            }

                            close(target);
                            target.add(grid);
                        }
                    });
                }
            };


    private WebMarkupContainer editContainer = new WebMarkupContainer("editorContainer");

    protected GoogleManagementPanel(String id, IModel<Workspace> workspaceIModel) {
        super(id, workspaceIModel);

        List<IGridColumn<GoogleDataSource, GooglePage>> columns = new ArrayList<>();
        columns.add(new PropertyWrapColumn(new ResourceModel("nameLabel"), "name", "name") {
            @Override
            public int getInitialSize() {
                return 335;
            }
        });

        columns.add(new AbstractColumn<GoogleDataSource, GooglePage>("action", ReadonlyObjects.EMPTY_DISPLAY_MODEL) {
            @Override
            public Component newCell(WebMarkupContainer parent, String componentId, final IModel<GooglePage> rowModel) {
                return new GridActionPanel(componentId) {
                    @Override
                    protected void onDelete(AjaxRequestTarget target) {
                        try {
                            genericManager.delete(rowModel.getObject());
                        } catch (EltilandManagerException e) {
                            LOGGER.error("Error while deleting google page");
                            throw new WicketRuntimeException("Error while deleting google page", e);
                        }
                        target.add(grid);
                    }

                    @Override
                    protected void onEdit(AjaxRequestTarget target) {
                        googlePropertyPanelDialog.getDialogPanel().initEditMode(rowModel.getObject());
                        googlePropertyPanelDialog.show(target);
                    }
                };
            }
        });

        grid = new EltiDefaultDataGrid<GoogleDataSource, GooglePage>("googleTable", new GoogleDataSource() {
            @Override
            public TextField getSearchField() {
                return null;
            }
        }, columns) {
            @Override
            protected boolean onCellClicked(AjaxRequestTarget target, final IModel<GooglePage> rowModel,
                                            IGridColumn<GoogleDataSource, GooglePage> column) {
                super.onCellClicked(target, rowModel, column);
                editContainer.setVisible(true);

                editContainer.replace(new ELTLoadingPanel("editorPanel") {
                    @Override
                    public Component getLazyLoadComponent(String markupId) {
                        genericManager.initialize(rowModel.getObject(), rowModel.getObject().getContent());
                        return new ELTGoogleDriveEditor(markupId,
                                new GenericDBModel<>(GoogleDriveFile.class, rowModel.getObject().getContent()),
                                ELTGoogleDriveEditor.MODE.EDIT, GoogleDriveFile.TYPE.DOCUMENT) {
                            @Override
                            protected Panel getAdditionalPanel(String markupId) {
                                return new ActionPanel(markupId, rowModel);
                            }

                            @Override
                            protected void onUpload(GoogleDriveFile file) {
                                rowModel.getObject().setContent(file);
                                try {
                                    genericManager.update(rowModel.getObject());
                                } catch (ConstraintException e) {
                                    LOGGER.error("Error while saving google page");
                                    throw new WicketRuntimeException("Error while saving google page", e);
                                }
                            }
                        };
                    }
                });
                target.add(editContainer);
                return true;
            }
        };

        editContainer.setVisible(false);
        add(editContainer.setOutputMarkupPlaceholderTag(true));
        editContainer.add(new EmptyPanel("editorPanel"));
        add(grid.setOutputMarkupId(true));

        add(new EltiAjaxLink("addButton") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                googlePropertyPanelDialog.getDialogPanel().initCreateMode();
                googlePropertyPanelDialog.show(target);
            }
        });

        add(googlePropertyPanelDialog);
    }

    private class ActionPanel extends BaseEltilandPanel<GooglePage> {

        private IModel<GooglePage> pageIModel = new GenericDBModel<>(GooglePage.class);

        private ActionPanel(String id, IModel<GooglePage> googlePageIModel) {
            super(id, googlePageIModel);
            pageIModel.setObject(googlePageIModel.getObject());

            EltiAjaxLink saveButton = new EltiAjaxLink("saveButton") {
                @Override
                public void onClick(AjaxRequestTarget target) {
                    try {
                        genericManager.initialize(pageIModel.getObject(), pageIModel.getObject().getContent());
/*                        googleDriveManager.publishDocument(pageIModel.getObject().getContent());
                        googleDriveManager.insertPermission(pageIModel.getObject().getContent(),
                                new ELTGooglePermissions(ELTGooglePermissions.ROLE.WRITER,
                                        ELTGooglePermissions.TYPE.ANYONE));*/
                        googleDriveManager.cacheFile(pageIModel.getObject().getContent());
                    } catch (GoogleDriveException e) {
                        LOGGER.error("Error while publish course");
                        throw new WicketRuntimeException("Error while publish course", e);
                    }
                    ELTAlerts.renderOKPopup(getString("saveMessage"), target);
                }
            };

            add(saveButton);
            saveButton.add(new AttributeModifier("title", new ResourceModel("saveTooltip")));
            saveButton.add(new TooltipBehavior());

        }
    }

    private abstract class GridActionPanel extends BaseEltilandPanel<GooglePage> {

        EltiAjaxLink editButton = new EltiAjaxLink("editButton") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                onEdit(target);
            }

            @Override
            protected IAjaxCallDecorator getAjaxCallDecorator() {
                return null;
            }
        };

        EltiAjaxLink closeButton = new EltiAjaxLink("closeButton") {
            {
                add(new ConfirmationDialogBehavior());
            }

            @Override
            public void onClick(AjaxRequestTarget target) {
                onDelete(target);
            }

            @Override
            protected IAjaxCallDecorator getAjaxCallDecorator() {
                return null;
            }
        };

        public GridActionPanel(String id) {
            super(id);

            add(editButton);
            add(closeButton);

            editButton.add(new AttributeModifier("title", new ResourceModel("editAction")));
            closeButton.add(new AttributeModifier("title", new ResourceModel("closeAction")));

            editButton.add(new TooltipBehavior());
            closeButton.add(new TooltipBehavior());
        }

        protected abstract void onDelete(AjaxRequestTarget target);

        protected abstract void onEdit(AjaxRequestTarget target);
    }
}
