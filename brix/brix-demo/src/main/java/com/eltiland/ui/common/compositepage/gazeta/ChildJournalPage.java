package com.eltiland.ui.common.compositepage.gazeta;

import com.eltiland.BrixPanel;
import com.eltiland.bl.GenericManager;
import com.eltiland.ui.common.TwoColumnPage;
import com.eltiland.ui.common.components.ResourcesUtils;
import com.eltiland.utils.UrlUtils;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 * Page with static content for Child journal.
 */
public class ChildJournalPage extends TwoColumnPage {

    @SpringBean
    private GenericManager genericManager;

    public static final String MOUNT_PATH = "/eltik_i_druzia";

    /**
     * Construct.
     */
    public ChildJournalPage() {
        add(new BrixPanel("aboutNewspaperCmsPanel",
                UrlUtils.createBrixPathForPanel("ELTIK_NEWSPAPER/aboutNewspaper.html")));
        /**
         * For render pages in box upload images into readNewspaperCmsPanel using brix admin
         * and specify rel attribute: rel="shadowbox[Элтик и Друзья]"
         */
        add(new BrixPanel("aboutSectionsCmsPanel",
                UrlUtils.createBrixPathForPanel("ELTIK_NEWSPAPER/aboutSections.html")));
        add(new BrixPanel("subscribeNewspaperCmsPanel",
                UrlUtils.createBrixPathForPanel("ELTIK_NEWSPAPER/subscribeNewspaper.html")));

        add(new MagazineStaticPanel("magazine1", new Model<>("eltik_01")));
        add(new MagazineStaticPanel("magazine2", new Model<>("eltik_02")));
        add(new MagazineStaticPanel("magazine3", new Model<>("eltik_03")));
        add(new MagazineStaticPanel("magazine4", new Model<>("eltik_04")));
        add(new MagazineStaticPanel("magazine5", new Model<>("eltik_05")));
        add(new MagazineStaticPanel("magazine6", new Model<>("eltik_06")));
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
        response.renderJavaScriptReference(ResourcesUtils.JS_SHADOWBOX);
        response.renderCSSReference(ResourcesUtils.CSS_SHADOWBOX);
        response.renderOnDomReadyJavaScript("Shadowbox.init({counterType: 'skip'})");
    }
}
