package com.eltiland.ui.library.panels.filter.collection;

import com.eltiland.bl.GenericManager;
import com.eltiland.bl.library.LibraryCollectionManager;
import com.eltiland.model.library.LibraryCollection;
import com.eltiland.ui.library.SearchData;
import com.eltiland.ui.library.panels.filter.AbstractFilterPanel;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Collection Filter Panel.
 *
 * @author Aleksey Plotnikov.
 */
public abstract class CollectionFilterPanel extends AbstractFilterPanel {

    @SpringBean
    private GenericManager genericManager;
    @SpringBean
    private LibraryCollectionManager libraryCollectionManager;

    protected Map<Long, Boolean> collectionStatus = new HashMap<>(0);
    private Long selectedCollection = null;

    /**
     * Panel constrctor.
     *
     * @param id markup id.
     */
    public CollectionFilterPanel(String id, IModel<SearchData> searchDataIModel) {
        super(id, searchDataIModel);

        if (collectionStatus.isEmpty()) {
            if (searchDataIModel.getObject().getCollection() != null) {
                selectedCollection = searchDataIModel.getObject().getCollection().getId();
            }

            List<LibraryCollection> topCollections = libraryCollectionManager.getTopLibraryCollectionList();
            for (LibraryCollection topCollection : topCollections) {
                genericManager.initialize(topCollection, topCollection.getSubCollections());
                if (!(topCollection.getSubCollections().isEmpty())) {
                    boolean isToogled = false;
                    if (selectedCollection != null) {
                        for (LibraryCollection subCollection : topCollection.getSubCollections()) {
                            if (subCollection.getId().equals(selectedCollection)) {
                                isToogled = true;
                                break;
                            }
                        }
                    }
                    collectionStatus.put(topCollection.getId(), isToogled);
                }
            }
        }
    }

    @Override
    protected IModel<String> getHeader() {
        return new ResourceModel("header");
    }

    @Override
    protected ListView getList() {
        return new ListView<LibraryCollection>("list",
                new LoadableDetachableModel<List<? extends LibraryCollection>>() {
                    @Override
                    protected List<? extends LibraryCollection> load() {
                        List<LibraryCollection> list = new ArrayList<>();
                        List<LibraryCollection> topCollections = libraryCollectionManager.getTopLibraryCollectionList();
                        for (LibraryCollection collection : topCollections) {
                            list.add(collection);
                            genericManager.initialize(collection, collection.getSubCollections());
                            for (LibraryCollection subCollection : collection.getSubCollections()) {
                                list.add(subCollection);
                            }
                        }
                        return list;
                    }
                }) {


            @Override
            protected void populateItem(final ListItem<LibraryCollection> item) {
                final CollectionFilterItemPanel panel = new CollectionFilterItemPanel("filterItem", item.getModelObject(),
                        (((SearchData) searchModel.getObject()).getCollection() != null) &&
                                (((SearchData) searchModel.getObject()).getCollection().getId().equals(
                                        item.getModelObject().getId()))) {
                    @Override
                    protected void onClick(AjaxRequestTarget target) {
                        ((SearchData) searchModel.getObject()).setCollection(item.getModelObject());
                        ((SearchData) searchModel.getObject()).setSearchString(getCurrentSearch());
                        ((SearchData) searchModel.getObject()).redirect();
                    }

                    @Override
                    protected void onClean(AjaxRequestTarget target) {
                        ((SearchData) searchModel.getObject()).setCollection(null);
                        ((SearchData) searchModel.getObject()).setSearchString(getCurrentSearch());
                        ((SearchData) searchModel.getObject()).redirect();
                    }
                };
                genericManager.initialize(item.getModelObject(), item.getModelObject().getParent());
                genericManager.initialize(item.getModelObject(), item.getModelObject().getSubCollections());

                if (item.getModelObject().getParent() != null) {
                    Boolean status = collectionStatus.get(item.getModelObject().getParent().getId());

                    panel.setVisible((status != null) ? status : false);
                    panel.add(new AttributeModifier("class", new Model<>("filter_item filter_subitem")));
                }

                final WebMarkupContainer toggle = new WebMarkupContainer("toggle");
                item.add(toggle.setVisible(!(item.getModelObject().getSubCollections().isEmpty())));

                Boolean isToogled = collectionStatus.get(item.getModelObject().getId());

                if (isToogled != null && isToogled.booleanValue()) {
                    toggle.add(new AttributeModifier("class", "toggle minus"));
                } else {
                    toggle.add(new AttributeModifier("class", "toggle plus"));
                }

                toggle.add(new AjaxEventBehavior("onclick") {
                    @Override
                    protected void onEvent(AjaxRequestTarget target) {
                        Boolean status = collectionStatus.get(item.getModelObject().getId());
                        collectionStatus.remove(item.getModelObject().getId());
                        collectionStatus.put(item.getModelObject().getId(), !status);
                        target.add(listContainer);
                    }
                });

                item.add(panel);
            }
        };
    }
}
