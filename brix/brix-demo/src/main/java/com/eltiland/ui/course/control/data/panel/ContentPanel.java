package com.eltiland.ui.course.control.data.panel;

import com.eltiland.bl.GenericManager;
import com.eltiland.bl.course.ELTCourseBlockManager;
import com.eltiland.bl.course.ELTCourseItemManager;
import com.eltiland.bl.course.ELTCourseManager;
import com.eltiland.bl.drive.GoogleDriveManager;
import com.eltiland.exceptions.CourseException;
import com.eltiland.exceptions.GoogleDriveException;
import com.eltiland.model.course2.AuthorCourse;
import com.eltiland.model.course2.ContentStatus;
import com.eltiland.model.course2.ELTCourse;
import com.eltiland.model.course2.content.ELTCourseBlock;
import com.eltiland.model.course2.content.ELTCourseItem;
import com.eltiland.model.course2.content.google.ELTDocumentCourseItem;
import com.eltiland.model.course2.content.google.ELTGoogleCourseItem;
import com.eltiland.model.course2.content.google.ELTPresentationCourseItem;
import com.eltiland.model.course2.content.group.ELTGroupCourseItem;
import com.eltiland.model.google.GoogleDriveFile;
import com.eltiland.ui.common.BaseEltilandPanel;
import com.eltiland.ui.common.components.ReadonlyObjects;
import com.eltiland.ui.common.components.ResourcesUtils;
import com.eltiland.ui.common.components.behavior.TooltipBehavior;
import com.eltiland.ui.common.components.button.EltiAjaxLink;
import com.eltiland.ui.common.components.button.EltiSpinAjaxDecorator;
import com.eltiland.ui.common.components.dialog.Dialog;
import com.eltiland.ui.common.components.dialog.ELTAlerts;
import com.eltiland.ui.common.components.dialog.callback.IDialogNewCallback;
import com.eltiland.ui.common.components.dialog.callback.IDialogSimpleNewCallback;
import com.eltiland.ui.common.components.dialog.callback.IDialogUpdateCallback;
import com.eltiland.ui.common.components.grid.ELTTable;
import com.eltiland.ui.common.components.grid.GridAction;
import com.eltiland.ui.common.components.interval.ELTIntervalDialog;
import com.eltiland.ui.course.CourseNewContentPage;
import com.eltiland.ui.course.control.data.panel.block.BlockAccessPanel;
import com.eltiland.ui.course.control.data.panel.block.BlockControlPanel;
import com.eltiland.ui.course.control.data.panel.block.BlockInfoPanel;
import com.eltiland.ui.course.control.data.panel.block.BlockPropertyPanel;
import com.eltiland.ui.course.control.data.panel.item.ItemPropertyPanel;
import com.eltiland.ui.course.control.data.panel.item.ItemTypeSelector;
import org.apache.commons.lang.StringUtils;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.IAjaxCallDecorator;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.table.AbstractColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.PropertyColumn;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * Internal content editing panel.
 *
 * @author Aleksey Plotnikov.
 */
public class ContentPanel extends BaseEltilandPanel<ELTCourse> {

    @SpringBean
    private ELTCourseBlockManager courseBlockManager;
    @SpringBean
    private GenericManager genericManager;
    @SpringBean
    private ELTCourseManager courseManager;
    @SpringBean
    private ELTCourseItemManager courseItemManager;
    @SpringBean
    private GoogleDriveManager googleDriveManager;

    private ContentStatus status;

    private Dialog<BlockPropertyPanel> blockDialog = new Dialog<BlockPropertyPanel>("blockDialog", 515) {
        @Override
        public BlockPropertyPanel createDialogPanel(String id) {
            return new BlockPropertyPanel(id);
        }

        @Override
        public void registerCallback(BlockPropertyPanel panel) {
            super.registerCallback(panel);
            panel.setSimpleNewCallback(new IDialogSimpleNewCallback.IDialogActionProcessor<String>() {
                @Override
                public void process(IModel<String> model, AjaxRequestTarget target) {
                    close(target);
                    ELTCourseBlock block = new ELTCourseBlock();
                    int blockCount = 0;

                    if (status.equals(ContentStatus.DEMO)) {
                        block.setDemoCourse((AuthorCourse) ContentPanel.this.getModelObject());
                        genericManager.initialize(ContentPanel.this.getModelObject(),
                                ((AuthorCourse) ContentPanel.this.getModelObject()).getDemoContent());
                        blockCount = ((AuthorCourse) ContentPanel.this.getModelObject()).getDemoContent().size();
                        ((AuthorCourse) ContentPanel.this.getModelObject()).getDemoContent().add(block);
                    } else {
                        block.setCourse(ContentPanel.this.getModelObject());
                        genericManager.initialize(ContentPanel.this.getModelObject(),
                                ContentPanel.this.getModelObject().getContent());
                        blockCount = ContentPanel.this.getModelObject().getContent().size();
                        ContentPanel.this.getModelObject().getContent().add(block);
                    }
                    block.setName(model.getObject());
                    block.setIndex(blockCount + 1);
                    block.setDefaultAccess(true);

                    try {
                        courseBlockManager.create(block);
                        courseManager.update(ContentPanel.this.getModelObject());
                        target.add(grid);
                    } catch (CourseException e) {
                        ELTAlerts.renderErrorPopup(e.getMessage(), target);
                    }
                }
            });
            panel.setUpdateCallback(new IDialogUpdateCallback.IDialogActionProcessor<ELTCourseBlock>() {
                @Override
                public void process(IModel<ELTCourseBlock> model, AjaxRequestTarget target) {
                    close(target);
                    try {
                        courseBlockManager.update(model.getObject());
                        target.add(grid);
                    } catch (CourseException e) {
                        ELTAlerts.renderErrorPopup(e.getMessage(), target);
                    }
                }
            });
        }
    };

    private Dialog<ELTIntervalDialog<ELTCourseBlock>> intervalDialog =
            new Dialog<ELTIntervalDialog<ELTCourseBlock>>("intervalDialog", 435) {
                @Override
                public ELTIntervalDialog<ELTCourseBlock> createDialogPanel(String id) {
                    return new ELTIntervalDialog<ELTCourseBlock>(id) {
                        @Override
                        protected String getHeader() {
                            return ContentPanel.this.getString("interval.header");
                        }
                    };
                }

                @Override
                public void registerCallback(ELTIntervalDialog<ELTCourseBlock> panel) {
                    super.registerCallback(panel);
                    panel.setUpdateCallback(new IDialogUpdateCallback.IDialogActionProcessor<ELTCourseBlock>() {
                        @Override
                        public void process(IModel<ELTCourseBlock> model, AjaxRequestTarget target) {
                            close(target);
                            try {
                                courseBlockManager.update(model.getObject());
                                target.add(grid);
                            } catch (CourseException e) {
                                ELTAlerts.renderErrorPopup(e.getMessage(), target);
                            }
                        }
                    });
                }
            };

    private Dialog<BlockAccessPanel> accessDialog = new Dialog<BlockAccessPanel>("blockAccessDialog", 700) {
        @Override
        public BlockAccessPanel createDialogPanel(String id) {
            return new BlockAccessPanel(id);
        }
    };

    private Dialog<ItemPropertyPanel> nameDialog = new Dialog<ItemPropertyPanel>("nameDialog", 440) {
        @Override
        public ItemPropertyPanel createDialogPanel(String id) {
            return new ItemPropertyPanel(id);
        }

        @Override
        public void registerCallback(ItemPropertyPanel panel) {
            super.registerCallback(panel);
            panel.setNewCallback(new IDialogNewCallback.IDialogActionProcessor<ELTCourseItem>() {
                @Override
                public void process(IModel<ELTCourseItem> model, AjaxRequestTarget target) {
                    close(target);
                    try {

                        //**** Creating course element logic ****////
                        /**********************************************/
                        if (model.getObject().getParent() == null) {
                            ELTCourseBlock block = model.getObject().getBlock();
                            genericManager.initialize(block, block.getItems());
                            model.getObject().setIndex((long) block.getItems().size());
                        } else {
                            ELTGroupCourseItem item = model.getObject().getParent();
                            genericManager.initialize(item, item.getItems());
                            model.getObject().setIndex((long) item.getItems().size());
                        }

                        if (model.getObject() instanceof ELTGoogleCourseItem) {
                            GoogleDriveFile file = null;
                            if (model.getObject() instanceof ELTDocumentCourseItem) {
                                file = googleDriveManager.createEmptyDoc(
                                        model.getObject().getName(), GoogleDriveFile.TYPE.DOCUMENT);
                            } else if (model.getObject() instanceof ELTPresentationCourseItem) {
                                file = googleDriveManager.createEmptyDoc(
                                        model.getObject().getName(), GoogleDriveFile.TYPE.PRESENTATION);
                            }
                            ((ELTGoogleCourseItem) model.getObject()).setItem(file);
                            ((ELTGoogleCourseItem) model.getObject()).setHasWarning(false);
                        }

                        courseItemManager.create(model.getObject());
                        target.add(grid);
                    } catch (CourseException | GoogleDriveException e) {
                        ELTAlerts.renderErrorPopup(e.getMessage(), target);
                    }
                }
            });
            panel.setUpdateCallback(new IDialogUpdateCallback.IDialogActionProcessor<ELTCourseItem>() {
                @Override
                public void process(IModel<ELTCourseItem> model, AjaxRequestTarget target) {
                    close(target);
                }
            });
        }
    };

    private Dialog<ItemTypeSelector> typeSelector = new Dialog<ItemTypeSelector>("typeSelectorDialog", 460) {
        @Override
        public ItemTypeSelector createDialogPanel(String id) {
            return new ItemTypeSelector(id);
        }

        @Override
        public void registerCallback(ItemTypeSelector panel) {
            super.registerCallback(panel);
            panel.setNewCallback(new IDialogNewCallback.IDialogActionProcessor<ELTCourseItem>() {
                @Override
                public void process(IModel<ELTCourseItem> model, AjaxRequestTarget target) {
                    close(target);
                    nameDialog.getDialogPanel().initData(model);
                    nameDialog.show(target);
                }
            });
        }
    };

    private ELTTable<ELTCourseBlock> grid = new ELTTable<ELTCourseBlock>("grid", 100) {
        @Override
        protected List<IColumn<ELTCourseBlock>> getColumns() {
            List<IColumn<ELTCourseBlock>> columns = new ArrayList<>();
            columns.add(new PropertyColumn<ELTCourseBlock>(ReadonlyObjects.EMPTY_DISPLAY_MODEL, "index"));
            columns.add(new AbstractColumn<ELTCourseBlock>(new ResourceModel("name.column")) {
                @Override
                public void populateItem(Item<ICellPopulator<ELTCourseBlock>> cellItem,
                                         String componentId, final IModel<ELTCourseBlock> rowModel) {
                    cellItem.add(new BlockControlPanel(componentId, rowModel) {
                        @Override
                        protected void onAdd(AjaxRequestTarget target) {
                            typeSelector.getDialogPanel().initData(rowModel);
                            typeSelector.show(target);
                        }
                    });
                }
            });
            columns.add(new AbstractColumn<ELTCourseBlock>(new ResourceModel("settings.column")) {
                @Override
                public void populateItem(Item<ICellPopulator<ELTCourseBlock>> cellItem,
                                         String componentId, IModel<ELTCourseBlock> rowModel) {
                    cellItem.add(new BlockInfoPanel(componentId, rowModel));
                }
            });
            return columns;
        }

        @Override
        protected Iterator getIterator(int first, int count) {
            return courseBlockManager.getSortedBlockList(getModelObject(), status).iterator();
        }

        @Override
        protected int getSize() {
            if (status.equals(ContentStatus.DEMO)) {
                genericManager.initialize(getModelObject(), ((AuthorCourse) getModelObject()).getDemoContent());
                return ((AuthorCourse) getModelObject()).getDemoContent().size();
            } else {
                genericManager.initialize(getModelObject(), getModelObject().getContent());
                return getModelObject().getContent().size();
            }
        }

        @Override
        protected String getNotFoundedMessage() {
            return ContentPanel.this.getString("empty.course");
        }

        @Override
        protected List<GridAction> getGridActions(IModel<ELTCourseBlock> rowModel) {
            return new ArrayList<>(Arrays.asList(
                    GridAction.EDIT, GridAction.UP, GridAction.DOWN, GridAction.TIME,
                    GridAction.USERS, GridAction.LOCK, GridAction.UNLOCK));
        }

        @Override
        protected boolean isActionVisible(GridAction action, IModel<ELTCourseBlock> rowModel) {
            int index = rowModel.getObject().getIndex();
            switch (action) {
                case UP:
                    return index > 1;
                case DOWN:
                    return index < getSize();
                case LOCK:
                    return rowModel.getObject().getDefaultAccess();
                case UNLOCK:
                    return !rowModel.getObject().getDefaultAccess();
                case TIME:
                    return rowModel.getObject().getDefaultAccess();
                default:
                    return true;
            }
        }

        @Override
        protected String getActionTooltip(GridAction action) {
            switch (action) {
                case EDIT:
                    return getString("edit.tooltip");
                case UP:
                    return getString("up.tooltip");
                case DOWN:
                    return getString("down.tooltip");
                case TIME:
                    return getString("time.tooltip");
                case USERS:
                    return getString("users.tooltip");
                case LOCK:
                    return getString("lock.tooltip");
                case UNLOCK:
                    return getString("unlock.tooltip");
                default:
                    return StringUtils.EMPTY;
            }
        }

        @Override
        protected void onClick(IModel<ELTCourseBlock> rowModel, GridAction action, AjaxRequestTarget target) {
            switch (action) {
                case EDIT:
                    blockDialog.getDialogPanel().setModeEdit(rowModel);
                    blockDialog.show(target);
                    break;
                case UP:
                    try {
                        courseBlockManager.moveUp(rowModel.getObject());
                        target.add(grid);
                    } catch (CourseException e) {
                        ELTAlerts.renderErrorPopup(e.getMessage(), target);
                    }
                    break;
                case DOWN:
                    try {
                        courseBlockManager.moveDown(rowModel.getObject());
                        target.add(grid);
                    } catch (CourseException e) {
                        ELTAlerts.renderErrorPopup(e.getMessage(), target);
                    }
                    break;
                case TIME:
                    intervalDialog.getDialogPanel().initPanel(rowModel);
                    intervalDialog.show(target);
                    break;
                case USERS:
                    accessDialog.getDialogPanel().initData(rowModel);
                    accessDialog.show(target);
                    break;
                case LOCK:
                    rowModel.getObject().setDefaultAccess(false);
                    rowModel.getObject().setStartDate(null);
                    rowModel.getObject().setEndDate(null);
                    try {
                        courseBlockManager.update(rowModel.getObject());
                        target.add(grid);
                    } catch (CourseException e) {
                        ELTAlerts.renderErrorPopup(e.getMessage(), target);
                    }
                    break;
                case UNLOCK:
                    rowModel.getObject().setDefaultAccess(true);
                    try {
                        courseBlockManager.update(rowModel.getObject());
                        target.add(grid);
                    } catch (CourseException e) {
                        ELTAlerts.renderErrorPopup(e.getMessage(), target);
                    }
                    break;
            }
        }
    };

    /**
     * Panel ctor.
     *
     * @param id              markup id.
     * @param eltCourseIModel course model.
     * @param status          content kind.
     */
    public ContentPanel(String id, IModel<ELTCourse> eltCourseIModel, ContentStatus status) {
        super(id, eltCourseIModel);
        this.status = status;

        EltiAjaxLink button = new EltiAjaxLink("button") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                blockDialog.getDialogPanel().setModeCreate();
                blockDialog.show(target);
            }

            @Override
            protected IAjaxCallDecorator getAjaxCallDecorator() {
                return new EltiSpinAjaxDecorator(ContentPanel.this);
            }
        };

        add(button);
        WebMarkupContainer image = new WebMarkupContainer("image");
        button.add(image);
        button.add(new AttributeModifier("title", new ResourceModel("add.tooltip")));
        button.add(new TooltipBehavior());

        EltiAjaxLink preview = new EltiAjaxLink("preview") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                throw new RestartResponseException(CourseNewContentPage.class, new PageParameters().add(
                        CourseNewContentPage.PARAM_ID, ContentPanel.this.getModelObject().getId()).add(
                        CourseNewContentPage.PARAM_VERSION, ContentPanel.this.status.equals(ContentStatus.DEMO) ?
                        CourseNewContentPage.DEMO_VERSION : CourseNewContentPage.FULL_VERSION));
            }
        };

        add(preview);
        WebMarkupContainer previewImage = new WebMarkupContainer("previewImage");
        preview.add(previewImage);
        preview.add(new AttributeModifier("title", new ResourceModel("preview.tooltip")));
        preview.add(new TooltipBehavior());

        add(grid.setOutputMarkupId(true));
        add(blockDialog);
        add(intervalDialog);
        add(accessDialog);
        add(typeSelector);
        add(nameDialog);
    }

    public void setStatus(ContentStatus status) {
        this.status = status;
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
        response.renderCSSReference(ResourcesUtils.CSS_COURSE_CONTENT_EDITING);
        response.renderCSSReference(ResourcesUtils.CSS_NEW_TABLE_STYLE);
    }
}
