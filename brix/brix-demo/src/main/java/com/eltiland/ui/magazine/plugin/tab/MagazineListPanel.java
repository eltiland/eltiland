package com.eltiland.ui.magazine.plugin.tab;

import brix.tinymce.TinyMceEnabler;
import com.eltiland.bl.CountableManager;
import com.eltiland.bl.magazine.MagazineManager;
import com.eltiland.exceptions.CountableException;
import com.eltiland.exceptions.EltilandManagerException;
import com.eltiland.exceptions.FileException;
import com.eltiland.model.magazine.Magazine;
import com.eltiland.ui.common.BaseEltilandPanel;
import com.eltiland.ui.common.CKEditorFull;
import com.eltiland.ui.common.column.PriceColumn;
import com.eltiland.ui.common.components.ReadonlyObjects;
import com.eltiland.ui.common.components.button.EltiAjaxLink;
import com.eltiland.ui.common.components.button.EltiAjaxSubmitLink;
import com.eltiland.ui.common.components.dialog.Dialog;
import com.eltiland.ui.common.components.dialog.ELTAlerts;
import com.eltiland.ui.common.components.dialog.callback.IDialogNewCallback;
import com.eltiland.ui.common.components.dialog.callback.IDialogSimpleUpdateCallback;
import com.eltiland.ui.common.components.dialog.callback.IDialogUpdateCallback;
import com.eltiland.ui.common.components.grid.ELTTable;
import com.eltiland.ui.common.components.grid.GridAction;
import com.eltiland.ui.common.components.textfield.ELTTextArea;
import com.eltiland.ui.common.model.GenericDBModel;
import com.eltiland.ui.magazine.plugin.tab.panel.MagazinePropertyPanel;
import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.table.AbstractColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.PropertyColumn;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * Panel for magazine list.
 *
 * @author Aleksey Plotnikov.
 */
public class MagazineListPanel extends Panel {

    private static final Logger LOGGER = LoggerFactory.getLogger(MagazineListPanel.class);

    @SpringBean
    private MagazineManager magazineManager;
    @SpringBean
    private CountableManager<Magazine> countableManager;

    private Dialog<MagazinePropertyPanel> magazinePropertyPanelDialog =
            new Dialog<MagazinePropertyPanel>("magazinePropertyDialog", 600) {
                @Override
                public MagazinePropertyPanel createDialogPanel(String id) {
                    return new MagazinePropertyPanel(id);
                }

                @Override
                public void registerCallback(MagazinePropertyPanel panel) {
                    panel.setNewCallback(new IDialogNewCallback.IDialogActionProcessor<Magazine>() {
                        @Override
                        public void process(IModel<Magazine> model, AjaxRequestTarget target) {
                            close(target);
                            target.add(grid);
                            ELTAlerts.renderOKPopup(getString("magazineAddedMessage"), target);
                        }
                    });
                    panel.setUpdateCallback(new IDialogUpdateCallback.IDialogActionProcessor<Magazine>() {
                        @Override
                        public void process(IModel<Magazine> model, AjaxRequestTarget target) {
                            close(target);
                            target.add(grid);
                            ELTAlerts.renderOKPopup(getString("magazineSavedMessage"), target);
                        }
                    });
                    super.registerCallback(panel);
                }
            };

    private Dialog<MagazineAboutPanel> magazineAboutPanelDialog =
            new Dialog<MagazineAboutPanel>("magazineAboutDialog", 740) {
                @Override
                public MagazineAboutPanel createDialogPanel(String id) {
                    return new MagazineAboutPanel(id, magazineAboutPanelDialog);
                }

                @Override
                public void registerCallback(MagazineAboutPanel panel) {
                    panel.setSimpleUpdateCallback(new IDialogSimpleUpdateCallback.IDialogActionProcessor<String>() {
                        @Override
                        public void process(IModel<String> model, AjaxRequestTarget target) {
                            close(target);
                            Magazine magazine = currentMagazineModel.getObject();
                            magazine.setAbout(model.getObject());
                            try {
                                magazineManager.updateMagazine(magazine);
                            } catch (EltilandManagerException | FileException e) {
                                LOGGER.error("Cannot update magazine entity", e);
                                throw new WicketRuntimeException("Cannot update magazine entity", e);
                            }
                            setResponsePage(getPage());
                        }
                    });
                    super.registerCallback(panel);
                }

                @Override
                protected void onClose(AjaxRequestTarget target) {
                    super.onClose(target);
                    setResponsePage(getPage());
                }
            };

    private ELTTable<Magazine> grid;

    private IModel<Magazine> currentMagazineModel = new GenericDBModel<>(Magazine.class);

    public MagazineListPanel(String id, IModel<?> model) {
        super(id, model);

        grid = new ELTTable<Magazine>("grid", 20) {
            @Override
            protected List<IColumn<Magazine>> getColumns() {
                List<IColumn<Magazine>> columns = new ArrayList<>();
                columns.add(new PropertyColumn<Magazine>(new ResourceModel("nameColumn"), "name"));
                columns.add(new PropertyColumn<Magazine>(new ResourceModel("topicColumn"), "topic"));
                columns.add(new PriceColumn(new ResourceModel("priceColumn"), "price"));
                columns.add(new AbstractColumn<Magazine>(ReadonlyObjects.EMPTY_DISPLAY_MODEL) {
                    @Override
                    public void populateItem(Item<ICellPopulator<Magazine>> cellItem,
                                             String componentId, final IModel<Magazine> rowModel) {
                        cellItem.add(new MagazineAboutLinkPanel(componentId, rowModel) {
                            @Override
                            protected void onAbout(AjaxRequestTarget target) {
                                currentMagazineModel.setObject(rowModel.getObject());
                                magazineAboutPanelDialog.getDialogPanel().initEditMode(
                                        rowModel.getObject().getAbout());
                                magazineAboutPanelDialog.show(target);
                            }
                        });
                    }
                });
                return columns;
            }

            @Override
            protected Iterator getIterator(int first, int count) {
                return countableManager.getEntityList(Magazine.class, first, count).iterator();
            }

            @Override
            protected int getSize() {
                return countableManager.getEntityCount(Magazine.class);
            }


            @Override
            protected List<GridAction> getGridActions(IModel<Magazine> rowModel) {
                return new ArrayList<>(Arrays.asList(
                        GridAction.UP, GridAction.DOWN, GridAction.EDIT, GridAction.REMOVE));
            }

            @Override
            protected boolean isActionVisible(GridAction action, IModel<Magazine> rowModel) {
                if (action.equals(GridAction.UP)) {
                    return rowModel.getObject().getIndex() != 0;
                } else if (action.equals(GridAction.DOWN)) {
                    return (rowModel.getObject().getIndex() + 1) < getSize();
                }
                return true;
            }

            @Override
            protected boolean hasConfirmation(GridAction action) {
                return action.equals(GridAction.REMOVE);
            }

            @Override
            protected String getActionTooltip(GridAction action) {
                if (action.equals(GridAction.EDIT)) {
                    return getString("editAction");
                } else if (action.equals(GridAction.REMOVE)) {
                    return getString("deleteAction");
                } else if (action.equals(GridAction.UP)) {
                    return getString("upAction");
                } else if (action.equals(GridAction.DOWN)) {
                    return getString("downAction");
                } else {
                    return getString("addAction");
                }
            }

            @Override
            protected boolean isControlling() {
                return true;
            }

            @Override
            protected List<GridAction> getControlActions() {
                return new ArrayList<>(Arrays.asList(GridAction.ADD));
            }

            @Override
            protected void onClick(IModel<Magazine> rowModel, GridAction action, AjaxRequestTarget target) {
                if (action.equals(GridAction.EDIT)) {
                    magazinePropertyPanelDialog.getDialogPanel().initEditMode(rowModel.getObject());
                    magazinePropertyPanelDialog.show(target);
                } else if (action.equals(GridAction.REMOVE)) {
                    try {
                        magazineManager.deleteMagazine(rowModel.getObject());
                    } catch (FileException | CountableException e) {
                        LOGGER.error("Cannot delete magazine entity", e);
                        throw new WicketRuntimeException("Cannot delete magazine entity", e);
                    }
                    target.add(grid);
                    ELTAlerts.renderOKPopup(getString("magazineDeletedMessage"), target);
                } else if (action.equals(GridAction.ADD)) {
                    magazinePropertyPanelDialog.getDialogPanel().initCreateMode();
                    magazinePropertyPanelDialog.show(target);
                } else if (action.equals(GridAction.UP)) {
                    try {
                        countableManager.moveUp(rowModel.getObject());
                    } catch (CountableException e) {
                        ELTAlerts.renderErrorPopup(e.getMessage(), target);
                    }
                    target.add(grid);
                } else if (action.equals(GridAction.DOWN)) {
                    try {
                        countableManager.moveDown(rowModel.getObject());
                    } catch (CountableException e) {
                        ELTAlerts.renderErrorPopup(e.getMessage(), target);
                    }
                    target.add(grid);
                }
            }
        };

        add(grid);

        add(magazinePropertyPanelDialog);
        add(magazineAboutPanelDialog);
    }

    private abstract class MagazineAboutLinkPanel extends BaseEltilandPanel<Magazine> {

        protected MagazineAboutLinkPanel(String id, IModel<Magazine> magazineIModel) {
            super(id, magazineIModel);

            add(new EltiAjaxLink("link") {
                @Override
                public void onClick(AjaxRequestTarget target) {
                    onAbout(target);
                }
            });
        }

        protected abstract void onAbout(AjaxRequestTarget target);
    }

    private class MagazineAboutPanel extends BaseEltilandPanel<Magazine> implements IDialogSimpleUpdateCallback<String> {

        private IDialogActionProcessor<String> updateCallback;

        private CKEditorFull aboutField;

        protected MagazineAboutPanel(String id, Dialog dialog) {
            super(id);

            Form form = new Form("form");
            add(form);
            aboutField = new CKEditorFull("aboutField", dialog);
            form.add(aboutField);
            form.add(new EltiAjaxSubmitLink("saveButton") {
                @Override
                protected void onSubmit(AjaxRequestTarget target, Form form) {
                    updateCallback.process(new Model<>(aboutField.getData()), target);
                }
            });
        }

        public void initEditMode(String about) {
            aboutField.setData(about);
        }

        @Override
        public void setSimpleUpdateCallback(IDialogActionProcessor<String> callback) {
            updateCallback = callback;
        }
    }
}
