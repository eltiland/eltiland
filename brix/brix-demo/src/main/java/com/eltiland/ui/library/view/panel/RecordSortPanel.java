package com.eltiland.ui.library.view.panel;

import com.eltiland.ui.common.BaseEltilandPanel;
import com.eltiland.ui.common.components.ReadonlyObjects;
import com.eltiland.ui.common.components.button.EltiAjaxSubmitLink;
import com.eltiland.ui.common.components.select.ELTSelectField;
import com.eltiland.ui.library.LibraryView;
import com.eltiland.ui.library.SearchData;
import com.eltiland.ui.library.components.switchview.SwitchViewPanel;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.IAjaxCallDecorator;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Panel for selecting sort field and direction and kind of view.
 *
 * @author Aleksey Plotnikov.
 */
public abstract class RecordSortPanel extends BaseEltilandPanel<SearchData> {

    private ELTSelectField<SortKind> sortField = new ELTSelectField<SortKind>(
            "selectSort", ReadonlyObjects.EMPTY_DISPLAY_MODEL, new Model<SortKind>()) {
        @Override
        protected IModel<List<SortKind>> getChoiceListModel() {
            return new LoadableDetachableModel<List<SortKind>>() {
                @Override
                protected List<SortKind> load() {
                    return new ArrayList<>(Arrays.asList(
                            SortKind.DATE_DESC,
                            SortKind.DATE_ASC,
                            SortKind.NAME_DESC,
                            SortKind.NAME_ASC,
                            SortKind.RELEVANCE_DESC,
                            SortKind.RELEVANCE_ASC));
                }
            };
        }

        @Override
        protected IChoiceRenderer<SortKind> getChoiceRenderer() {
            return new IChoiceRenderer<SortKind>() {
                @Override
                public Object getDisplayValue(SortKind object) {
                    return object.toString();
                }

                @Override
                public String getIdValue(SortKind object, int index) {
                    return object.toString();
                }
            };
        }
    };

    /**
     * Panel ctor.
     *
     * @param id markup id.
     */
    public RecordSortPanel(String id, IModel<SearchData> searchDataIModel) {
        super(id, searchDataIModel);

        Form form = new Form("form");

        form.add(sortField);
        String sortKey = getModelObject() != null ? getModelObject().getSortProperty() : null;

        sortField.setModelObject((sortKey == null) ?
                SortKind.DATE_DESC : SortKind.fromStr(sortKey, getModelObject().isAscending()));
        sortField.setNullValid(false);

        form.add(new EltiAjaxSubmitLink("sortButton") {
            @Override
            protected void onSubmit(AjaxRequestTarget target, Form form) {
                RecordSortPanel.this.getModelObject().setSearchString(getCurrentSearch());
                RecordSortPanel.this.getModelObject().setSortProperty(sortField.getModelObject().getField());
                RecordSortPanel.this.getModelObject().setAscending(sortField.getModelObject().isAsc());
                RecordSortPanel.this.getModelObject().redirect();
            }

            @Override
            protected IAjaxCallDecorator getAjaxCallDecorator() {
                return null;
            }
        });
        add(form);

        SwitchViewPanel switchPanel = new SwitchViewPanel("switchViewPanel", getModelObject().getView()) {
            @Override
            protected void onChangeView(LibraryView view, AjaxRequestTarget target) {
                RecordSortPanel.this.getModelObject().setSearchString(getCurrentSearch());
                RecordSortPanel.this.getModelObject().setView(view);
                RecordSortPanel.this.getModelObject().redirect();
            }
        };

        add(switchPanel);
    }

    protected abstract String getCurrentSearch();
}
