package com.eltiland.ui.faq.plugin.tab;

import com.eltiland.bl.FaqManager;
import com.eltiland.exceptions.FaqException;
import com.eltiland.model.faq.Faq;
import com.eltiland.model.faq.FaqCategory;
import com.eltiland.ui.common.BaseEltilandPanel;
import com.eltiland.ui.common.components.ResourcesUtils;
import com.eltiland.ui.common.components.UIConstants;
import com.eltiland.ui.common.components.dialog.Dialog;
import com.eltiland.ui.common.components.dialog.ELTAlerts;
import com.eltiland.ui.common.components.dialog.ELTDialogPanel;
import com.eltiland.ui.common.components.dialog.callback.IDialogSimpleNewCallback;
import com.eltiland.ui.common.components.grid.ELTTable;
import com.eltiland.ui.common.components.grid.GridAction;
import com.eltiland.ui.common.components.textfield.ELTTextArea;
import com.eltiland.ui.faq.components.FaqCategoryList;
import com.eltiland.ui.faq.plugin.components.CreateQAPanel;
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
 * FAQ managements panel.
 */
public class FaqUserManagementPanel extends BaseEltilandPanel<Workspace> {

    @SpringBean
    private FaqManager faqManager;

    private final ELTTable<Faq> grid;

    final Form form = new Form("form");

    private FaqCategoryList category = new FaqCategoryList("category") {
        @Override
        public void onCategoryUpdate(AjaxRequestTarget target, IModel<FaqCategory> model) {
            target.add(form);
        }
    };

    private Dialog<CreateQAPanel> createQAPanelDialog = new Dialog<CreateQAPanel>("createQADialog",
            UIConstants.DIALOG_POPUP_WIDTH_SMALL) {
        @Override
        public CreateQAPanel createDialogPanel(String id) {
            return new CreateQAPanel(id);
        }

        @Override
        protected void onClose(AjaxRequestTarget target) {
            super.onClose(target);
        }

        @Override
        public void registerCallback(CreateQAPanel panel) {
            super.registerCallback(panel);
            panel.setSimpleNewCallback(new IDialogSimpleNewCallback.IDialogActionProcessor<Faq>() {
                @Override
                public void process(IModel<Faq> model, AjaxRequestTarget target) {
                    try {
                        faqManager.create(model.getObject());
                    } catch (FaqException e) {
                        ELTAlerts.renderErrorPopup(e.getMessage(), target);
                        return;
                    }
                    close(target);
                    target.add(grid);
                    ELTAlerts.renderOKPopup(getString("qaAddedMessage"), target);
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
    public FaqUserManagementPanel(String id, IModel<Workspace> workspaceIModel) {
        super(id, workspaceIModel);
        add(form);

        form.add(createQAPanelDialog);
        form.add(editFAQDialog);

        grid = new ELTTable<Faq>("grid", 10) {
            @Override
            protected List<GridAction> getControlActions() {
                return Arrays.asList(GridAction.ADD);
            }

            @Override
            protected boolean isControlling() {
                return true;
            }

            @Override
            protected boolean isSearching() {
                return true;
            }

            @Override
            protected String getActionTooltip(GridAction action) {
                switch (action) {
                    case REMOVE:
                        return getString("deleteTooltip");
                    case EDIT:
                        return getString("editTooltip");
                    default:
                        return "";
                }
            }

            @Override
            protected List<IColumn<Faq>> getColumns() {
                List<IColumn<Faq>> columns = new ArrayList<>();

                columns.add(new PropertyColumn<Faq>(new ResourceModel("numberColumnLabel"), "number", "number"));
                columns.add(new PropertyColumn<Faq>(new ResourceModel("questionColumnLabel"), "question", "question"));
                columns.add(new PropertyColumn<Faq>(new ResourceModel("answerColumnLabel"), "answer", "answer"));

                return columns;
            }

            @Override
            protected List<GridAction> getGridActions(IModel<Faq> rowModel) {
                return Arrays.asList(GridAction.UP, GridAction.DOWN, GridAction.EDIT, GridAction.REMOVE);
            }

            @Override
            protected Iterator getIterator(int first, int count) {
                return faqManager.getFaqList(category.getCategoryModel().getObject(), first, count, getSearchString()).iterator();
            }

            @Override
            protected int getSize() {
                return faqManager.getFaqCount(category.getCategoryModel().getObject(), getSearchString());
            }

            @Override
            protected boolean isActionVisible(GridAction action, IModel<Faq> rowModel) {
                Faq faq;
                switch (action) {
                    case UP:
                        faq = rowModel.getObject();
                        return faq.getNumber() != 1;
                    case DOWN:
                        faq = rowModel.getObject();
                        return faq.getNumber() != faqManager.getFaqCount(faq.getCategory(), null);
                    default:
                        return true;
                }
            }

            @Override
            protected void onClick(IModel<Faq> rowModel, GridAction action, AjaxRequestTarget target) {
                switch (action) {
                    case UP:
                        try {
                            faqManager.moveUp(rowModel.getObject());
                        } catch (FaqException e) {
                            ELTAlerts.renderErrorPopup(e.getMessage(), target);
                            return;
                        }
                        target.add(grid);

                        break;
                    case DOWN:
                        try {
                            faqManager.moveDown(rowModel.getObject());
                        } catch (FaqException e) {
                            ELTAlerts.renderErrorPopup(e.getMessage(), target);
                            return;
                        }
                        target.add(grid);

                        break;
                    case EDIT:
                        editFAQDialog.getDialogPanel().setFaqModel(rowModel);
                        editFAQDialog.show(target);

                        break;
                    case ADD:
                        createQAPanelDialog.detachDialogPanel();
                        createQAPanelDialog.show(target);

                        break;
                    case REMOVE:
                        try {
                            faqManager.delete(rowModel.getObject());
                        } catch (FaqException e) {
                            ELTAlerts.renderErrorPopup(e.getMessage(), target);
                            return;
                        }
                        target.add(grid);
                        ELTAlerts.renderOKPopup(getString("qaDeletedMessage"), target);
                        break;
                }
            }
        };
        form.add(grid.setOutputMarkupId(true));
        form.add(category);
    }

    private Dialog<EditFaqPanel> editFAQDialog = new Dialog<EditFaqPanel>("editFaqDialog",
            UIConstants.DIALOG_POPUP_WIDTH_SMALL) {
        @Override
        public EditFaqPanel createDialogPanel(String id) {
            return new EditFaqPanel(id);
        }

        @Override
        public void registerCallback(EditFaqPanel panel) {
            super.registerCallback(panel);
            panel.setSimpleNewCallback(new IDialogSimpleNewCallback.IDialogActionProcessor<Faq>() {
                @Override
                public void process(IModel<Faq> model, AjaxRequestTarget target) {
                    try {
                        faqManager.update(model.getObject());
                    } catch (FaqException e) {
                        ELTAlerts.renderErrorPopup(e.getMessage(), target);
                        return;
                    }
                    close(target);
                    target.add(grid);
                }
            });
        }
    };

    private class EditFaqPanel extends ELTDialogPanel implements IDialogSimpleNewCallback<Faq> {
        private IDialogActionProcessor<Faq> callback;
        private IModel<Faq> faqModel;
        private ELTTextArea question = new ELTTextArea("question", new ResourceModel("questionLabel"),
                new Model<String>(), true);
        private ELTTextArea answer = new ELTTextArea("answer", new ResourceModel("answerLabel"),
                new Model<String>(), true);

        public EditFaqPanel(String id) {
            super(id);

            form.setMultiPart(true);
            form.add(question);
            form.add(answer);
        }

        public void setFaqModel(IModel<Faq> model) {
            faqModel = model;

            question.setModelObject(faqModel.getObject().getQuestion());
            answer.setModelObject(faqModel.getObject().getAnswer());
        }

        @Override
        protected String getHeader() {
            return getString("header");
        }

        @Override
        protected List<EVENT> getActionList() {
            return Arrays.asList(EVENT.Save);
        }

        @Override
        protected void eventHandler(EVENT event, AjaxRequestTarget target) {
            switch (event) {
                case Save:
                    if (faqModel == null) {
                        return;
                    }

                    faqModel.getObject().setQuestion(question.getModelObject());
                    faqModel.getObject().setAnswer(answer.getModelObject());

                    callback.process(faqModel, target);

                    break;
            }
        }

        @Override
        public void setSimpleNewCallback(IDialogActionProcessor<Faq> callback) {
            this.callback = callback;
        }
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        response.renderCSSReference(ResourcesUtils.CSS_TOOLTIP);
        response.renderCSSReference(ResourcesUtils.CSS_TOOLTUP_BOX);
        response.renderCSSReference(ResourcesUtils.CSS_NEW_TABLE_STYLE);
    }
}
