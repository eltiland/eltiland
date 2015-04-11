package com.eltiland.ui.library.components.selector;

import com.eltiland.model.library.*;
import com.eltiland.ui.common.BaseEltilandPanel;
import com.eltiland.ui.library.SearchData;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.ResourceModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Component for selecting record type.
 *
 * @author Aleksey Plotnikov.
 */
public abstract class SelectTypeField extends BaseEltilandPanel {

    private static final String CSS = "static/css/library/dropdown/demo.css";
    private static final String CSS_DEMO = "static/css/library/dropdown/dropdown.css";

    private static final String JS = "static/jquery/js/library/dropdown.js";

    private ListView<RecordType> list = new ListView<RecordType>("itemList",
            new LoadableDetachableModel<List<? extends RecordType>>() {
                @Override
                protected List<? extends RecordType> load() {
                    List<RecordType> list = new ArrayList<>();
                    if (enableAll()) {
                        list.add(new RecordType("icon-search", LibraryRecord.class));
                    }
                    list.add(new RecordType("icon-pencil", LibraryDocumentRecord.class));
                    list.add(new RecordType("icon-map-marker", LibraryPresentationRecord.class));
                    list.add(new RecordType("icon-picture", LibraryImageRecord.class));
                    list.add(new RecordType("icon-camera", LibraryVideoRecord.class));
                    list.add(new RecordType("icon-file", LibraryArchiveRecord.class));
                    return list;
                }
            }) {
        @Override
        protected void populateItem(ListItem<RecordType> item) {
            item.add(new ItemPanel("itemPanel", item.getModel()) {
                @Override
                protected void onClick(AjaxRequestTarget target, Class<? extends LibraryRecord> clazz) {
                    onSelect(target, clazz);
                }
            });
        }
    };

    private Class<? extends LibraryRecord> clazz = LibraryRecord.class;

    /**
     * Panel constructor.
     *
     * @param id markup id.
     */
    public SelectTypeField(String id) {
        super(id);

        add(new Label("header", getInitialString()));
        add(list);
    }

    /**
     * Panel constructor.
     *
     * @param id markup id.
     */
    public SelectTypeField(String id, IModel<SearchData> searchDataIModel) {
        super(id);

        clazz = searchDataIModel.getObject().getClazz();
        add(new Label("header", getInitialString()));
        add(list);
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
        response.renderCSSReference(CSS);
        response.renderCSSReference(CSS_DEMO);
        response.renderJavaScriptReference(JS);

        if (!clazz.equals(LibraryRecord.class)) {
            response.renderOnDomReadyJavaScript(String.format("$('.selector').find('span').text('%s')",
                    getString(clazz.getSimpleName() + ".type")));
        }
    }

    /**
     * @return initial string, which will be displayed ing
     */
    protected String getInitialString() {
        return getString("selector");
    }

    /**
     * @return TRUE, if 'All' option is enabled.
     */
    protected boolean enableAll() {
        return false;
    }

    /**
     * Callback, called when user select item in the dropdown list.
     *
     * @param target request target.
     * @param clazz  class of the selected item.
     */
    protected abstract void onSelect(AjaxRequestTarget target, Class<? extends LibraryRecord> clazz);

    /**
     * Internal item panel, used in dropdown list.
     */
    private abstract class ItemPanel extends BaseEltilandPanel<RecordType> {
        private ItemPanel(String id, final IModel<RecordType> recordTypeIModel) {
            super(id, recordTypeIModel);

            WebMarkupContainer link = new WebMarkupContainer("link");
            link.add(new AjaxEventBehavior("onclick") {
                @Override
                protected void onEvent(AjaxRequestTarget target) {
                    onClick(target, recordTypeIModel.getObject().getClazz());
                }
            });

            link.add(new Label("label",
                    new ResourceModel(recordTypeIModel.getObject().getClazz().getSimpleName() + ".type")));

            WebMarkupContainer iconContainer = new WebMarkupContainer("icon");
            iconContainer.add(new AttributeModifier("class", recordTypeIModel.getObject().getIconStyle()));
            link.add(iconContainer);
            add(link);
        }

        protected abstract void onClick(AjaxRequestTarget target, Class<? extends LibraryRecord> clazz);
    }

    /**
     * Internal class for
     */
    private class RecordType {
        private String iconStyle;
        private Class<? extends LibraryRecord> clazz;

        private RecordType(String iconStyle, Class<? extends LibraryRecord> clazz) {
            this.iconStyle = iconStyle;
            this.clazz = clazz;
        }

        public String getIconStyle() {
            return iconStyle;
        }

        public void setIconStyle(String iconStyle) {
            this.iconStyle = iconStyle;
        }

        public Class<? extends LibraryRecord> getClazz() {
            return clazz;
        }

        public void setClazz(Class<? extends LibraryRecord> clazz) {
            this.clazz = clazz;
        }
    }
}
