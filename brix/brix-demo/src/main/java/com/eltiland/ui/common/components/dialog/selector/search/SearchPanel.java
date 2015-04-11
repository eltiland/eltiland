package com.eltiland.ui.common.components.dialog.selector.search;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;

/**
 * Base modifiable search panel.
 *
 * @author Ihor Cherednichenko
 * @version 1.1
 */
public abstract class SearchPanel extends Panel {

    protected Form searchForm = new Form("searchForm");
    private TextField<String> searchPatternField = new TextField<String>("searchPatternField", new Model<String>());

    /**
     * Default constructor.
     *
     * @param id wicket component id
     */
    public SearchPanel(String id) {
        super(id);
        add(searchForm);

        searchForm.add(searchPatternField);


    }


    protected void onSubmit(AjaxRequestTarget target) {

    }

    /**
     * Reset search pattern to default value.
     */
    public final void resetSearchPattern() {
        searchPatternField.setModelObject(null);
    }

    /**
     * @return Return current search string. Can be null.
     */
    public String getCurrentSearchPattern() {
        return (String) searchPatternField.getDefaultModelObject();
    }
}
