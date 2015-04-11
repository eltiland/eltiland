package com.eltiland.ui.library.admin;

import com.eltiland.model.library.LibraryCollection;
import com.eltiland.ui.common.components.dialog.ELTDialogPanel;
import com.eltiland.ui.common.components.dialog.callback.IDialogNewCallback;
import com.eltiland.ui.common.components.dialog.callback.IDialogUpdateCallback;
import com.eltiland.ui.common.components.textfield.ELTTextArea;
import com.eltiland.ui.common.components.textfield.ELTTextField;
import com.eltiland.ui.common.model.GenericDBModel;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Record panel.
 *
 * @author Aleksey Plotnikov.
 */
public class CollectionPropertyPanel extends ELTDialogPanel
        implements IDialogNewCallback<LibraryCollection>, IDialogUpdateCallback<LibraryCollection> {

    private boolean createMode;

    private IDialogNewCallback.IDialogActionProcessor<LibraryCollection> createCallback;
    private IDialogUpdateCallback.IDialogActionProcessor<LibraryCollection> updateCallback;

    private IModel<LibraryCollection> libraryCollectionIModel = new GenericDBModel<>(LibraryCollection.class);

    private ELTTextField<String> nameField = new ELTTextField<>(
            "name", new ResourceModel("name"), new Model<String>(), String.class, true);

    private ELTTextArea descriptionField = new ELTTextArea(
            "description", new ResourceModel("description"), new Model<String>());

    public CollectionPropertyPanel(String id) {
        super(id);
        form.add(nameField);
        form.add(descriptionField);
    }

    public void initCreateMode() {
        createMode = true;
        nameField.setModelObject(null);
        descriptionField.setModelObject(null);
    }

    public void initEditMode(IModel<LibraryCollection> libraryCollectionIModel) {
        createMode = false;
        nameField.setModelObject(libraryCollectionIModel.getObject().getName());
        descriptionField.setModelObject(libraryCollectionIModel.getObject().getDescription());
        this.libraryCollectionIModel.setObject(libraryCollectionIModel.getObject());
    }

    @Override
    protected String getHeader() {
        return getString(createMode ? "newHeader" : "editHeader");
    }

    @Override
    protected List<EVENT> getActionList() {
        return new ArrayList<>(Arrays.asList(EVENT.Create, EVENT.Save));
    }

    @Override
    protected void eventHandler(EVENT event, AjaxRequestTarget target) {
        if (event.equals(EVENT.Create)) {
            LibraryCollection collection = new LibraryCollection();
            collection.setName(nameField.getModelObject());
            collection.setDescription(descriptionField.getModelObject());
            createCallback.process(new GenericDBModel<>(LibraryCollection.class, collection), target);
        } else if (event.equals(EVENT.Save)) {
            LibraryCollection collection = libraryCollectionIModel.getObject();
            collection.setName(nameField.getModelObject());
            collection.setDescription(descriptionField.getModelObject());
            updateCallback.process(new GenericDBModel<>(LibraryCollection.class, collection), target);
        }
    }

    @Override
    protected boolean actionSelector(EVENT event) {
        return event.equals(EVENT.Create) && createMode || event.equals(EVENT.Save) && !createMode;
    }

    @Override
    public void setUpdateCallback(IDialogUpdateCallback.IDialogActionProcessor<LibraryCollection> callback) {
        updateCallback = callback;
    }

    @Override
    public void setNewCallback(IDialogNewCallback.IDialogActionProcessor<LibraryCollection> callback) {
        createCallback = callback;
    }
}
