package com.eltiland;

import com.eltiland.utils.UrlUtils;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.markup.IMarkupCacheKeyProvider;
import org.apache.wicket.markup.IMarkupResourceStreamProvider;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.util.resource.IResourceStream;
import org.apache.wicket.util.resource.StringResourceStream;
import org.brixcms.Brix;
import org.brixcms.BrixNodeModel;
import org.brixcms.jcr.api.JcrSession;
import org.brixcms.jcr.exception.JcrException;
import org.brixcms.jcr.wrapper.BrixNode;
import org.brixcms.markup.MarkupHelper;
import org.brixcms.markup.MarkupSource;
import org.brixcms.markup.MarkupSourceProvider;
import org.brixcms.markup.tag.Item;
import org.brixcms.markup.tag.Tag;
import org.brixcms.markup.tag.simple.SimpleTag;
import org.brixcms.markup.title.TitleTransformer;
import org.brixcms.markup.transform.HeadTransformer;
import org.brixcms.markup.transform.MarkupSourceTransformer;
import org.brixcms.markup.variable.VariableTransformer;
import org.brixcms.plugin.site.SitePlugin;
import org.brixcms.plugin.site.page.AbstractContainer;
import org.brixcms.plugin.site.page.PageMarkupSource;
import org.brixcms.plugin.site.page.PageNode;
import org.brixcms.web.generic.IGenericComponent;
import org.brixcms.workspace.Workspace;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Panel showing brix-editable content (together with tiles etc)
 */
public class BrixPanel extends Panel implements IMarkupResourceStreamProvider, IGenericComponent<BrixNode>,
        MarkupSourceProvider, IMarkupCacheKeyProvider {

    private static final Logger LOGGER = LoggerFactory.getLogger(BrixPanel.class);
    public static final String NOTFOUND_PATH = UrlUtils.BRIX_PAGE_ROOT + "/not_found.html";

    private StringResourceStream pageStream;

    /**
     * Create a panel displaying content from brix repository. In order to create the correct path, one can use UrlUtils
     * methods
     *
     * @param id                 wicket id
     * @param brixRepositoryPath path within brix repository (JCR)
     */
    public BrixPanel(String id, String brixRepositoryPath) {
        super(id);

        List<Workspace> workspaces = SitePlugin.get().getWorkspaces(null, false);
        Workspace w = workspaces.get(0);
        JcrSession s = Brix.get().getCurrentSession(w.getId());
        try {

            Node n = s.getNode(brixRepositoryPath);
            PageNode jcrNode = (PageNode) PageNode.FACTORY.wrap(Brix.get(), n, s);

            LOGGER.debug("Accessing CMS part of the system via {}", brixRepositoryPath);
            setModel(new BrixNodeModel(jcrNode));
        } catch (JcrException ex) {
            LOGGER.warn("Could not find JCR path {}", brixRepositoryPath);

            Node n = s.getNode(SitePlugin.get().getSiteRootPath() + NOTFOUND_PATH);
            PageNode jcrNode = (PageNode) PageNode.FACTORY.wrap(Brix.get(), n, s);
            setModel(new BrixNodeModel(jcrNode));
        }

        pageStream = new StringResourceStream(
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n<div style=\"background: green;\">"
                        + getMarkupHelper().getMarkup() + "</div><div style=\"clear:both;\"/>", "text/html");
        pageStream.setCharset(Charset.forName("UTF-8"));


    }

    @Override
    public IResourceStream getMarkupResourceStream(MarkupContainer container, Class<?> containerClass) {
        return pageStream;
    }

    private MarkupHelper markupHelper;

    private MarkupHelper getMarkupHelper() {
        if (markupHelper == null) {
            markupHelper = new MarkupHelper(this);
        }
        return markupHelper;
    }

    @Override
    public IModel<BrixNode> getModel() {
        return (IModel<BrixNode>) getDefaultModel();
    }

    @Override
    public BrixNode getModelObject() {
        if (getModel() == null) {
            return null;
        }
        return getModel().getObject();
    }

    @Override
    public void setModel(IModel<BrixNode> brixNodeIModel) {
        setDefaultModel(brixNodeIModel);
    }

    @Override
    public void setModelObject(BrixNode object) {
        setDefaultModelObject(object);
    }

    @Override
    public MarkupSource getMarkupSource() {
        MarkupSource source = new PageMarkupSource((AbstractContainer) getModelObject());
        return transform(source, (AbstractContainer) getModelObject());
    }

    public static MarkupSource transform(MarkupSource source, AbstractContainer container) {
        source = new HeadTransformer(source);
        source = new VariableTransformer(source, container);
        source = new TitleTransformer(source, container);
        source = new WicketPanelTagsTransformer(source);
//        Data Source
        return source;
    }

    @Override
    public String getCacheKey(MarkupContainer container, Class<?> containerClass) {
        return null;
    }

    private static class WicketPanelTagsTransformer extends MarkupSourceTransformer {
        private static Map<String, String> wrapperDivAttrMap = new HashMap<String, String>();
        private static Map<String, String> clearDivAttrMap = new HashMap<String, String>();


        public WicketPanelTagsTransformer(MarkupSource delegate) {
            super(delegate);
            wrapperDivAttrMap.put("class", "cms-panel");
            clearDivAttrMap.put("style", "clear:both");
        }

        @Override
        protected List<Item> transform(List<Item> originalItems) {
            originalItems.add(0, new SimpleTag("div", Tag.Type.OPEN, wrapperDivAttrMap));
            originalItems.add(0, new SimpleTag("wicket:panel", Tag.Type.OPEN, Collections.<String, String>emptyMap()));

            originalItems.add(new SimpleTag("div", Tag.Type.CLOSE, Collections.<String, String>emptyMap()));
            originalItems.add(new SimpleTag("div", Tag.Type.OPEN_CLOSE, clearDivAttrMap));
            originalItems.add(new SimpleTag("wicket:panel", Tag.Type.CLOSE, Collections.<String, String>emptyMap()));
            return originalItems;
        }
    }


}
