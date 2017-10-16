package com.eltiland.ui.webinars.plugin.tab.subscribe.components;

import com.eltiland.bl.GenericManager;
import com.eltiland.bl.WebinarManager;
import com.eltiland.model.webinar.Webinar;
import com.eltiland.ui.common.components.button.EltiAjaxLink;
import com.eltiland.ui.common.components.selector.ELTSelectDialog;
import com.eltiland.ui.common.model.GenericDBListModel;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.PropertyColumn;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.FormComponentPanel;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Элемент управдения для выбора
 */
public class SubWebinarSelector extends FormComponentPanel<List<Webinar>> {

    @SpringBean
    private WebinarManager webinarManager;
    @SpringBean
    private GenericManager genericManager;

    private WebMarkupContainer listContainer = new WebMarkupContainer("listContainer");

    private IModel<List<Webinar>> webinarsModel = new GenericDBListModel<>(Webinar.class);

    private IModel<List<Webinar>> webinarsViewModel = new LoadableDetachableModel<List<Webinar>>() {
        @Override
        protected List<Webinar> load() {
            return webinarsModel.getObject();
        }
    };

    private ListView<Webinar> webinarListView = new ListView<Webinar>("webinarList", webinarsViewModel ) {
        @Override
        protected void populateItem(ListItem<Webinar> listItem) {
            listItem.add(new Label("webinarInnerPanel", listItem.getModel().getObject().getName()));
        }
    };

    private ELTSelectDialog<Webinar> selectDialog = new ELTSelectDialog<Webinar>("webinarSelectDialog", 750) {
        @Override
        protected int getMaxRows() {
            return 10;
        }

        @Override
        protected String getHeader() {
            return SubWebinarSelector.this.getString("selectHeader");
        }

        @Override
        protected void onSelect(AjaxRequestTarget target, List<Long> selectedIds) {
            List<Webinar> webinars = new ArrayList<>();
            for(Long id: selectedIds) {
                Webinar webinar = genericManager.getObject(Webinar.class, id);
                webinars.add(webinar);
            }
            webinarsModel.setObject(webinars);
            this.close(target);

            webinarsViewModel.detach();
            target.add(listContainer);
        }

        @Override
        protected List<IColumn<Webinar>> getColumns() {
            List<IColumn<Webinar>> columns = new ArrayList<>();
            columns.add(new PropertyColumn<Webinar>(new ResourceModel("nameColumn"), "name", "name"));
            return columns;
        }

        @Override
        protected Iterator getIterator(int first, int count) {
            return webinarManager.getWebinarList(
                    first, count, getSort().getProperty(), getSort().isAscending(), true, true, null).iterator();
        }

        @Override
        protected int getSize() {
            return webinarManager.getWebinarCount(true, true, null);
        }

        @Override
        protected boolean isSearching() {
            return false;
        }
    };

    public SubWebinarSelector(String id, IModel<List<Webinar>> model) {
        super(id, model);

        add(listContainer.setOutputMarkupId(true));
        listContainer.add(webinarListView);

        add( new EltiAjaxLink("editButton") {
            @Override
            public void onClick(AjaxRequestTarget ajaxRequestTarget) {
                selectDialog.show(ajaxRequestTarget);
            }
        });

        add(selectDialog);
    }

    public void setWebinars(List<Webinar> webinars) {
        webinarsModel.setObject(webinars);
        webinarsViewModel.setObject(webinars);
        List<Long> ids = new ArrayList<>();
        for(Webinar webinar : webinarsModel.getObject()) {
            ids.add(webinar.getId());
        }
        selectDialog.getDialogPanel().setSelectedIds(ids);
    }

    @Override
    protected void convertInput() {
        setConvertedInput(webinarsModel.getObject());
    }
}

