package com.eltiland.ui.common.components.label.multiselect;

import com.eltiland.model.Identifiable;
import com.eltiland.ui.common.components.UIConstants;
import com.eltiland.ui.common.components.button.EltiAjaxLink;
import com.eltiland.ui.common.components.dialog.Dialog;
import com.eltiland.ui.common.components.dialog.callback.IDialogSelectCallback;
import com.eltiland.ui.common.components.feedbacklabel.ELTFeedbackLabel;
import com.eltiland.ui.common.model.GenericDBModel;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.ISortableDataProvider;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.FormComponentPanel;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.EmptyPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.util.string.Strings;
import org.hibernate.Hibernate;

import java.util.List;

/**
 * @author knorr
 * @version 1.0
 * @since 8/9/12
 */
public abstract class AjaxMultiSelector<T extends Identifiable> extends FormComponentPanel<List<T>> {

    private boolean isReadOnly = false;

    private ListView<T> chosenElementsListView;

    private Dialog<GenericSelectorPanel<T>> selectorDialog;

    public AjaxMultiSelector(String id, IModel<String> headerModel,
                             IModel<List<T>> chosenObjectsListModel,
                             final IModel<String> panelHeaderModel) {
        this(id, headerModel, chosenObjectsListModel, panelHeaderModel, false);
    }

    public AjaxMultiSelector(String id, IModel<String> headerModel,
                             IModel<List<T>> chosenObjectsListModel,
                             final IModel<String> panelHeaderModel,
                             final boolean isSingleComponent) {
        super(id, chosenObjectsListModel);
        setOutputMarkupId(true);
        add(AttributeAppender.append("class", Strings.join(" ",
                UIConstants.CLASS_MULTI_SELECTOR,
                UIConstants.CLASS_EDITABLE_LABEL)));
        add(new ELTFeedbackLabel("titleLabel", headerModel, this));
        EltiAjaxLink editButton = new EltiAjaxLink("editButton") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                selectorDialog.show(target);
            }

            @Override
            protected void onConfigure() {
                super.onConfigure();
                setVisible(!isReadOnly());
            }
        };
        add(editButton);
        editButton.add(new Label("label", getSelectLinkText()));

        //Add selector dialog
        add(selectorDialog = new Dialog<GenericSelectorPanel<T>>("searchDialog", UIConstants.DIALOG_MEDIUM_WIDTH) {
            @Override
            public GenericSelectorPanel<T> createDialogPanel(String id) {
                return new GenericSelectorPanel<T>(id, panelHeaderModel) {
                    @Override
                    protected ISortableDataProvider<T> createDataProvider() {
                        return AjaxMultiSelector.this.createDataProvider();
                    }

                    @Override
                    protected List<IColumn<T>> createColumns() {
                        return AjaxMultiSelector.this.createColumns();
                    }
                };
            }

            @Override
            public void registerCallback(GenericSelectorPanel<T> panel) {
                super.registerCallback(panel);

                panel.setSelectCallback(new IDialogSelectCallback.IDialogActionProcessor<T>() {
                    @Override
                    public void process(IModel<T> model, AjaxRequestTarget target) {
                        if (isSingleComponent) {
                            AjaxMultiSelector.this.getModelObject().clear();
                        }
                        AjaxMultiSelector.this.getModelObject().add(model.getObject());

                        target.add(AjaxMultiSelector.this);
                        close(target);
                    }
                });
            }
        });
        //Populate chosen elements.
        chosenElementsListView = new ListView<T>("chosenElementsListView", chosenObjectsListModel) {

            @Override
            protected void populateItem(ListItem<T> item) {
                T element = item.getModelObject();
                //Wrapper panel consist of the user custom selected item panel and disable cross link.
                MultiSelectorChoicePanel<T> repeaterPanel = new MultiSelectorChoicePanel<T>(
                        "chosenElementsListViewElement",
                        new GenericDBModel<T>((Class<T>) Hibernate.getClass(element), element.getId()),
                        getChosenElementComponent(MultiSelectorChoicePanel.getInnerPanelMarkupId(), element)) {
                    @Override
                    protected void onConfigure() {
                        super.onConfigure();
                        setReadOnly(AjaxMultiSelector.this.isReadOnly());
                    }
                };
                //Set select callback
                repeaterPanel.setSelectCallback(new IDialogSelectCallback.IDialogActionProcessor<T>() {

                    @Override
                    public void process(IModel<T> model, AjaxRequestTarget target) {
                        T elementToRemove = model.getObject();
                        final Long id = elementToRemove.getId();
                        List<T> selectedObjects = AjaxMultiSelector.this.getModelObject();
                        CollectionUtils.<T>filter(selectedObjects, new Predicate() {
                            @Override
                            public boolean evaluate(Object object) {
                                return !((T) object).getId().equals(id);
                            }
                        });
                        AjaxMultiSelector.this.setDefaultModelObject(selectedObjects);
                        target.add(AjaxMultiSelector.this);
                    }
                });
                item.add(repeaterPanel);

                item.add(getAdditionalPanel("additionalPanel", item));
            }
        };
        add(chosenElementsListView.setOutputMarkupId(true));
    }


    @Override
    protected void convertInput() {
        setConvertedInput(getModelObject());
    }

    @Override
    public boolean checkRequired() {
        return !isRequired() || !chosenElementsListView.getModelObject().isEmpty();
    }

    /**
     * IMPLEMENT this method to customize view of the chosen element.
     *
     * @param id      markup id
     * @param element chosen element
     * @return Component for displaying chosen element, it can be just "Label" or custom panel!
     */
    public abstract Component getChosenElementComponent(String id, T element);

    /**
     * Implement this method for construct real view for data table.
     *
     * @return Columns array.
     */
    protected abstract List<IColumn<T>> createColumns();

    /**
     * Implement this method to get real data for data table.
     * EltiDataProviderBase} as template data provider.<p/>
     *
     * @return Sortable data provider
     */
    protected abstract ISortableDataProvider<T> createDataProvider();

    /**
     * Return search query text.
     *
     * @return search Query.
     */
    protected IModel<String> getSearchQueryModel() {
        return selectorDialog.getDialogPanel().getSearchQueryModel();
    }

    public void setReadOnly(boolean readOnly) {
        isReadOnly = readOnly;
    }

    public boolean isReadOnly() {
        return isReadOnly;
    }

    protected Panel getAdditionalPanel(String id, ListItem<T> item) {
        return new EmptyPanel(id);
    }

    protected String getSelectLinkText() {
        return getString("addNew");
    }

}