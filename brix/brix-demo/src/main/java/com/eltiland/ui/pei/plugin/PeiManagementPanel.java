package com.eltiland.ui.pei.plugin;

import com.eltiland.bl.PeiManager;
import com.eltiland.exceptions.PeiException;
import com.eltiland.model.Pei;
import com.eltiland.model.file.File;
import com.eltiland.ui.common.BaseEltilandPanel;
import com.eltiland.ui.common.components.ResourcesUtils;
import com.eltiland.ui.common.components.UIConstants;
import com.eltiland.ui.common.components.avatar.AvatarPreviewPanel;
import com.eltiland.ui.common.components.dialog.Dialog;
import com.eltiland.ui.common.components.dialog.ELTAlerts;
import com.eltiland.ui.common.components.dialog.ELTDialogPanel;
import com.eltiland.ui.common.components.dialog.callback.IDialogSimpleNewCallback;
import com.eltiland.ui.common.components.dialog.callback.IDialogSimpleUpdateCallback;
import com.eltiland.ui.common.components.grid.ELTTable;
import com.eltiland.ui.common.components.grid.GridAction;
import com.eltiland.ui.common.components.textfield.*;
import com.eltiland.ui.common.model.GenericDBModel;
import com.eltiland.utils.UrlUtils;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.PropertyColumn;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.brixcms.workspace.Workspace;

import java.util.*;

/**
 * Panel manages PEI.
 */
public class PeiManagementPanel extends BaseEltilandPanel<Workspace> {
    @SpringBean
    private PeiManager peiManager;

    private ELTTable<Pei> grid;

    private Dialog<EditPeiPanel> createDialog = new Dialog<EditPeiPanel>("createDialog",
            UIConstants.DIALOG_POPUP_WIDTH) {
        @Override
        public EditPeiPanel createDialogPanel(String id) {
            return new EditPeiPanel(id);
        }

        @Override
        public void registerCallback(EditPeiPanel panel) {
            super.registerCallback(panel);
            panel.setSimpleNewCallback(new IDialogSimpleNewCallback.IDialogActionProcessor<Pei>() {
                @Override
                public void process(IModel<Pei> model, AjaxRequestTarget target) {
                    try {
                        peiManager.createPei(model.getObject());
                        ELTAlerts.renderOKPopup(getString("peiCreated"), target);
                    } catch (PeiException e) {
                        ELTAlerts.renderErrorPopup(e.getMessage(), target);
                    }

                    close(target);
                    target.add(grid);
                }
            });
            panel.setSimpleUpdateCallback(new IDialogSimpleUpdateCallback.IDialogActionProcessor<Pei>() {
                @Override
                public void process(IModel<Pei> model, AjaxRequestTarget target) {
                    try {
                        peiManager.updatePei(model.getObject());
                        ELTAlerts.renderOKPopup(getString("peiUpdated"), target);
                    } catch (PeiException e) {
                        ELTAlerts.renderErrorPopup(e.getMessage(), target);
                    }

                    close(target);
                    target.add(grid);
                }
            });
        }
    };

    public PeiManagementPanel(String id, IModel<Workspace> workspaceIModel) {
        super(id, workspaceIModel);

        grid = new ELTTable<Pei>("grid", 10) {
            @Override
            protected boolean isSearching() {
                return true;
            }

            @Override
            protected String getActionTooltip(GridAction action) {
                switch (action) {
                    case EDIT:
                        return getString("editTooltip");
                    case REMOVE:
                        return getString("removeTooltip");
                    default:
                        return "";
                }
            }

            @Override
            protected List<GridAction> getGridActions(IModel<Pei> rowModel) {
                return Arrays.asList(GridAction.EDIT, GridAction.REMOVE);
            }

            @Override
            protected List<IColumn<Pei>> getColumns() {
                List<IColumn<Pei>> columns = new ArrayList<>();

                columns.add(new PropertyColumn<Pei>(new ResourceModel("nameColumn"), "name", "name"));
                columns.add(new PropertyColumn<Pei>(new ResourceModel("addressColumn"), "address", "address"));
                columns.add(new PropertyColumn<Pei>(new ResourceModel("emailColumn"), "email", "email"));
                columns.add(new PropertyColumn<Pei>(new ResourceModel("managerColumn"), "manager", "manager"));
                columns.add(new PropertyColumn<Pei>(new ResourceModel("phoneColumn"), "phone", "phone"));

                return columns;
            }

            @Override
            protected Iterator getIterator(int first, int count) {
                return peiManager.getPeiList(first, count, getSort().getProperty(), getSort().isAscending(),
                        getSearchString()).iterator();
            }

            @Override
            protected int getSize() {
                return peiManager.getPeiListCount(getSearchString());
            }

            @Override
            protected void onClick(IModel<Pei> rowModel, GridAction action, AjaxRequestTarget target) {
                switch (action) {
                    case ADD:
                        createDialog.getDialogPanel().prepare();
                        createDialog.show(target);

                        break;

                    case EDIT:
                        createDialog.getDialogPanel().setItemModel(rowModel);
                        createDialog.show(target);

                        break;

                    case REMOVE:
                        try {
                            peiManager.deletePei(rowModel.getObject());
                        } catch (PeiException e) {
                            ELTAlerts.renderErrorPopup(e.getMessage(), target);
                        }

                        target.add(grid);

                        break;
                }
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
            protected boolean hasConfirmation(GridAction action) {
                switch (action) {
                    case REMOVE:
                        return true;
                    default:
                        return false;
                }
            }
        };

        add(grid);
        add(createDialog);
    }

    private class EditPeiPanel extends ELTDialogPanel implements IDialogSimpleNewCallback<Pei>,
            IDialogSimpleUpdateCallback<Pei> {
        private IModel<Pei> itemModel;
        private IDialogSimpleNewCallback.IDialogActionProcessor<Pei> newCallback;
        private IDialogSimpleUpdateCallback.IDialogActionProcessor<Pei> updateCallback;

        private ELTTextField<String> name = new ELTTextField<String>("name", new ResourceModel("nameLabel"),
                new Model<String>(), String.class, true);
        private ELTTextField<String> address = new ELTTextField<String>("address", new ResourceModel("addressLabel"),
                new Model<String>(), String.class, false);
        private ELTTextEmailField email = new ELTTextEmailField("email", new ResourceModel("emailLabel"),
                new Model<String>(), true);
        private ELTTextField<String> manager = new ELTTextField<String>("manager", new ResourceModel("managerLabel"),
                new Model<String>(), String.class, false);
        private ELTTextField<String> phone = new ELTTextField<String>("phone", new ResourceModel("phoneLabel"),
                new Model<String>(), String.class, false);
        private ELTTextField<String> url = new ELTTextField<String>("url", new ResourceModel("urlLabel"),
                new Model<String>(), String.class, false);
        private ELTTextField<String> groupCount = new ELTTextField<>("groupCount", new ResourceModel("groupCountLabel"),
                new Model<String>(), String.class, false);
        private Label familyLabel = new Label("familyLabel", new ResourceModel("familyLabel"));
        private CheckBox family = new CheckBox("family", new Model<Boolean>());
        private Label consultationLabel = new Label("consultationLabel", new ResourceModel("consultationLabel"));
        private CheckBox consultation = new CheckBox("consultation", new Model<Boolean>());
        private AvatarPreviewPanel avatar = new AvatarPreviewPanel("avatar", UrlUtils.StandardIcons.ICONS_DEFAULT_PEI);


        public EditPeiPanel(String id) {
            super(id);

            form.add(avatar);
            form.add(name);
            form.add(address);
            form.add(email);
            form.add(manager);
            form.add(phone.add(UIConstants.phoneNumberValidator));
            form.add(url);
            form.add(groupCount.add(UIConstants.integerValidator));
            form.add(family);
            form.add(consultation);
            form.add(familyLabel);
            form.add(consultationLabel);
        }

        public void prepare() {
            itemModel = null;
            name.setModelObject("");
            address.setModelObject("");
            email.setModelObject("");
            manager.setModelObject("");
            phone.setModelObject("");
            url.setModelObject("");
            groupCount.setModelObject("");
            family.setModelObject(false);
            consultation.setModelObject(false);
        }

        public void setItemModel(IModel<Pei> model) {
            itemModel = model;
            Pei item = itemModel.getObject();

            name.setModelObject(item.getName());
            address.setModelObject(item.getAddress());
            email.setModelObject(item.getEmail());
            manager.setModelObject(item.getManager());
            phone.setModelObject(item.getPhone());
            url.setModelObject(item.getWebsite());
            groupCount.setModelObject(item.getGroupCount());
            family.setModelObject(item.getFamilyPresent());
            consultation.setModelObject(item.getConsultationPresent());

            if (item.getAvatar() != null) {
                avatar.initEditMode(new GenericDBModel<>(File.class, item.getAvatar()));
            }
        }

        @Override
        protected String getHeader() {
            return itemModel == null ? PeiManagementPanel.this.getString("addPeiPanelHeader")
                    : PeiManagementPanel.this.getString("editPeiPanelHeader");
        }

        @Override
        protected List<EVENT> getActionList() {
            return Arrays.asList(EVENT.Save, EVENT.Add);
        }

        @Override
        protected boolean actionSelector(EVENT event) {
            switch (event) {
                case Save:
                    return itemModel != null;
                case Add:
                    return itemModel == null;
                default:
                    return false;
            }
        }

        private void setItemData(Pei item) {
            item.setName(name.getModelObject());
            item.setAddress(address.getModelObject());
            item.setEmail(email.getModelObject());
            item.setManager(manager.getModelObject());
            item.setPhone(phone.getModelObject());
            item.setWebsite(url.getModelObject());
            item.setGroupCount(groupCount.getModelObject());
            item.setFamilyPresent(family.getModelObject());
            item.setConsultationPresent(consultation.getModelObject());
            item.setAvatar(avatar.getAvatarFile());
        }

        @Override
        protected void eventHandler(EVENT event, AjaxRequestTarget target) {
            Pei item;
            switch (event) {
                case Save:
                    item = itemModel.getObject();
                    setItemData(item);
                    updateCallback.process(itemModel, target);

                    break;

                case Add:
                    item = new Pei();
                    itemModel = new GenericDBModel<>(Pei.class, item);
                    setItemData(item);
                    newCallback.process(itemModel, target);

                    break;
            }
        }

        @Override
        public void setSimpleNewCallback(IDialogSimpleNewCallback.IDialogActionProcessor<Pei> callback) {
            this.newCallback = callback;
        }

        @Override
        public void setSimpleUpdateCallback(IDialogSimpleUpdateCallback.IDialogActionProcessor<Pei> callback) {
            this.updateCallback = callback;
        }
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
        response.renderCSSReference(ResourcesUtils.CSS_NEW_TABLE_STYLE);
        response.renderCSSReference(ResourcesUtils.CSS_PEI_STYLE);
    }

}
