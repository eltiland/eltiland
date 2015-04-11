package com.eltiland.ui.course.edit;

import com.eltiland.bl.GenericManager;
import com.eltiland.bl.course.video.ELTVideoItemManager;
import com.eltiland.exceptions.CourseException;
import com.eltiland.model.course2.content.video.ELTVideoCourseItem;
import com.eltiland.model.course2.content.video.ELTVideoItem;
import com.eltiland.ui.common.components.ReadonlyObjects;
import com.eltiland.ui.common.components.ResourcesUtils;
import com.eltiland.ui.common.components.button.EltiAjaxLink;
import com.eltiland.ui.common.components.dialog.Dialog;
import com.eltiland.ui.common.components.dialog.ELTAlerts;
import com.eltiland.ui.common.components.dialog.callback.IDialogNewCallback;
import com.eltiland.ui.common.components.dialog.callback.IDialogUpdateCallback;
import com.eltiland.ui.common.components.grid.ELTTable;
import com.eltiland.ui.common.components.grid.GridAction;
import com.eltiland.ui.course.edit.video.VideoDescriptionPanel;
import com.eltiland.ui.course.edit.video.VideoListPreviewPanel;
import com.eltiland.ui.course.edit.video.VideoPanel;
import com.eltiland.ui.course.edit.video.VideoPreviewDialogPanel;
import org.apache.commons.lang.StringUtils;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * Video course item panel.
 *
 * @author Aleksey Plotnikov.
 */
public class VideoCourseItemPanel extends AbstractCourseItemPanel<ELTVideoCourseItem> {

    @SpringBean
    private ELTVideoItemManager videoItemManager;
    @SpringBean
    private GenericManager genericManager;

    private ELTTable<ELTVideoItem> table;
    private WebMarkupContainer previewContainer = new WebMarkupContainer("previewContainer");

    private Dialog<VideoDescriptionPanel> descriptionPanelDialog =
            new Dialog<VideoDescriptionPanel>("descriptionDialog", 760) {
                @Override
                public VideoDescriptionPanel createDialogPanel(String id) {
                    return new VideoDescriptionPanel(id);
                }

                @Override
                public void registerCallback(VideoDescriptionPanel panel) {
                    super.registerCallback(panel);
                    panel.setUpdateCallback(new IDialogUpdateCallback.IDialogActionProcessor<ELTVideoItem>() {
                        @Override
                        public void process(IModel<ELTVideoItem> model, AjaxRequestTarget target) {
                            close(target);
                            try {
                                videoItemManager.update(model.getObject());
                                setResponsePage(getPage());
                            } catch (CourseException e) {
                                ELTAlerts.renderErrorPopup(e.getMessage(), target);
                            }
                        }
                    });
                }
            };

    private Dialog<VideoPanel> videoEditPanelDialog = new Dialog<VideoPanel>("videoDialog", 695) {
        @Override
        public VideoPanel createDialogPanel(String id) {
            return new VideoPanel(id);
        }

        @Override
        public void registerCallback(VideoPanel panel) {
            super.registerCallback(panel);
            panel.setUpdateCallback(new IDialogUpdateCallback.IDialogActionProcessor<ELTVideoItem>() {
                @Override
                public void process(IModel<ELTVideoItem> model, AjaxRequestTarget target) {
                    close(target);
                    try {
                        videoItemManager.update(model.getObject());
                        target.add(table);
                    } catch (CourseException e) {
                        ELTAlerts.renderErrorPopup(e.getMessage(), target);
                    }
                }
            });
            panel.setNewCallback(new IDialogNewCallback.IDialogActionProcessor<ELTVideoItem>() {
                @Override
                public void process(IModel<ELTVideoItem> model, AjaxRequestTarget target) {
                    genericManager.initialize(VideoCourseItemPanel.this.getModelObject(),
                            VideoCourseItemPanel.this.getModelObject().getItems());

                    model.getObject().setItem(VideoCourseItemPanel.this.getModelObject());
                    model.getObject().setIndex((long) VideoCourseItemPanel.this.getModelObject().getItems().size());
                    close(target);
                    try {
                        videoItemManager.create(model.getObject());
                        target.add(table);
                    } catch (CourseException e) {
                        ELTAlerts.renderErrorPopup(e.getMessage(), target);
                    }
                }
            });
        }
    };

    private Dialog<VideoPreviewDialogPanel> videoPreviewPanelDialog =
            new Dialog<VideoPreviewDialogPanel>("videoPreviewDialog", 690) {
                @Override
                public VideoPreviewDialogPanel createDialogPanel(String id) {
                    return new VideoPreviewDialogPanel(id);
                }
            };

    public VideoCourseItemPanel(String id, IModel<ELTVideoCourseItem> eltCourseItemIModel) {
        super(id, eltCourseItemIModel);

        table = new ELTTable<ELTVideoItem>("grid", 30) {
            @Override
            protected List<IColumn<ELTVideoItem>> getColumns() {
                List<IColumn<ELTVideoItem>> columns = new ArrayList<>();
                columns.add(new AbstractColumn<ELTVideoItem>(ReadonlyObjects.EMPTY_DISPLAY_MODEL) {
                    @Override
                    public void populateItem(Item<ICellPopulator<ELTVideoItem>> cellItem, String componentId,
                                             IModel<ELTVideoItem> rowModel) {
                        cellItem.add(new Label(componentId, String.valueOf(rowModel.getObject().getIndex() + 1)));
                    }
                });
                columns.add(new PropertyColumn(new ResourceModel("name.column"), "name"));
                return columns;
            }

            @Override
            protected Iterator getIterator(int first, int count) {
                return videoItemManager.getItems(
                        VideoCourseItemPanel.this.getModelObject(), first, count, "index", true).iterator();
            }

            @Override
            protected int getSize() {
                return videoItemManager.getCount(VideoCourseItemPanel.this.getModelObject());
            }

            @Override
            protected List<GridAction> getGridActions(IModel<ELTVideoItem> rowModel) {
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
                return new ArrayList<>(Arrays.asList(GridAction.ADD, GridAction.PAGE_PREVIEW));
            }

            @Override
            protected boolean hasConfirmation(GridAction action) {
                return action.equals(GridAction.REMOVE);
            }

            @Override
            protected String getActionTooltip(GridAction action) {
                switch (action) {
                    case EDIT:
                        return getString("edit.tooltip");
                    case PREVIEW:
                        return getString("preview.tooltip");
                    case INFO:
                        return getString("desc.tooltip");
                    case REMOVE:
                        return getString("remove.tooltip");
                    case ADD:
                        return getString("add.tooltip");
                    case UP:
                        return getString("up.tooltip");
                    case DOWN:
                        return getString("down.tooltip");
                    case PAGE_PREVIEW:
                        return getString("page.preview.tooltip");
                    default:
                        return StringUtils.EMPTY;
                }
            }

            @Override
            protected boolean isActionVisible(GridAction action, IModel<ELTVideoItem> rowModel) {
                genericManager.initialize(rowModel.getObject(), rowModel.getObject().getItem());

                Long index = rowModel.getObject().getIndex();
                return !(action.equals(GridAction.UP) && (index == 0)) &&
                        !(action.equals(GridAction.DOWN) &&
                                (index >= (videoItemManager.getCount(rowModel.getObject().getItem()) - 1)));
            }

            @Override
            protected void onClick(IModel<ELTVideoItem> rowModel, GridAction action, AjaxRequestTarget target) {
                switch (action) {
                    case ADD:
                        videoEditPanelDialog.getDialogPanel().initCreate();
                        videoEditPanelDialog.show(target);
                        break;
                    case EDIT:
                        videoEditPanelDialog.getDialogPanel().initData(rowModel);
                        videoEditPanelDialog.show(target);
                        break;
                    case INFO:
                        descriptionPanelDialog.getDialogPanel().initData(rowModel);
                        descriptionPanelDialog.show(target);
                        break;
                    case UP:
                        try {
                            videoItemManager.moveUp(rowModel.getObject());
                            target.add(table);
                        } catch (CourseException e) {
                            ELTAlerts.renderErrorPopup(e.getMessage(), target);
                        }
                        break;
                    case DOWN:
                        try {
                            videoItemManager.moveDown(rowModel.getObject());
                            target.add(table);
                        } catch (CourseException e) {
                            ELTAlerts.renderErrorPopup(e.getMessage(), target);
                        }
                        break;
                    case REMOVE:
                        try {
                            videoItemManager.delete(rowModel.getObject());
                            target.add(table);
                        } catch (CourseException e) {
                            ELTAlerts.renderErrorPopup(e.getMessage(), target);
                        }
                        break;
                    case PREVIEW:
                        videoPreviewPanelDialog.getDialogPanel().initData(rowModel);
                        videoPreviewPanelDialog.show(target);
                        break;
                    case PAGE_PREVIEW:
                        previewContainer.setVisible(true);
                        table.setVisible(false);
                        target.add(previewContainer);
                        target.add(table);
                        break;
                }
            }
        };
        add(table.setOutputMarkupId(true));
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
