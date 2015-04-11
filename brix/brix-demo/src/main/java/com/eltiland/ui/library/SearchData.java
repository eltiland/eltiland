package com.eltiland.ui.library;

import com.eltiland.bl.GenericManager;
import com.eltiland.model.library.*;
import com.eltiland.model.tags.Tag;
import org.apache.commons.collections.BidiMap;
import org.apache.commons.collections.bidimap.DualHashBidiMap;
import org.apache.velocity.util.StringUtils;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.injection.Injector;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Search data class.
 *
 * @author Aleksey Plotnikov.
 */
public class SearchData implements Serializable {

    @SpringBean
    private GenericManager genericManager;

    private BidiMap classMap = new DualHashBidiMap();

    public static final String SEARCH_PARAM = "search";
    public static final String SORT_PARAM = "sort";
    public static final String ASC_PARAM = "asc";
    public static final String VIEW_PARAM = "view";
    public static final String TYPE_PARAM = "type";
    public static final String TAG_PARAM = "tag";
    public static final String COL_PARAM = "col";

    private String searchString;
    private String sortProperty;
    private boolean isAscending;
    private Class<? extends LibraryRecord> clazz;
    private List<Tag> tags = new ArrayList<>();
    private LibraryCollection collection;
    private LibraryView view;

    public SearchData(PageParameters params) {
        Set<String> keySet = params.getNamedKeys();

        Injector.get().inject(this);

        classMap.put("doc", LibraryDocumentRecord.class);
        classMap.put("video", LibraryVideoRecord.class);
        classMap.put("pres", LibraryPresentationRecord.class);
        classMap.put("image", LibraryImageRecord.class);
        classMap.put("arch", LibraryArchiveRecord.class);
        classMap.put("all", LibraryRecord.class);

        if (keySet.contains(SearchData.SEARCH_PARAM)) {
            this.setSearchString(params.get(SearchData.SEARCH_PARAM).toString());
        }
        if (keySet.contains(SearchData.SORT_PARAM)) {
            this.setSortProperty(params.get(SearchData.SORT_PARAM).toString());
        }
        if (keySet.contains(SearchData.ASC_PARAM)) {
            this.setAscending(params.get(SearchData.ASC_PARAM).toBoolean());
        }
        if (keySet.contains(SearchData.VIEW_PARAM)) {
            this.setView(LibraryView.fromStr(params.get(SearchData.VIEW_PARAM).toString()));
        }
        if (keySet.contains(SearchData.TYPE_PARAM)) {
            this.setClazz(getClassFromStr(params.get(SearchData.TYPE_PARAM).toString()));
        }
        if (keySet.contains(SearchData.TAG_PARAM)) {
            String str = params.get(TAG_PARAM).toString();
            String[] tag_ids = StringUtils.split(str, "_");
            List<Tag> tags = new ArrayList<>();
            for (String id : tag_ids) {
                tags.add(genericManager.getObject(Tag.class, Long.parseLong(id)));
            }
            this.setTags(tags);
        }
        if (keySet.contains(SearchData.COL_PARAM)) {
            Long id = params.get(COL_PARAM).toLong();
            this.setCollection(genericManager.getObject(LibraryCollection.class, id));
        }
    }

    public void redirect() {
        PageParameters pageParameters = new PageParameters();
        if (searchString != null && !searchString.isEmpty()) {
            pageParameters.add(SEARCH_PARAM, searchString);
        }
        if (sortProperty != null && !sortProperty.isEmpty()) {
            pageParameters.add(SORT_PARAM, sortProperty);
            pageParameters.add(ASC_PARAM, isAscending);
        }
        if (view != null) {
            pageParameters.add(VIEW_PARAM, view.toString());
        }
        if (clazz != null) {
            pageParameters.add(TYPE_PARAM, getStrFromClass(clazz));
        }
        if (!tags.isEmpty()) {
            String tagString = "";
            boolean isFirst = true;

            for (Tag tag : getTags()) {
                if (!isFirst) {
                    tagString += "_";
                } else {
                    isFirst = false;
                }

                tagString += tag.getId().toString();
            }
            pageParameters.add(TAG_PARAM, tagString);
        }
        if (collection != null) {
            pageParameters.add(COL_PARAM, collection.getId().toString());
        }

        throw new RestartResponseException(LibraryPage.class, pageParameters);
    }

    private Class<? extends LibraryRecord> getClassFromStr(String str) {
        return (Class<? extends LibraryRecord>) classMap.get(str);
    }

    private String getStrFromClass(Class<? extends LibraryRecord> clazz) {
        return (String) classMap.getKey(clazz);
    }

    public String getSearchString() {
        return searchString;
    }

    public void setSearchString(String searchString) {
        this.searchString = searchString;
    }

    public String getSortProperty() {
        return sortProperty;
    }

    public void setSortProperty(String sortProperty) {
        this.sortProperty = sortProperty;
    }

    public boolean isAscending() {
        return isAscending;
    }

    public void setAscending(boolean ascending) {
        isAscending = ascending;
    }

    public Class<? extends LibraryRecord> getClazz() {
        return (clazz == null) ? LibraryRecord.class : clazz;
    }

    public void setClazz(Class<? extends LibraryRecord> clazz) {
        this.clazz = clazz;
    }

    public List<Tag> getTags() {
        return tags;
    }

    public void setTags(List<Tag> tags) {
        this.tags = tags;
    }

    public LibraryView getView() {
        return (view == null) ? LibraryView.LIST : view;
    }

    public void setView(LibraryView view) {
        this.view = view;
    }

    public LibraryCollection getCollection() {
        return collection;
    }

    public void setCollection(LibraryCollection collection) {
        this.collection = collection;
    }
}
