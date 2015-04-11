package com.eltiland.ui.slider.plugin;

import com.eltiland.bl.FileManager;
import com.eltiland.bl.GenericManager;
import com.eltiland.bl.PropertyManager;
import com.eltiland.bl.SliderManager;
import com.eltiland.exceptions.ConstraintException;
import com.eltiland.exceptions.EltilandManagerException;
import com.eltiland.exceptions.FileException;
import com.eltiland.model.Property;
import com.eltiland.model.Slider;
import com.eltiland.model.file.File;
import com.eltiland.ui.common.BaseEltilandPanel;
import com.eltiland.ui.common.column.image.ImageFileColumn;
import com.eltiland.ui.common.components.ReadonlyObjects;
import com.eltiland.ui.common.components.button.EltiAjaxLink;
import com.eltiland.ui.common.components.checkbox.ELTAjaxCheckBox;
import com.eltiland.ui.common.components.datagrid.EltiDataGrid;
import com.eltiland.ui.common.components.datagrid.EltiDefaultDataGrid;
import com.eltiland.ui.common.components.dialog.Dialog;
import com.eltiland.ui.common.components.dialog.ELTAlerts;
import com.eltiland.ui.common.components.dialog.callback.IDialogProcessCallback;
import com.eltiland.ui.common.components.dialog.callback.IDialogUpdateCallback;
import com.eltiland.ui.common.model.GenericDBModel;
import com.inmethod.grid.IDataSource;
import com.inmethod.grid.IGridColumn;
import com.inmethod.grid.column.AbstractColumn;
import org.apache.wicket.Component;
import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.brixcms.workspace.Workspace;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Paid Groups management panel.
 */
public class SliderManagementPanel extends BaseEltilandPanel<Workspace> {

    private static final Logger LOGGER = LoggerFactory.getLogger(SliderManagementPanel.class);

    private final EltiDataGrid<SliderDataSource, Slider> grid;

    @SpringBean
    private PropertyManager propertyManager;
    @SpringBean
    private GenericManager genericManager;
    @SpringBean
    private FileManager fileManager;
    @SpringBean
    private SliderManager sliderManager;

    private Dialog<ImagePanel> addImageDialog = new Dialog<ImagePanel>("addImageDialog", 400) {
        @Override
        public ImagePanel createDialogPanel(String id) {
            return new ImagePanel(id);
        }

        @Override
        public void registerCallback(ImagePanel panel) {
            super.registerCallback(panel);
            panel.setProcessCallback(new IDialogProcessCallback.IDialogActionProcessor<File>() {
                @Override
                public void process(IModel<File> model, AjaxRequestTarget target) {
                    try {
                        File file = fileManager.saveFile(model.getObject());
                        Slider slider = new Slider();
                        slider.setFile(file);
                        genericManager.saveNew(slider);
                        target.add(grid);

                    } catch (ConstraintException | FileException e) {
                        LOGGER.error("Cannot save file", e);
                        throw new WicketRuntimeException("Cannot save file", e);
                    }
                    target.add(grid);
                    ELTAlerts.renderOKPopup(getString("imageAddedMessage"), target);
                    close(target);
                }
            });
        }
    };

    private Dialog<EditLinkPanel> editLinkPanelDialog = new Dialog<EditLinkPanel>("editLinkDialog", 395) {
        @Override
        public EditLinkPanel createDialogPanel(String id) {
            return new EditLinkPanel(id);
        }

        @Override
        public void registerCallback(EditLinkPanel panel) {
            super.registerCallback(panel);
            panel.setUpdateCallback(new IDialogUpdateCallback.IDialogActionProcessor<Slider>() {
                @Override
                public void process(IModel<Slider> model, AjaxRequestTarget target) {
                    Slider slider = model.getObject();
                    try {
                        genericManager.update(slider);
                    } catch (ConstraintException e) {
                        LOGGER.error("Cannot save link", e);
                        throw new WicketRuntimeException("Cannot save link", e);
                    }
                    target.add(grid);
                    ELTAlerts.renderOKPopup(getString("linkSavedMessage"), target);
                    close(target);
                }
            });
        }
    };

    /**
     * Panel constructor.
     *
     * @param id              panel's ID.
     * @param workspaceIModel workspace model.
     */
    protected SliderManagementPanel(String id, IModel<Workspace> workspaceIModel) {
        super(id, workspaceIModel);

        final ELTAjaxCheckBox sliderCheckBox = new ELTAjaxCheckBox("sliderCheckBox", new ResourceModel("showSlider"),
                 new Model<>(Boolean.parseBoolean(propertyManager.getProperty(Property.SHOW_SLIDER)))) {
            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                int count = genericManager.getEntityCount(Slider.class, null, null);
                if (getModelObject() && (count == 0)) {
                    ELTAlerts.renderErrorPopup(getString("errorNoImages"), target);
                    setModelObject(false);
                    target.add(this);
                } else {
                    try {
                        propertyManager.saveProperty(Property.SHOW_SLIDER, String.valueOf(getModelObject()));
                    } catch (EltilandManagerException e) {
                        LOGGER.error("Cannot update property", e);
                        throw new WicketRuntimeException("Cannot update property", e);
                    }
                }
            }
        };

        List<IGridColumn<SliderDataSource, Slider>> columns = new ArrayList<>();
        columns.add(new ImageFileColumn<SliderDataSource, Slider>("imageColumn") {
            @Override
            protected IModel<File> getImageFile(IModel<Slider> rowModel) {
                genericManager.initialize(rowModel.getObject(), rowModel.getObject().getFile());
                return new GenericDBModel<>(File.class, fileManager.getFileById(rowModel.getObject().getFile().getId()));
            }

            @Override
            public int getInitialSize() {
                return 210;
            }
        });
        columns.add(new AbstractColumn<SliderDataSource, Slider>("linkColumn", new ResourceModel("linkColumn")) {
            @Override
            public Component newCell(WebMarkupContainer components, String s, final IModel<Slider> sliderIModel) {
                return new LinkPanel(s, sliderIModel) {
                    @Override
                    protected void onClick(AjaxRequestTarget target) {
                        editLinkPanelDialog.getDialogPanel().initData(sliderIModel);
                        editLinkPanelDialog.show(target);
                    }
                };
            }

            @Override
            public int getInitialSize() {
                return 300;
            }
        });
        columns.add(new AbstractColumn<SliderDataSource, Slider>("actionColumn", ReadonlyObjects.EMPTY_DISPLAY_MODEL) {
            @Override
            public Component newCell(WebMarkupContainer components, String s, final IModel<Slider> sliderIModel) {
                return new ActionPanel(s) {
                    @Override
                    public void onMoveUp(AjaxRequestTarget target) {
                        try {
                            sliderManager.moveUp(sliderIModel.getObject());
                        } catch (EltilandManagerException e) {
                            LOGGER.error("Cannot move image", e);
                            throw new WicketRuntimeException("Cannot move image", e);
                        }
                        target.add(grid);
                    }

                    @Override
                    public void onMoveDown(AjaxRequestTarget target) {
                        try {
                            sliderManager.moveDown(sliderIModel.getObject());
                        } catch (EltilandManagerException e) {
                            LOGGER.error("Cannot move image", e);
                            throw new WicketRuntimeException("Cannot move image", e);
                        }
                        target.add(grid);
                    }

                    @Override
                    public void onDelete(AjaxRequestTarget target) {
                        try {
                            File file = sliderIModel.getObject().getFile();
                            genericManager.delete(sliderIModel.getObject());
                            fileManager.deleteFile(file);
                        } catch (FileException | EltilandManagerException e) {
                            LOGGER.error("Cannot delete image", e);
                            throw new WicketRuntimeException("Cannot delete image", e);
                        }
                        target.add(grid);
                        ELTAlerts.renderOKPopup(getString("imageRemovedMessage"), target);

                        int count = genericManager.getEntityCount(Slider.class, null, null);
                        if (count == 0) {
                            try {
                                propertyManager.saveProperty(Property.SHOW_SLIDER, String.valueOf(false));
                            } catch (EltilandManagerException e) {
                                LOGGER.error("Cannot update property", e);
                                throw new WicketRuntimeException("Cannot update property", e);
                            }
                            sliderCheckBox.setModelObject(false);
                            target.add(sliderCheckBox);
                        }
                    }

                    @Override
                    public boolean canBeMovedUp() {
                        return sliderIModel.getObject().getOrder() != 0;
                    }

                    @Override
                    public boolean canBeMovedDown() {
                        int count = genericManager.getEntityCount(Slider.class, null, null);
                        return (sliderIModel.getObject().getOrder() + 1) != count;
                    }
                };
            }
        });

        grid = new EltiDefaultDataGrid<>("grid", new Model<>(new SliderDataSource()), columns);
        add(grid.setOutputMarkupId(true));
        add(sliderCheckBox);

        EltiAjaxLink addImageButton = new EltiAjaxLink("addImage") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                addImageDialog.show(target);
            }
        };
        add(addImageButton);
        add(addImageDialog);
        add(editLinkPanelDialog);
    }

    private class SliderDataSource implements IDataSource<Slider> {
        @Override
        public void query(IQuery query, IQueryResult<Slider> result) {
            int count = genericManager.getEntityCount(Slider.class, null, null);
            result.setTotalCount(count);

            if (count < 1) {
                result.setItems(Collections.<Slider>emptyIterator());
            }
            result.setItems(genericManager.getEntityList(Slider.class, "order").iterator());
        }

        @Override
        public IModel<Slider> model(Slider object) {
            return new GenericDBModel<>(Slider.class, object);
        }

        @Override
        public void detach() {
        }
    }
}
