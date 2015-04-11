package com.eltiland.ui.common.components.dialog.selector;

import com.eltiland.model.Identifiable;
import com.eltiland.ui.common.components.button.EltiAjaxLink;
import com.eltiland.ui.common.components.datatable.EltiDataTable;
import com.eltiland.ui.common.components.dialog.Dialog;
import com.eltiland.ui.common.components.dialog.callback.IDialogSelectCallback;
import com.eltiland.ui.common.components.dialog.selector.search.SearchPanel;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.ISortableDataProvider;
import org.apache.wicket.markup.html.WebComponent;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Radio;
import org.apache.wicket.markup.html.panel.Fragment;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;

import java.util.Collections;
import java.util.List;

/**
 * Base class for selector panels.
 * <ol>
 * <li>Implement {@link #getDataProvider()}</li>
 * <li>Implement {@link #getColumns()}</li>
 * <li>Implement {@link #createAddNewDialog(String, org.apache.wicket.model.IModel)}</li>
 * <li>If need extended search, override {@link #getSearchSelectorKeys()}</li>
 * </ol>
 * To get search request use {@link #getCurrentSearchPattern()} and {@link #getCurrentSelectorKey()}}.
 * For integrate add new dialog implement {@link #createAddNewDialog(String, org.apache.wicket.model.IModel)},
 * otherwise it should return null.<p/>
 * Some times, create new dialog should know parent entity (like contact for contact persons). In this case,
 * create selector panel using special constructor {@link #SelectorPanel(String, org.apache.wicket.model.IModel)}.
 * Otherwise, parent model will be null.<p/>
 * SelectorPanel can be extended, extend markup supported by <wicket:child/>.
 * <p/>
 * By default the 'Add new' button behavior is controlled in a follwoing way:
 * <ul>
 * <li>There is a flag which can hide it for all the cases, passed in ctor.</li>
 * <li>If constructor wants the button, we only show it when a) the search has been made OR b) there are less than a
 * half-page of the results. This is done in order for user to search first and then only add new entities.</li>
 * <li>There is a way to override this behavior by overriding isAddNewButtonAlwaysVisibleOverride method. This would force
 * button to show always.</li>
 * </ul>
 *
 * @param <T> type of operated entity
 * @author Ihor Cherednichenko
 * @version 1.1
 */
public abstract class SelectorPanel<T extends Identifiable> extends Panel
        implements IDialogSelectCallback<T> {
    private static final String ADD_NEW_DIALOG_ID = "addNewDialog";
    private static final String DATA_TABLE_ID = "dataTable";

    private IModel<?> parentModel;
    private Model<String> searchPatternModel = new Model<String>();
    private Model<String> selectorKeyModel = new Model<String>();

    @Override
    protected void onDetach() {
        if (parentModel != null) {
            parentModel.detach();
        }
        searchPatternModel.detach();
        selectorKeyModel.detach();

        super.onDetach();
    }


    private ExtendedSearchPanel searchPanel;
    private EltiDataTable<T> table;

    private boolean isAddNewButtonEnable = false;

    protected Dialog getAddNewDialog() {
        return addNewDialog;
    }

    private Dialog addNewDialog;

    private EltiAjaxLink<T> addNewButton = new EltiAjaxLink<T>("addNewButton") {

        {
            setBody(getAddButtonCaption());
        }

        @Override
        public void onClick(AjaxRequestTarget target) {
            onAddNewButtonClicked(target);
        }

        @Override
        protected void onConfigure() {
            super.onConfigure();
            // forcing to 'show' if add new button is true-overridden
            if (isAddNewButtonAlwaysVisibleOverride()) {
                setVisible(true);
            } else {
                setVisible(isAddNewButtonEnable);
            }
        }
    };

    /**
     * Called when the 'add new' button is clicked.
     *
     * @param target target to use when showing dialog
     */
    protected void onAddNewButtonClicked(AjaxRequestTarget target) {
        addNewDialog.show(target);
    }

    /**
     * Gets caption for the 'Add new button'. May return null for default 'Add' caption.
     *
     * @return model with the caption for the Add new button. Alternatively, null for default.
     */
    protected IModel<String> getAddButtonCaption() {
        return null;
    }

    /**
     * Default panel constructor.
     *
     * @param id wicket component id.
     */
    public SelectorPanel(String id) {
        this(id, true);
    }

    /**
     * Model based panel constructor.
     *
     * @param id          wicket component id.
     * @param parentModel parent object model
     */
    public SelectorPanel(String id, IModel<?> parentModel) {
        this(id, true);
        this.parentModel = parentModel;
    }

    /**
     * Construct panel with support actions flag.
     *
     * @param id           wicket component id
     * @param enableAddNew flag for "add new" action
     */
    public SelectorPanel(String id, Boolean enableAddNew) {
        super(id);
        isAddNewButtonEnable = enableAddNew;

        // all the real initialization is happening in onInitialize in order to decouple construction from the virtual
        // methods. These WebComponents will be replaced by real parts later on.
        add(new WebComponent("searchForm"));
        add(new WebComponent(getTableId()));

        add(addNewButton.setOutputMarkupId(true));
    }

    /**
     * Override this for change default table. For table construction use {@link #getTableId}.<p/>
     * This method should return existed data table (down create table instance inside method)!
     *
     * @return existed data table.
     */
    protected EltiDataTable<T> getTable() {
        return table;
    }

    /**
     * @return data table component id
     */
    protected final String getTableId() {
        return DATA_TABLE_ID;
    }

    /**
     * Reset search pattern to default value.
     */
    public final void resetSearchPattern() {
        searchPatternModel.setObject(null);
        selectorKeyModel.setObject(null);
        searchPanel.resetSearchPattern();
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        addNewDialog = createAddNewDialog(ADD_NEW_DIALOG_ID, parentModel);
        if (addNewDialog == null) {
            //if dialog not implemented, hide button "addNewButton" and create empty dialog
            isAddNewButtonEnable = false;
            addNewDialog = new SelectorDialog(ADD_NEW_DIALOG_ID) {
                @Override
                public Panel createDialogPanel(String id) {
                    return null;
                }

                @Override
                public void onSelect(IModel iModel, AjaxRequestTarget target) {
                }
            };
        }

        table = new EltiDataTable<T>(getTableId(), getColumns(), getDataProvider());
        addOrReplace(getTable().setOutputMarkupId(true));

        searchPanel = new ExtendedSearchPanel("searchForm") {
            @Override
            protected void onInitialize() {
                super.onInitialize();
            }

            @Override
            public Panel createAddNewDialog() {
                return SelectorPanel.this.addNewDialog;
            }

            /* @Override
            public List<String> getSearchSelectorKeys() {
                return SelectorPanel.this.getSearchSelectorKeys();
            }*/
        };
        addOrReplace(searchPanel.setOutputMarkupId(true));

    }

    /**
     * @return Return current search string. Can be null.
     */
    public String getCurrentSearchPattern() {
        return searchPatternModel.getObject();
    }

    /**
     * @return Return current search selector key.
     *         If {@link #getSearchSelectorKeys()} is empty collection, return null.
     */
    public String getCurrentSelectorKey() {
        if (getSearchSelectorKeys().size() != 0 && selectorKeyModel.getObject() == null) {
            selectorKeyModel.setObject(getSearchSelectorKeys().get(0));
        }

        return selectorKeyModel.getObject();
    }

    /**
     * Implement this method to get real data for data table.
     * PoseidonDataProviderBase} as template data provider.<p/>
     * To handle user request ("search by" mechanism) use {@link #getCurrentSearchPattern()}
     * and {@link #getCurrentSelectorKey()}}.
     *
     * @return Sortable data provider for PoseidonDataTablePanel.
     */
    public abstract ISortableDataProvider<T> getDataProvider();

    /**
     * Implement this method for construct real view for data table.<p/>
     *
     * @return Columns array for PoseidonDataTablePanel.
     */
    public abstract List<IColumn<T>> getColumns();

    /**
     * Implement this method for get create "add new" dialog.
     * Just return null in case there is no Add New dialog in plans.
     *
     * @param id          wicket component id
     * @param parentModel parent object model, if not used can be null
     * @return new dialog
     */
    public abstract Dialog createAddNewDialog(String id, IModel parentModel);

    /**
     * Override this method for construct extended "search by" mechanism.<p/>
     * Elements of list (selector key) used as current value of selector {@link #getCurrentSelectorKey()} and as
     * resource key for associated radio label.<p/>
     * <p/>
     * BECAUSE THIS METHOD CALLED IN SUPER CONSTRUCTOR, LIST SHOULD BE DECLARED AS STATIC.
     *
     * @return List of selector keys.
     */
    public List<String> getSearchSelectorKeys() {
        return Collections.emptyList();
    }

    /**
     * Markup fragment for represent radio input with label.
     */
    private class RadioItem extends Fragment {
        public RadioItem(String id, IModel<String> model) {
            super(id, "radioItemFragment", SelectorPanel.this, model);

            Radio radio = new Radio<String>("searchBy", new Model<String>() {
                public String getObject() {
                    return (String) RadioItem.this.getDefaultModelObject();
                }
            });
            add(radio.setOutputMarkupId(true));

            Label label = new Label("searchName", new ResourceModel(model.getObject()));
            label.add(new AttributeModifier("for", new Model<String>(radio.getMarkupId())));
            add(label.setOutputMarkupId(true));

            setRenderBodyOnly(true);
        }
    }

    /**
     * Action processors.
     */
    protected IDialogSelectCallback.IDialogActionProcessor<T> onSelectCallback;

    /**
     * {@inheritDoc}
     */
    public void setSelectCallback(IDialogSelectCallback.IDialogActionProcessor<T> callback) {
        this.onSelectCallback = callback;
    }

    /**
     * Allows you to override default behavior and make 'Add new button' showing alwyas not depending on the count of
     * the search results returned and whether the search has been made at all.
     * <p/>
     * See class javadoc for details.
     *
     * @return whether to override-true add new button visibility.
     */
    public boolean isAddNewButtonAlwaysVisibleOverride() {
        return false;
    }

    protected EltiAjaxLink<T> getAddNewLink() {
        return addNewButton;
    }

    public IModel<?> getParentModel() {
        return parentModel;
    }

    /**
     * Extend SearchPanel for place addNewDialog. for internal use.
     */
    private abstract class ExtendedSearchPanel extends SearchPanel {

        /**
         * Default constructor.
         *
         * @param id wicket component id
         */
        public ExtendedSearchPanel(String id) {
            super(id);
            searchForm.add(createAddNewDialog());
        }

        public abstract Panel createAddNewDialog();
    }
}
