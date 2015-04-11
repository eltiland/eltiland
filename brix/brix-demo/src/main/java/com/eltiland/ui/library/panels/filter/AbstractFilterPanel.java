package com.eltiland.ui.library.panels.filter;

import com.eltiland.ui.common.BaseEltilandPanel;
import com.eltiland.ui.library.SearchData;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

/**
 * Abstract filter panel for library.
 */
public abstract class AbstractFilterPanel<T> extends BaseEltilandPanel<T> {

    protected IModel<SearchData> searchModel = new Model<>();

    protected WebMarkupContainer listContainer = new WebMarkupContainer("filter_panel");
    /**
     * Panel constructor.
     *
     * @param id markup id.
     */
    public AbstractFilterPanel(String id, IModel<SearchData> searchDataIModel) {
        super(id);

        searchModel.setObject(searchDataIModel.getObject());
        listContainer.add(new Label("header", getHeader()));
        listContainer.add(getList());

        add(listContainer.setOutputMarkupId(true));
    }

    protected AbstractFilterPanel(String id, IModel<T> tiModel, IModel<SearchData> searchDataIModel) {
        super(id, tiModel);

        searchModel.setObject(searchDataIModel.getObject());
        listContainer.add(new Label("header", getHeader()));
        listContainer.add(getList());

        add(listContainer.setOutputMarkupId(true));
    }

    /**
     * @return header of the panel.
     */
    protected abstract IModel<String> getHeader();

    /**
     * @return list item of the filters.
     */
    protected abstract ListView getList();

    /**
     * @return current search value.
     */
    protected abstract String getCurrentSearch();
}
