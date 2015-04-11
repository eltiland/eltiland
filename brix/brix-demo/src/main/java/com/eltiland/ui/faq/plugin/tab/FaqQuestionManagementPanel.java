package com.eltiland.ui.faq.plugin.tab;

import com.eltiland.bl.FaqApprovalManager;
import com.eltiland.bl.FaqManager;
import com.eltiland.exceptions.FaqException;
import com.eltiland.model.faq.Faq;
import com.eltiland.model.faq.FaqApproval;
import com.eltiland.model.faq.FaqCategory;
import com.eltiland.ui.common.BaseEltilandPanel;
import com.eltiland.ui.common.components.ReadonlyObjects;
import com.eltiland.ui.common.components.ResourcesUtils;
import com.eltiland.ui.common.components.UIConstants;
import com.eltiland.ui.common.components.button.EltiAjaxLink;
import com.eltiland.ui.common.components.dialog.Dialog;
import com.eltiland.ui.common.components.dialog.ELTAlerts;
import com.eltiland.ui.common.components.dialog.ELTDialogPanel;
import com.eltiland.ui.common.components.dialog.callback.IDialogConfirmCallback;
import com.eltiland.ui.common.components.grid.ELTTable;
import com.eltiland.ui.common.components.grid.GridAction;
import com.eltiland.ui.faq.components.FaqCategoryList;
import com.eltiland.ui.faq.plugin.components.AnswerQuestionPanel;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.table.AbstractColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.PropertyColumn;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.brixcms.workspace.Workspace;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * FAQ question management panel.
 */
public class FaqQuestionManagementPanel extends BaseEltilandPanel<Workspace> {
    @SpringBean
    private FaqApprovalManager faqApprovalManager;

    @SpringBean
    private FaqManager faqManager;

    private final ELTTable<FaqApproval> grid;

    private Dialog<AnswerQuestionPanel> answerQuestionPanelDialog = new Dialog<AnswerQuestionPanel>(
            "answerQuestionDialog", UIConstants.DIALOG_POPUP_WIDTH_SMALL) {
        @Override
        public AnswerQuestionPanel createDialogPanel(String id) {
            return new AnswerQuestionPanel(id);
        }

        @Override
        public void registerCallback(AnswerQuestionPanel panel) {
            super.registerCallback(panel);
            panel.setConfirmCallback(new IDialogConfirmCallback.IDialogActionProcessor() {
                @Override
                public void process(AjaxRequestTarget target) {
                    close(target);
                    target.add(grid);
                    ELTAlerts.renderOKPopup(getString("questionAddedMessage"), target);
                }
            });
        }
    };

    private Dialog<SelectCategoryPanel> addToFaqDialog = new Dialog<SelectCategoryPanel>("addToFaqDialog",
            UIConstants.DIALOG_POPUP_WIDTH_SMALL) {

        @Override
        public SelectCategoryPanel createDialogPanel(String id) {
            return new SelectCategoryPanel(id);
        }
    };

    /**
     * Panel constructor.
     *
     * @param id              panel's ID.
     * @param workspaceIModel workspace model.
     */
    public FaqQuestionManagementPanel(String id, IModel<Workspace> workspaceIModel) {
        super(id, workspaceIModel);

        grid = new ELTTable<FaqApproval>("grid", 10) {
            @Override
            protected boolean isSearching() {
                return true;
            }

            @Override
            protected String getActionTooltip(GridAction action) {
                switch (action) {
                    case REMOVE:
                        return getString("deleteTooltip");
                    default:
                        return "";
                }
            }
            @Override
            protected List<GridAction> getGridActions(IModel<FaqApproval> rowModel) {
                return Arrays.asList(GridAction.REMOVE);
            }

            @Override
            protected List<IColumn<FaqApproval>> getColumns() {
                List<IColumn<FaqApproval>> columns = new ArrayList<>();

                columns.add(new PropertyColumn<FaqApproval>(new ResourceModel("dateColumnLabel"), "creationDate",
                        "creationDate"));
                columns.add(new PropertyColumn<FaqApproval>(new ResourceModel("questionColumnLabel"), "question",
                        "question"));
                columns.add(new AbstractColumn<FaqApproval>(ReadonlyObjects.EMPTY_DISPLAY_MODEL) {
                  @Override
                    public void populateItem(Item<ICellPopulator<FaqApproval>> components, String s,
                                             final IModel<FaqApproval> faqApprovalIModel) {
                        components.add(new ApprovePanel(s, faqApprovalIModel) {
                            @Override
                            public void onApprove(AjaxRequestTarget target) {
                                answerQuestionPanelDialog.detachDialogPanel();
                                answerQuestionPanelDialog.getDialogPanel().initQuestion(faqApprovalIModel.getObject());
                                answerQuestionPanelDialog.show(target);
                            }
                        });
                    }
                });
                columns.add(new AbstractColumn<FaqApproval>(ReadonlyObjects.EMPTY_DISPLAY_MODEL) {
                    @Override
                    public void populateItem(Item<ICellPopulator<FaqApproval>> components, String s,
                                             final IModel<FaqApproval> faqApprovalIModel) {
                        components.add(new AddToFaqPanel(s, faqApprovalIModel) {
                            @Override
                            public void onLinkClick(AjaxRequestTarget target) {
                                // answer is empty
                                if (faqApprovalIModel.getObject().getAnswer() == null) {
                                    ELTAlerts.renderErrorPopup(getString("emptyAnswer"), target);
                                    return;
                                }

                                if (faqApprovalIModel.getObject().getAnswer().isEmpty()) {
                                    ELTAlerts.renderErrorPopup(getString("emptyAnswer"), target);
                                    return;
                                }

                                // faq already exists
                                if (faqApprovalManager.isExists(faqApprovalIModel.getObject())) {
                                    ELTAlerts.renderErrorPopup(getString("faqApprovalAlreadyExists"), target);
                                    return;
                                }

                                addToFaqDialog.getDialogPanel().setDialog(addToFaqDialog);
                                addToFaqDialog.getDialogPanel().setApprovalModel(faqApprovalIModel);
                                addToFaqDialog.show(target);
                            }
                        });
                    }
                });

                return columns;
            }

            @Override
            protected Iterator getIterator(int first, int count) {
                return faqApprovalManager.getFaqApprovalList(first, count, getSort().getProperty(),
                        getSort().isAscending(), getSearchString()).iterator();
            }

            @Override
            protected int getSize() {
                return faqApprovalManager.getFaqApprovalCount(getSearchString());
            }

            @Override
            protected void onClick(IModel<FaqApproval> rowModel, GridAction action, AjaxRequestTarget target) {
                switch (action) {
                    case REMOVE:
                        try {
                            faqApprovalManager.delete(rowModel.getObject());
                        } catch (FaqException e) {
                            ELTAlerts.renderErrorPopup(e.getMessage(), target);
                            return;
                        }
                        target.add(grid);
                        break;
                }
            }
        };

        add(grid.setOutputMarkupId(true));
        add(addToFaqDialog);
        add(answerQuestionPanelDialog);
    }

    private class SelectCategoryPanel extends ELTDialogPanel {
        private IModel<FaqApproval> approvalModel;
        private Dialog dialog;

        private FaqCategoryList list = new FaqCategoryList("category") {
            @Override
            public void onCategoryUpdate(AjaxRequestTarget target, IModel<FaqCategory> model) {
                target.add(list);
            }
        };

        public SelectCategoryPanel(String id) {
            super(id);
            form.add(list.setOutputMarkupId(true));
        }

        @Override
        protected String getHeader() {
            return getString("addToFaqHeader");
        }

        @Override
        protected List<EVENT> getActionList() {
            return Arrays.asList(EVENT.Save);
        }

        @Override
        protected void eventHandler(EVENT event, AjaxRequestTarget target) {
            switch (event) {
                case Save:
                    if ((approvalModel == null) || (list.getCategoryModel() == null)) {
                        return;
                    }

                    FaqApproval approval = approvalModel.getObject();
                    Faq faq = new Faq();
                    faq.setCategory(list.getCategoryModel().getObject());
                    faq.setAnswer(approval.getAnswer());
                    faq.setQuestion(approval.getQuestion());
                    try {
                        faqManager.create(faq);

                        ELTAlerts.renderOKPopup(getString("addedToFaq"), target);
                        dialog.close(target);
                    } catch (FaqException e) {
                        ELTAlerts.renderErrorPopup(e.getMessage(), target);
                    }

                    break;
            }
        }

        public void setApprovalModel(IModel<FaqApproval> model) {
            approvalModel = model;
        }

        public void setDialog(Dialog dialog) {
            this.dialog = dialog;
        }
    }

    private abstract class ApprovePanel extends BaseEltilandPanel {

        public ApprovePanel(String id, IModel<FaqApproval> model) {
            super(id);
            boolean isAnswered = model.getObject().isAnswered();
            Label label = new Label("answered", new ResourceModel("questionAnsweredLabel"));
            add(label);
            EltiAjaxLink link = new EltiAjaxLink("approveLink") {
                @Override
                public void onClick(AjaxRequestTarget target) {
                    onApprove(target);
                }
            };
            add(link);
            label.setVisible(isAnswered);
            link.setVisible(!isAnswered);
        }

        public abstract void onApprove(AjaxRequestTarget target);
    }

    private abstract class AddToFaqPanel extends BaseEltilandPanel {

        public AddToFaqPanel(String id, IModel<FaqApproval> model) {
            super(id);

            EltiAjaxLink link = new EltiAjaxLink("addToFaqLink") {
                @Override
                public void onClick(AjaxRequestTarget target) {
                    onLinkClick(target);
                }
            };

            add(link);
        }

        public abstract void onLinkClick(AjaxRequestTarget target);
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        response.renderCSSReference(ResourcesUtils.CSS_TOOLTIP);
        response.renderCSSReference(ResourcesUtils.CSS_TOOLTUP_BOX);
        response.renderCSSReference(ResourcesUtils.CSS_NEW_TABLE_STYLE);
    }
}
