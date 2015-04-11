package com.eltiland.ui.course.components.editPanels.elements;

import com.eltiland.bl.CourseVideoItemManager;
import com.eltiland.bl.GenericManager;
import com.eltiland.exceptions.ConstraintException;
import com.eltiland.exceptions.EltilandManagerException;
import com.eltiland.model.course.CourseVideoItem;
import com.eltiland.model.course.VideoCourseItem;
import com.eltiland.ui.common.components.ReadonlyObjects;
import com.eltiland.ui.common.components.ResourcesUtils;
import com.eltiland.ui.common.components.button.EltiAjaxLink;
import com.eltiland.ui.common.components.dialog.Dialog;
import com.eltiland.ui.common.components.dialog.ELTAlerts;
import com.eltiland.ui.common.components.dialog.EltiStaticAlerts;
import com.eltiland.ui.common.components.dialog.callback.IDialogNewCallback;
import com.eltiland.ui.common.components.dialog.callback.IDialogUpdateCallback;
import com.eltiland.ui.common.components.grid.ELTTable;
import com.eltiland.ui.common.components.grid.GridAction;
import com.eltiland.ui.course.components.editPanels.elements.video.VideoDescriptionPanel;
import com.eltiland.ui.course.components.editPanels.elements.video.VideoListPreviewPanel;
import com.eltiland.ui.course.components.editPanels.elements.video.VideoPanel;
import com.eltiland.ui.course.components.editPanels.elements.video.VideoPreviewDialogPanel;
import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.table.AbstractColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.PropertyColumn;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * Video course item editing panel.
 *
 * @author Aleksey Plotnikov
 */
public class VideoEditPanel extends AbstractCourseItemEditPanel<VideoCourseItem> {

    private static final Logger LOGGER = LoggerFactory.getLogger(VideoEditPanel.class);

    @SpringBean
    private CourseVideoItemManager courseVideoItemManager;
    @SpringBean
    private GenericManager genericManager;

    private ELTTable<CourseVideoItem> table;
    private WebMarkupContainer previewContainer = new WebMarkupContainer("previewContainer");
    private VideoListPreviewPanel listVideoPanel;

    private Dialog<VideoDescriptionPanel> descriptionPanelDialog =
            new Dialog<VideoDescriptionPanel>("descriptionDialog", 740) {
                @Override
                public VideoDescriptionPanel createDialogPanel(String id) {
                    return new VideoDescriptionPanel(id);
                }

                @Override
                public void registerCallback(VideoDescriptionPanel panel) {
                    super.registerCallback(panel);
                    panel.setUpdateCallback(new IDialogUpdateCallback.IDialogActionProcessor<CourseVideoItem>() {
                        @Override
                        public void process(IModel<CourseVideoItem> model, AjaxRequestTarget target) {
                            try {
                                genericManager.update(model.getObject());
                            } catch (ConstraintException e) {
                                LOGGER.error("Cannot save course item", e);
                                throw new WicketRuntimeException("Cannot save course item", e);
                            }
                            close(target);
                            EltiStaticAlerts.registerOKPopup(getString("saveMessage"));
                            setResponsePage(getPage());
                        }
                    });
                }
            };

    private Dialog<VideoPanel> videoEditPanelDialog = new Dialog<VideoPanel>("videoDialog", 675) {
        @Override
        public VideoPanel createDialogPanel(String id) {
            return new VideoPanel(id);
        }

        @Override
        public void registerCallback(VideoPanel panel) {
            super.registerCallback(panel);
            panel.setUpdateCallback(new IDialogUpdateCallback.IDialogActionProcessor<CourseVideoItem>() {
                @Override
                public void process(IModel<CourseVideoItem> model, AjaxRequestTarget target) {
                    try {
                        genericManager.update(model.getObject());
                    } catch (ConstraintException e) {
                        LOGGER.error("Cannot save course item", e);
                        throw new WicketRuntimeException("Cannot save course item", e);
                    }
                    close(target);
                    target.add(table);
                    ELTAlerts.renderOKPopup(getString("saveMessage"), target);
                }
            });
            panel.setNewCallback(new IDialogNewCallback.IDialogActionProcessor<CourseVideoItem>() {
                @Override
                public void process(IModel<CourseVideoItem> model, AjaxRequestTarget target) {
                    model.getObject().setItem(VideoEditPanel.this.getModelObject());
                    try {
                        courseVideoItemManager.createVideoItem(model.getObject());
                    } catch (EltilandManagerException e) {
                        LOGGER.error("Cannot create item", e);
                        throw new WicketRuntimeException("Cannot create item", e);
                    }
                    close(target);
                    target.add(table);
                }
            });
        }
    };

    private Dialog<VideoPreviewDialogPanel> videoPreviewPanelDialog =
            new Dialog<VideoPreviewDialogPanel>("videoPreviewDialog", 675) {
                @Override
                public VideoPreviewDialogPanel createDialogPanel(String id) {
                    return new VideoPreviewDialogPanel(id);
                }
            };

    /**
     * Default constructor.
     *
     * @param id                    markup id.
     * @param videoCourseItemIModel video course item.
     */
    public VideoEditPanel(String id, final IModel<VideoCourseItem> videoCourseItemIModel) {
        super(id, videoCourseItemIModel);

        table = new ELTTable<CourseVideoItem>("grid", 30) {

            @Override
            protected List<IColumn<CourseVideoItem>> getColumns() {
                List<IColumn<CourseVideoItem>> columns = new ArrayList<>();
                columns.add(new AbstractColumn<CourseVideoItem>(ReadonlyObjects.EMPTY_DISPLAY_MODEL) {
                    @Override
                    public void populateItem(Item<ICellPopulator<CourseVideoItem>> cellItem, String componentId,
                                             IModel<CourseVideoItem> rowModel) {
                        cellItem.add(new Label(componentId, String.valueOf(rowModel.getObject().getIndex() + 1)));
                    }
                });
                columns.add(new PropertyColumn(new ResourceModel("nameColumn"), "name"));
                return columns;
            }

            @Override
            protected Iterator getIterator(int first, int count) {
                return courseVideoItemManager.getItemList(VideoEditPanel.this.getModelObject(),
                        first, count, "index", true).iterator();
            }

            @Override
            protected int getSize() {
                return courseVideoItemManager.getItemCount(VideoEditPanel.this.getModelObject());
            }

            @Override
            protected List<GridAction> getGridActions(IModel<CourseVideoItem> rowModel) {
                return new ArrayList<>(Arrays.asList(
                        GridAction.UP, GridAction.DOWN, GridAction.EDIT,
                        GridAction.INFO, GridAction.PREVIEW, GridAction.REMOVE));
            }

            @Override
            protected boolean isControlling() {
                return true;
            }

            @Override
            protected List<GridAction> getControlActions() {
                return new ArrayList<>(Arrays.asList(GridAction.ADD, GridAction.PAGE_PREVIEW,
                        GridAction.CONTROL_SET, GridAction.CONTROL_RESET));
            }

            @Override
            protected boolean hasConfirmation(GridAction action) {
                return action.equals(GridAction.REMOVE);
            }

            @Override
            protected String getActionTooltip(GridAction action) {
                switch (action) {
                    case EDIT:
                        return getString("editAction");
                    case PREVIEW:
                        return getString("previewAction");
                    case INFO:
                        return getString("descAction");
                    case REMOVE:
                        return getString("removeAction");
                    case ADD:
                        return getString("addAction");
                    case UP:
                        return getString("upAction");
                    case DOWN:
                        return getString("downAction");
                    default:
                        return "";
                }
            }

            @Override
            protected boolean isActionVisible(GridAction action, IModel<CourseVideoItem> rowModel) {
                genericManager.initialize(rowModel.getObject(), rowModel.getObject().getItem());

                int index = rowModel.getObject().getIndex();
                return !(action.equals(GridAction.UP) && (index == 0)) &&
                        !(action.equals(GridAction.DOWN) &&
                                (index >= (courseVideoItemManager.getItemCount(rowModel.getObject().getItem()) - 1)));
            }

            @Override
            protected boolean isControlActionVisible(GridAction action) {
                boolean isControl = VideoEditPanel.this.getModelObject().isControl();

                if (action.equals(GridAction.CONTROL_SET)) {
                    return !isControl;
                } else return !action.equals(GridAction.CONTROL_RESET) || isControl;
            }

            @Override
            protected void onClick(IModel<CourseVideoItem> rowModel, GridAction action, AjaxRequestTarget target) {
                switch (action) {
                    case INFO:
                        descriptionPanelDialog.getDialogPanel().initData(rowModel);
                        descriptionPanelDialog.show(target);
                        break;
                    case EDIT:
                        videoEditPanelDialog.getDialogPanel().initData(rowModel);
                        videoEditPanelDialog.show(target);
                        break;
                    case PREVIEW:
                        videoPreviewPanelDialog.getDialogPanel().initData(rowModel);
                        videoPreviewPanelDialog.show(target);
                        break;
                    case ADD:
                        videoEditPanelDialog.getDialogPanel().initCreate();
                        videoEditPanelDialog.show(target);
                        break;
                    case UP:
                        try {
                            courseVideoItemManager.moveUp(rowModel.getObject());
                        } catch (EltilandManagerException e) {
                            LOGGER.error("Cannot move item", e);
                            throw new WicketRuntimeException("Cannot move item", e);
                        }
                        target.add(table);
                        break;
                    case DOWN:
                        try {
                            courseVideoItemManager.moveDown(rowModel.getObject());
                        } catch (EltilandManagerException e) {
                            LOGGER.error("Cannot move item", e);
                            throw new WicketRuntimeException("Cannot move item", e);
                        }
                        target.add(table);
                        break;
                    case REMOVE:
                        try {
                            courseVideoItemManager.deleteVideoItem(rowModel.getObject());
                        } catch (EltilandManagerException e) {
                            LOGGER.error("Cannot delete item", e);
                            throw new WicketRuntimeException("Cannot delete item", e);
                        }
                        target.add(table);
                        break;
                    case PAGE_PREVIEW:
                        previewContainer.setVisible(true);
                        table.setVisible(false);
                        target.add(previewContainer);
                        target.add(table);
                        break;
                    case CONTROL_SET: {
                        VideoCourseItem videoItem = VideoEditPanel.this.getModelObject();
                        videoItem.setControl(true);
                        try {
                            genericManager.update(videoItem);
                        } catch (ConstraintException e) {
                            LOGGER.error("Error while change item params");
                            throw new WicketRuntimeException("Error while change item params", e);
                        }
                        target.add(table);
                        break;
                    }
                    case CONTROL_RESET: {
                        VideoCourseItem videoItem = VideoEditPanel.this.getModelObject();
                        videoItem.setControl(false);
                        try {
                            genericManager.update(videoItem);
                        } catch (ConstraintException e) {
                            LOGGER.error("Error while change item params");
                            throw new WicketRuntimeException("Error while change item params", e);
                        }
                        target.add(table);
                        break;
                    }
                }
            }

        };
        add(table.setOutputMarkupPlaceholderTag(true));

        add(descriptionPanelDialog);
        add(videoEditPanelDialog);
        add(videoPreviewPanelDialog);
        add(previewContainer.setVisible(false).setOutputMarkupPlaceholderTag(true));
        previewContainer.add(new VideoListPreviewPanel("videoList", getModel()));
        previewContainer.add(new EltiAjaxLink("backButton") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                table.setVisible(true);
                previewContainer.setVisible(false);
                target.add(table);
                target.add(previewContainer);
            }
        });
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
        response.renderCSSReference(ResourcesUtils.CSS_NEW_TABLE_STYLE);
    }
}
