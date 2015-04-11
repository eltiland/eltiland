package com.eltiland.ui.library.components.search;

import com.eltiland.model.library.LibraryRecord;
import com.eltiland.ui.common.BaseEltilandPanel;
import com.eltiland.ui.common.components.button.EltiAjaxSubmitLink;
import com.eltiland.ui.library.SearchData;
import com.eltiland.ui.library.components.selector.SelectTypeField;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.IAjaxCallDecorator;
import org.apache.wicket.ajax.form.OnChangeAjaxBehavior;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

/**
 * Search field for library.
 *
 * @author Aleksey Plotnikov
 */
public abstract class SearchField extends BaseEltilandPanel<SearchData> {

    private static final String CSS = "static/css/library/search.css";
    private Class<? extends LibraryRecord> currentType = null;

    private TextField<String> searchText = new TextField<>("textSearch", new Model<String>());
    private String currentValue;


    /**
     * Search field ctor.
     *
     * @param id markup id.
     */
    public SearchField(String id, IModel<SearchData> searchDataIModel) {
        super(id, searchDataIModel);

        Form form = new Form("form");

        currentType = searchDataIModel.getObject().getClazz();
        SelectTypeField selector = new SelectTypeField("selector", searchDataIModel) {
            @Override
            protected void onSelect(AjaxRequestTarget target, Class<? extends LibraryRecord> clazz) {
                currentType = clazz;
            }

            @Override
            protected String getInitialString() {
                return getString("all");
            }

            @Override
            protected boolean enableAll() {
                return true;
            }
        };

        searchText.add(new OnChangeAjaxBehavior() {
            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                currentValue = (((TextField<String>) getComponent()).getModelObject());
            }
        });

        searchText.setModelObject(getModelObject().getSearchString());

        form.add(searchText);
        form.add(selector);
        EltiAjaxSubmitLink submitButton = new EltiAjaxSubmitLink("searchButton") {
            @Override
            protected void onSubmit(AjaxRequestTarget target, Form form) {
                onSearch(target, searchText.getModelObject(), currentType);
            }

            @Override
            protected IAjaxCallDecorator getAjaxCallDecorator() {
                return null;
            }
        };
        form.add(submitButton);
        form.setDefaultButton(submitButton);

        add(form);
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
        response.renderCSSReference(CSS);
    }

    /**
     * OnSearch callback.
     *
     * @param target       Ajax request target
     * @param searchString search string, inputed from user.
     * @param clazz        class of the records to find. (null for all).
     */
    protected abstract void onSearch(
            AjaxRequestTarget target, String searchString, Class<? extends LibraryRecord> clazz);

    /**
     * @return current value of text search field
     */
    public String getCurrentValue() {
        String value = searchText.getModelObject();
        return (value == null) ? currentValue : value;
    }
}
