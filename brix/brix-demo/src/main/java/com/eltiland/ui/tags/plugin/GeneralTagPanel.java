package com.eltiland.ui.tags.plugin;

import com.eltiland.bl.GenericManager;
import com.eltiland.bl.tags.TagCategoryManager;
import com.eltiland.bl.tags.TagEntityManager;
import com.eltiland.bl.tags.TagManager;
import com.eltiland.exceptions.EltilandManagerException;
import com.eltiland.model.tags.ITagable;
import com.eltiland.model.tags.Tag;
import com.eltiland.model.tags.TagCategory;
import com.eltiland.ui.common.BaseEltilandPanel;
import com.eltiland.ui.common.column.PropertyWrapColumn;
import com.eltiland.ui.common.components.ReadonlyObjects;
import com.eltiland.ui.common.components.ResourcesUtils;
import com.eltiland.ui.common.components.button.EltiAjaxLink;
import com.eltiland.ui.common.components.datagrid.EltiDataGrid;
import com.eltiland.ui.common.components.datagrid.EltiDefaultDataGrid;
import com.eltiland.ui.common.components.dialog.Dialog;
import com.eltiland.ui.common.components.dialog.ELTAlerts;
import com.eltiland.ui.common.components.dialog.callback.IDialogNewCallback;
import com.eltiland.ui.common.components.dialog.callback.IDialogUpdateCallback;
import com.eltiland.ui.common.model.GenericDBModel;
import com.eltiland.ui.tags.plugin.panels.TagCategoryPropertyPanel;
import com.eltiland.ui.tags.plugin.panels.TagEntityActionPanel;
import com.eltiland.ui.tags.plugin.panels.TagPropertyPanel;
import com.inmethod.grid.IDataSource;
import com.inmethod.grid.IGridColumn;
import com.inmethod.grid.IGridSortState;
import com.inmethod.grid.column.AbstractColumn;
import org.apache.wicket.Component;
import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Panel for tag management. Can be used both in admin or application.
 *
 * @author Aleksey Plotnikov.
 */
public class GeneralTagPanel extends BaseEltilandPanel<Class<? extends ITagable>> {

    private static final Logger LOGGER = LoggerFactory.getLogger(GeneralTagPanel.class);

    @SpringBean
    private TagCategoryManager tagCategoryManager;
    @SpringBean
    private GenericManager genericManager;
    @SpringBean
    private TagManager tagManager;
    @SpringBean
    private TagEntityManager tagEntityManager;

    private Class<? extends ITagable> clazz;

    private final EltiDataGrid<TagCategoryDataSource, TagCategory> grid;
    private final EltiDataGrid<TagDataSource, Tag> tagGrid;

    private IModel<TagCategory> currentCategoryModel = new GenericDBModel<>(TagCategory.class);

    private Dialog<TagCategoryPropertyPanel> tagCategoryPropertyPanelDialog =
            new Dialog<TagCategoryPropertyPanel>("tagCategoryCreateDialog", 320) {
                @Override
                public TagCategoryPropertyPanel createDialogPanel(String id) {
                    return new TagCategoryPropertyPanel(id, clazz);
                }

                @Override
                public void registerCallback(TagCategoryPropertyPanel panel) {
                    super.registerCallback(panel);
                    panel.setNewCallback(new IDialogNewCallback.IDialogActionProcessor<TagCategory>() {
                        @Override
                        public void process(IModel<TagCategory> model, AjaxRequestTarget target) {
                            close(target);
                            target.add(grid);
                            ELTAlerts.renderOKPopup(getString("tagCategoryCreatedMessage"), target);
                        }
                    });
                    panel.setUpdateCallback(new IDialogUpdateCallback.IDialogActionProcessor<TagCategory>() {
                        @Override
                        public void process(IModel<TagCategory> model, AjaxRequestTarget target) {
                            close(target);
                            target.add(grid);
                            ELTAlerts.renderOKPopup(getString("tagCategoryUpdatedMessage"), target);
                        }
                    });
                }
            };

    private Dialog<TagPropertyPanel> tagPropertyPanelDialog = new Dialog<TagPropertyPanel>("tagCreateDialog", 320) {
        @Override
        public TagPropertyPanel createDialogPanel(String id) {
            return new TagPropertyPanel(id);
        }

        @Override
        public void registerCallback(TagPropertyPanel panel) {
            super.registerCallback(panel);

            panel.setNewCallback(new IDialogNewCallback.IDialogActionProcessor<Tag>() {
                @Override
                public void process(IModel<Tag> model, AjaxRequestTarget target) {
                    close(target);
                    target.add(tagGrid);
                    ELTAlerts.renderOKPopup(getString("tagCreatedMessage"), target);
                }
            });

            panel.setUpdateCallback(new IDialogUpdateCallback.IDialogActionProcessor<Tag>() {
                @Override
                public void process(IModel<Tag> model, AjaxRequestTarget target) {
                    close(target);
                    target.add(tagGrid);
                    ELTAlerts.renderOKPopup(getString("tagUpdatedMessage"), target);
                }
            });
        }
    };

    private EltiAjaxLink addTagButton = new EltiAjaxLink("addTagButton") {
        @Override
        public void onClick(AjaxRequestTarget target) {
            tagPropertyPanelDialog.getDialogPanel().initCreateMode(currentCategoryModel.getObject());
            tagPropertyPanelDialog.show(target);
        }
    };

    private Label tagLabel = new Label("tagLabel", new Model<String>());


    public GeneralTagPanel(String id, Class<? extends ITagable> clazz) {
        super(id);
        this.clazz = clazz;

        add(new EltiAjaxLink("addButton") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                tagCategoryPropertyPanelDialog.getDialogPanel().initCreateMode();
                tagCategoryPropertyPanelDialog.show(target);
            }
        });

        List<IGridColumn<TagCategoryDataSource, TagCategory>> columns = new ArrayList<>();
        columns.add(new PropertyWrapColumn(new ResourceModel("nameColumn"), "name", "name", 310));
        columns.add(new AbstractColumn<TagCategoryDataSource, TagCategory>(
                "actionColumn", ReadonlyObjects.EMPTY_DISPLAY_MODEL) {
            @Override
            public Component newCell(WebMarkupContainer parent, String componentId, final IModel<TagCategory> rowModel) {
                return new TagEntityActionPanel(componentId) {
                    @Override
                    protected void onEdit(AjaxRequestTarget target) {
                        tagCategoryPropertyPanelDialog.getDialogPanel().initEditMode(rowModel.getObject());
                        tagCategoryPropertyPanelDialog.show(target);
                    }

                    @Override
                    protected void onDelete(AjaxRequestTarget target) {
                        try {
                            tagCategoryManager.deleteTagCategory(rowModel.getObject());
                        } catch (EltilandManagerException e) {
                            LOGGER.error("Got exception when deleting tag category entity", e);
                            throw new WicketRuntimeException("Got exception when deleting tag category entity", e);
                        }
                        target.add(grid);
                        ELTAlerts.renderOKPopup(getString("tagCategoryDeletedMessage"), target);
                    }
                };
            }
        });

        grid = new EltiDefaultDataGrid<TagCategoryDataSource, TagCategory>(
                "grid", new Model<>(new TagCategoryDataSource()), columns) {

            @Override
            protected boolean onCellClicked(AjaxRequestTarget target, IModel<TagCategory> rowModel,
                                            IGridColumn<TagCategoryDataSource, TagCategory> column) {
                if (!(column.getId().equals("actionColumn"))) {
                    super.onRowClicked(target, rowModel);
                    currentCategoryModel.setObject(rowModel.getObject());
                    currentCategoryModel.detach();
                    tagGrid.setVisible(true);
                    addTagButton.setVisible(true);
                    tagLabel.setDefaultModelObject(String.format(getString("tagTitle"), rowModel.getObject().getName()));
                    target.add(tagLabel);
                    target.add(tagGrid);
                    target.add(addTagButton);
                }
                return true;
            }
        };


        List<IGridColumn<TagDataSource, Tag>> tagColumns = new ArrayList<>();
        tagColumns.add(new PropertyWrapColumn(new ResourceModel("nameTagColumn"), "name", "name", 310));
        tagColumns.add(new AbstractColumn<TagDataSource, Tag>("actionColumn", ReadonlyObjects.EMPTY_DISPLAY_MODEL) {
            @Override
            public Component newCell(WebMarkupContainer components, String s, final IModel<Tag> tagIModel) {
                return new TagEntityActionPanel(s) {
                    @Override
                    protected void onEdit(AjaxRequestTarget target) {
                        tagPropertyPanelDialog.getDialogPanel().initEditMode(tagIModel.getObject());
                        tagPropertyPanelDialog.show(target);
                    }

                    @Override
                    protected void onDelete(AjaxRequestTarget target) {
                        try {
                            tagEntityManager.deleteTagEntity(tagIModel.getObject().getId());
                            genericManager.delete(tagIModel.getObject());

                        } catch (EltilandManagerException e) {
                            LOGGER.error("Got exception when deleting tag entity", e);
                            throw new WicketRuntimeException("Got exception when deleting tag entity", e);
                        }
                        target.add(tagGrid);
                        ELTAlerts.renderOKPopup(getString("tagDeletedMessage"), target);
                    }
                };
            }
        });
        tagGrid = new EltiDefaultDataGrid<>("tagGrid", new Model<>(new TagDataSource()), tagColumns);

        add(grid.setOutputMarkupId(true));
        add(tagGrid.setOutputMarkupPlaceholderTag(true));

        add(tagCategoryPropertyPanelDialog);
        add(tagPropertyPanelDialog);

        tagGrid.setVisible(false);
        tagLabel.setOutputMarkupPlaceholderTag(true);

        add(addTagButton);
        addTagButton.setVisible(false);
        addTagButton.setOutputMarkupPlaceholderTag(true);

        add(tagLabel);
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
        response.renderCSSReference(ResourcesUtils.CSS_TAGS);
    }

    /**
     * Tag category data source.
     */
    private class TagCategoryDataSource implements IDataSource<TagCategory> {

        @Override
        public void query(IQuery query, IQueryResult<TagCategory> result) {
            int count = tagCategoryManager.getCategoryCount(clazz.getSimpleName());
            result.setTotalCount(count);

            if (count < 1) {
                result.setItems(Collections.<TagCategory>emptyIterator());
            }

            String sortProperty = null;
            boolean isAscending = true;

            if (!query.getSortState().getColumns().isEmpty()) {
                IGridSortState.ISortStateColumn sortingColumn = query.getSortState().getColumns().get(0);
                sortProperty = sortingColumn.getPropertyName();
                isAscending = sortingColumn.getDirection() == IGridSortState.Direction.ASC;
            }

            boolean isSorting = (!(sortProperty == null || sortProperty.isEmpty()));

            result.setItems(tagCategoryManager.getCategoryList(
                    clazz.getSimpleName(), isSorting, isAscending).iterator());
        }

        @Override
        public IModel<TagCategory> model(TagCategory object) {
            return new GenericDBModel<>(TagCategory.class, object);
        }

        @Override
        public void detach() {
        }
    }

    /**
     * Tag data source.
     */
    private class TagDataSource implements IDataSource<Tag> {

        @Override
        public void query(IQuery query, IQueryResult<Tag> result) {
            TagCategory category = currentCategoryModel.getObject();
            if (category == null) {
                result.setItems(Collections.<Tag>emptyIterator());
            } else {
                category = genericManager.getObject(TagCategory.class, category.getId());
                genericManager.initialize(category, category.getTags());

                int count = category.getTags().size();
                result.setTotalCount(count);

                if (count < 1) {
                    result.setItems(Collections.<Tag>emptyIterator());
                } else {
                    String sortProperty = null;
                    boolean isAscending = true;

                    if (!query.getSortState().getColumns().isEmpty()) {
                        IGridSortState.ISortStateColumn sortingColumn = query.getSortState().getColumns().get(0);
                        sortProperty = sortingColumn.getPropertyName();
                        isAscending = sortingColumn.getDirection() == IGridSortState.Direction.ASC;
                    }

                    boolean isSorting = (!(sortProperty == null || sortProperty.isEmpty()));
                    result.setItems(tagManager.getTagList(category, isSorting, isAscending).iterator());
                }
            }
        }

        @Override
        public IModel<Tag> model(Tag object) {
            return new GenericDBModel<>(Tag.class, object);
        }

        @Override
        public void detach() {
        }
    }
}
