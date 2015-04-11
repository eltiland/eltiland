package com.eltiland.ui.faq.plugin.tab;

import com.eltiland.bl.FaqCategoryManager;
import com.eltiland.exceptions.FaqException;
import com.eltiland.model.faq.FaqCategory;
import com.eltiland.ui.common.BaseEltilandPanel;
import com.eltiland.ui.common.components.ResourcesUtils;
import com.eltiland.ui.common.components.UIConstants;
import com.eltiland.ui.common.components.dialog.Dialog;
import com.eltiland.ui.common.components.dialog.ELTAlerts;
import com.eltiland.ui.common.components.dialog.callback.IDialogSimpleNewCallback;
import com.eltiland.ui.common.components.dialog.callback.IDialogSimpleUpdateCallback;
import com.eltiland.ui.common.components.grid.ELTTable;
import com.eltiland.ui.common.components.grid.GridAction;
import com.eltiland.ui.faq.plugin.components.CreateCategoryPanel;
import com.eltiland.ui.faq.plugin.components.EditQAPanel;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.PropertyColumn;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.brixcms.workspace.Workspace;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * Panel for editing faq categories
 *
 * @author Pavel Androschuk
 */
public class FaqCategoryManagementPanel extends BaseEltilandPanel<Workspace> {
    @SpringBean
    private FaqCategoryManager faqCategoryManager;

    private final ELTTable<FaqCategory> grid;

    private IModel<FaqCategory> currentModel;

    private Dialog<EditQAPanel> editQAPanelDialog = new Dialog<EditQAPanel>("editQADialog",
            UIConstants.DIALOG_POPUP_WIDTH_SMALL) {
        @Override
        public EditQAPanel createDialogPanel(String id) {
            return new EditQAPanel(id);
        }

        @Override
        public void registerCallback(EditQAPanel panel) {
            super.registerCallback(panel);
            panel.setSimpleUpdateCallback(
                    new IDialogSimpleUpdateCallback.IDialogActionProcessor<String>() {
                        @Override
                        public void process(IModel<String> model, AjaxRequestTarget target) {
                            currentModel.getObject().setName(model.getObject());
                            try {
                                faqCategoryManager.update(currentModel.getObject());
                            } catch (FaqException e) {
                                ELTAlerts.renderErrorPopup(e.getMessage(), target);
                                return;
                            }
                            target.add(grid);
                            close(target);
                            ELTAlerts.renderOKPopup(getString("categorySavedMessage"), target);
                        }
                    });
        }
    };

    private Dialog<CreateCategoryPanel> createCategoryPanelDialog = new Dialog<CreateCategoryPanel>(
            "createCategoryDialog", UIConstants.DIALOG_POPUP_WIDTH_SMALL) {
        @Override
        public CreateCategoryPanel createDialogPanel(String id) {
            return new CreateCategoryPanel(id);
        }

        @Override
        protected void onClose(AjaxRequestTarget target) {
            super.onClose(target);
        }

        @Override
        public void registerCallback(CreateCategoryPanel panel) {
            super.registerCallback(panel);
            panel.setSimpleNewCallback(new IDialogSimpleNewCallback.IDialogActionProcessor<FaqCategory>() {
                @Override
                public void process(IModel<FaqCategory> model, AjaxRequestTarget target) {
                    try {
                        faqCategoryManager.create(model.getObject());
                    } catch (FaqException e) {
                        ELTAlerts.renderErrorPopup(e.getMessage(), target);
                        return;
                    }
                    close(target);
                    target.add(grid);
                    ELTAlerts.renderOKPopup(getString("categoryAddedMessage"), target);
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
    public FaqCategoryManagementPanel(String id, IModel<Workspace> workspaceIModel) {
        super(id, workspaceIModel);

        final Form form = new Form("form");
        add(form);

        form.add(createCategoryPanelDialog);
        form.add(editQAPanelDialog);

        grid = new ELTTable<FaqCategory>("grid", 10) {
            @Override
            protected boolean isSearching() {
                return true;
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
            protected List<IColumn<FaqCategory>> getColumns() {
                ArrayList<IColumn<FaqCategory>> columns = new ArrayList<>();

                columns.add(new PropertyColumn<FaqCategory>(new ResourceModel("numberColumnLabel"), "number",
                        "number"));
                columns.add(new PropertyColumn<FaqCategory>(new ResourceModel("nameColumnLabel"), "name", "name"));

                return columns;
            }

            @Override
            protected List<GridAction> getGridActions(IModel<FaqCategory> rowModel) {
                return new ArrayList<>(Arrays.asList(GridAction.UP, GridAction.DOWN, GridAction.EDIT,
                        GridAction.REMOVE));
            }

            @Override
            protected boolean isActionVisible(GridAction action, IModel<FaqCategory> rowModel) {
                FaqCategory faqCategory;

                switch (action) {
                    case UP:
                        faqCategory = rowModel.getObject();
                        return faqCategory.getNumber() != 1;
                    case DOWN:
                        faqCategory = rowModel.getObject();
                        return faqCategory.getNumber() != faqCategoryManager.getCount(null);
                    default:
                        return true;
                }
            }

            @Override
            protected String getActionTooltip(GridAction action) {
                switch (action) {
                    case EDIT:
                        return getString("editCategoryTooltip");
                    case REMOVE:
                        return getString("deleteCategoryTooltip");
                    default:
                        return "";
                }
            }

            @Override
            protected Iterator getIterator(int first, int count) {
                return faqCategoryManager.getList(first, count, getSearchString()).iterator();
            }

            @Override
            protected int getSize() {
                return faqCategoryManager.getCount(getSearchString());
            }

            @Override
            protected void onClick(final IModel<FaqCategory> rowModel, GridAction action, AjaxRequestTarget target) {
                switch (action) {
                    case ADD:
                        createCategoryPanelDialog.detachDialogPanel();
                        createCategoryPanelDialog.show(target);

                        break;
                    case UP:
                        try {
                            faqCategoryManager.moveUp(rowModel.getObject());
                        } catch (FaqException e) {
                            ELTAlerts.renderErrorPopup(e.getMessage(), target);
                            return;
                        }
                        target.add(grid);

                        break;
                    case DOWN:
                        try {
                            faqCategoryManager.moveDown(rowModel.getObject());
                        } catch (FaqException e) {
                            ELTAlerts.renderErrorPopup(e.getMessage(), target);
                        }
                        target.add(grid);

                        break;
                    case EDIT:
                        currentModel = rowModel;
                        editQAPanelDialog.getDialogPanel().setData(new Model<>(rowModel.getObject().getName()));
                        editQAPanelDialog.show(target);

                        break;
                    case REMOVE:
                        try {
                            faqCategoryManager.delete(rowModel.getObject());
                        } catch (FaqException e) {
                            ELTAlerts.renderErrorPopup(e.getMessage(), target);
                        }
                        target.add(grid);
                        ELTAlerts.renderOKPopup(getString("categoryDeletedMessage"), target);

                        break;
                }
            }
        };

        form.add(grid.setOutputMarkupId(true));
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        response.renderCSSReference(ResourcesUtils.CSS_TOOLTIP);
        response.renderCSSReference(ResourcesUtils.CSS_TOOLTUP_BOX);
        response.renderCSSReference(ResourcesUtils.CSS_NEW_TABLE_STYLE);
    }
}
