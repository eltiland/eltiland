package com.eltiland.ui.library.view.kind;

import com.eltiland.bl.library.LibraryRecordManager;
import com.eltiland.exceptions.EltilandManagerException;
import com.eltiland.model.library.LibraryRecord;
import com.eltiland.ui.common.BaseEltilandPanel;
import com.eltiland.ui.common.components.navigator.ELTAjaxPagingNavigator;
import com.eltiland.ui.library.SearchData;
import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.PageableListView;
import org.apache.wicket.markup.html.navigation.paging.IPageable;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Abstract list view panel.
 *
 * @author Aleksey Plotnikov.
 */
public abstract class AbstractRecordViewPanel extends BaseEltilandPanel<SearchData> {

    protected static final Logger LOGGER = LoggerFactory.getLogger(AbstractRecordViewPanel.class);

    @SpringBean
    private LibraryRecordManager libraryRecordManager;

    private int index = 0;

    protected IModel<List<LibraryRecord>> recordModel = new LoadableDetachableModel<List<LibraryRecord>>() {
        @Override
        protected List<LibraryRecord> load() {
            try {
                return libraryRecordManager.getRecordList(
                        AbstractRecordViewPanel.this.getModelObject().getSearchString(),
                        AbstractRecordViewPanel.this.getModelObject().getClazz(),
                        AbstractRecordViewPanel.this.getModelObject().getTags(),
                        AbstractRecordViewPanel.this.getModelObject().getCollection(), index, getViewCount(),
                        AbstractRecordViewPanel.this.getModelObject().getSortProperty(),
                        AbstractRecordViewPanel.this.getModelObject().isAscending());
            } catch (EltilandManagerException e) {
                LOGGER.error("Cannot search on library", e);
                throw new WicketRuntimeException(e);
            }
        }
    };

    protected IModel<Integer> recordCountModel = new LoadableDetachableModel<Integer>() {
        @Override
        protected Integer load() {
            try {
                return libraryRecordManager.getRecordListCount(
                        AbstractRecordViewPanel.this.getModelObject().getSearchString(),
                        AbstractRecordViewPanel.this.getModelObject().getClazz(),
                        AbstractRecordViewPanel.this.getModelObject().getTags(),
                        AbstractRecordViewPanel.this.getModelObject().getCollection());
            } catch (EltilandManagerException e) {
                LOGGER.error("Cannot search on library", e);
                throw new WicketRuntimeException(e);
            }
        }
    };

    private WebMarkupContainer mainContainer = new WebMarkupContainer("mainContainer");

    protected WebMarkupContainer listContainer = new WebMarkupContainer("listContainer");

    protected RecordNavigator navigatorTop;
    protected RecordNavigator navigatorDown;

    /**
     * Panel ctor.
     *
     * @param id markup id.
     */
    public AbstractRecordViewPanel(String id, IModel<SearchData> searchDataIModel) {
        super(id, searchDataIModel);

        setOutputMarkupId(true);
        index = 0;

        final PageableListView<LibraryRecord> list =
                new PageableListView<LibraryRecord>("list", recordModel, getViewCount()) {
                    @Override
                    protected void populateItem(ListItem<LibraryRecord> item) {
                        item.add(getPanel("item", item.getModel()));
                    }

                    @Override
                    public int getViewSize() {
                        return Math.min((getItemCount() - index), getViewCount());
                    }

                    @Override
                    public int getItemCount() {
                   //     try {
                            return recordCountModel.getObject();
//                            return libraryRecordManager.getRecordListCount(
//                                    AbstractRecordViewPanel.this.getModelObject().getSearchString(),
//                                    AbstractRecordViewPanel.this.getModelObject().getClazz(),
//                                    AbstractRecordViewPanel.this.getModelObject().getTags(),
//                                    AbstractRecordViewPanel.this.getModelObject().getCollection());
//                        } catch (EltilandManagerException e) {
//                            LOGGER.error("Cannot search on library", e);
//                            throw new WicketRuntimeException(e);
//                        }
                    }
                };

        add(mainContainer.setOutputMarkupId(true));
        mainContainer.add(listContainer.setOutputMarkupId(true));

        int recordCount = list.getItemCount();
        int pageCount = list.getPageCount();

        String label = getString("noRecords");
        if (pageCount == 1) {
            label = String.format(getString("recordCount"), recordCount);
        }
        if (pageCount > 1) {
            label = String.format(getString(
                    (pageCount < 5) ? "recordCountPagesSmall" : "recordCountPages"), recordCount, pageCount);
        }
        mainContainer.add(new Label("countLabel", label));

        WebMarkupContainer collectionContainer = new WebMarkupContainer("collectionContainer") {
            @Override
            public boolean isVisible() {
                return AbstractRecordViewPanel.this.getModelObject().getCollection() != null;
            }
        };

        if (AbstractRecordViewPanel.this.getModelObject().getCollection() != null) {
            collectionContainer.add(new Label("name",
                    AbstractRecordViewPanel.this.getModelObject().getCollection().getName()));
            collectionContainer.add(new Label("description",
                    AbstractRecordViewPanel.this.getModelObject().getCollection().getDescription()));
        }

        mainContainer.add(collectionContainer);
        listContainer.add(list);

        navigatorTop = new RecordNavigator("navigatorTop", list) {
            @Override
            protected void onEvent(AjaxRequestTarget target) {
                target.add(mainContainer);
            }
        };

        navigatorDown = new RecordNavigator("navigatorDown", list) {
            @Override
            protected void onEvent(AjaxRequestTarget target) {
                target.add(mainContainer);
            }
        };

        mainContainer.add(navigatorTop.setOutputMarkupId(true));
        mainContainer.add(navigatorDown.setOutputMarkupId(true));
    }

    protected abstract void onChange(AjaxRequestTarget target);

    protected abstract int getViewCount();

    protected abstract BaseEltilandPanel getPanel(String markupId, IModel<LibraryRecord> recordIModel);

    public abstract class RecordNavigator extends ELTAjaxPagingNavigator {

        private IPageable pageable;

        public RecordNavigator(String id, IPageable pageable) {
            super(id, pageable);
            this.pageable = pageable;
        }

        @Override
        public boolean isVisible() {
            return recordCountModel.getObject() > getViewCount();
//            try {
//                return libraryRecordManager.getRecordListCount(
//                        AbstractRecordViewPanel.this.getModelObject().getSearchString(),
//                        AbstractRecordViewPanel.this.getModelObject().getClazz(),
//                        AbstractRecordViewPanel.this.getModelObject().getTags(),
//                        AbstractRecordViewPanel.this.getModelObject().getCollection()) > getViewCount();
//            } catch (EltilandManagerException e) {
//                LOGGER.error("Cannot search on library", e);
//                throw new WicketRuntimeException(e);
//            }
        }

        @Override
        protected void onAjaxEvent(AjaxRequestTarget target) {
            super.onAjaxEvent(target);
            index = this.pageable.getCurrentPage() * getViewCount();
            recordModel.detach();
            onEvent(target);
        }

        protected abstract void onEvent(AjaxRequestTarget target);
    }
}
