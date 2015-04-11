package com.eltiland.ui.course.plugin.tab.test;

import com.eltiland.bl.GenericManager;
import com.eltiland.bl.test.TestAttemptManager;
import com.eltiland.exceptions.ConstraintException;
import com.eltiland.model.course.test.UserTestAttempt;
import com.eltiland.ui.common.BaseEltilandPanel;
import com.eltiland.ui.common.components.ResourcesUtils;
import com.eltiland.ui.common.components.dialog.Dialog;
import com.eltiland.ui.common.components.dialog.ELTAlerts;
import com.eltiland.ui.common.components.dialog.callback.IDialogSimpleNewCallback;
import com.eltiland.ui.common.components.grid.GridAction;
import com.eltiland.ui.common.model.GenericDBModel;
import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.model.IModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.brixcms.workspace.Workspace;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * Panel for output limited attempts.
 *
 * @author Aleksey Plotnikov
 */
public class AttemptLimitManagementPanel extends BaseEltilandPanel<Workspace> {

    private static final Logger LOGGER = LoggerFactory.getLogger(AttemptLimitManagementPanel.class);

    @SpringBean
    private GenericManager genericManager;
    @SpringBean
    private TestAttemptManager testAttemptManager;

    private AttemptDataGrid grid;

    private IModel<UserTestAttempt> attemptModel = new GenericDBModel<>(UserTestAttempt.class);

    private Dialog<AddAttemptsPanel> attemptsPanelDialog = new Dialog<AddAttemptsPanel>("addDialog", 320) {
        @Override
        public AddAttemptsPanel createDialogPanel(String id) {
            return new AddAttemptsPanel(id);
        }

        @Override
        public void registerCallback(AddAttemptsPanel panel) {
            super.registerCallback(panel);
            panel.setSimpleNewCallback(new IDialogSimpleNewCallback.IDialogActionProcessor<Integer>() {
                @Override
                public void process(IModel<Integer> model, AjaxRequestTarget target) {
                    UserTestAttempt attempt = attemptModel.getObject();
                    attempt.setAttemptLimit(attempt.getAttemptLimit() + model.getObject());

                    try {
                        genericManager.update(attempt);
                    } catch (ConstraintException e) {
                        LOGGER.error("Cannot update attempt entity", e);
                        throw new WicketRuntimeException("Cannot update attempt entity", e);
                    }

                    close(target);
                    target.add(grid);
                    ELTAlerts.renderOKPopup(getString("attemptsAdded"), target);
                }
            });
        }
    };

    public AttemptLimitManagementPanel(String id, IModel<Workspace> workspaceIModel) {
        super(id, workspaceIModel);

        grid = new AttemptDataGrid("grid", 20) {
            @Override
            protected Iterator getIterator(int first, int count) {
                return testAttemptManager.getLimitList(
                        first, count, getSort().getProperty(), getSort().isAscending(), getSearchString()).iterator();
            }

            @Override
            protected int getSize() {
                return testAttemptManager.getLimitCount(null);
            }

            @Override
            protected List<GridAction> getGridActions(IModel<UserTestAttempt> rowModel) {
                return new ArrayList<>(Arrays.asList(GridAction.ADD));
            }

            @Override
            protected String getActionTooltip(GridAction action) {
                return getString("addAttempts");
            }

            @Override
            protected void onClick(IModel<UserTestAttempt> rowModel, GridAction action, AjaxRequestTarget target) {
                super.onClick(rowModel, action, target);
                attemptModel.setObject(rowModel.getObject());
                attemptsPanelDialog.show(target);
            }
        };
        add(grid.setOutputMarkupId(true));
        add(attemptsPanelDialog);
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
        response.renderCSSReference(ResourcesUtils.CSS_NEW_TABLE_STYLE);
    }
}
