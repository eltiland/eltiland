package com.eltiland.ui.common;

import com.eltiland.ui.common.components.ResourcesUtils;
import com.eltiland.ui.common.components.dialog.EltiStaticAlerts;
import com.eltiland.ui.common.components.menu.ELTMainMenu;
import com.eltiland.ui.common.compositepage.gazeta.ChildJournalPage;
import com.eltiland.ui.common.resource.StaticImage;
import com.eltiland.ui.course.CourseListPage;
import com.eltiland.ui.course.TeachingModulesPage;
import com.eltiland.ui.faq.FaqPage;
import com.eltiland.ui.forum.ForumPage;
import com.eltiland.ui.library.LibraryPage;
import com.eltiland.ui.login.panels.HeadLoginPanel;
import com.eltiland.ui.login.panels.HeadSocialPanel;
import com.eltiland.ui.webinars.WebinarsPage;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.GenericWebPage;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.request.flow.RedirectToUrlException;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.util.Properties;

/**
 * Base class for all pages. Provide simple page with linked all common styles, java script libraries, jquery and
 * favicon.
 *
 * @param <T> the type of the page's model object
 */
public abstract class BaseEltilandPage<T> extends GenericWebPage<T> {

    @SpringBean(name = "eltilandProperties")
    private Properties eltilandProps;

    private static final String CSS = "static/css/base.css";

    @Override
    protected void onInitialize() {
        super.onInitialize();

        add(new HeadLoginPanel("headLogin"));
        add(new HeadSocialPanel("socialPanel"));
        StaticImage image = new StaticImage("logo",
                eltilandProps.getProperty("application.base.url") + "/static/images/homepage/eltiland_logo.png");
        add(image);

        add(new ELTMainMenu("ELTmenu", this.getClass()));

        add(new BottomLinkPanel("homeLink", HomePage.class, new ResourceModel("homePage")));
        add(new BottomLinkPanel("trainLink", TrainingPage.class, new ResourceModel("trainPage")));
        add(new BottomLinkPanel("courseLink", CourseListPage.class, new ResourceModel("coursesPage")));
        add(new BottomLinkPanel("moduleLink", TeachingModulesPage.class, new ResourceModel("modulePage")));
        add(new BottomLinkPanel("webinarLink", WebinarsPage.class, new ResourceModel("webinarsPage")));
        add(new BottomLinkPanel("libraryLink", LibraryPage.class, new ResourceModel("libraryPage")));
        add(new BottomLinkPanel("faqLink", FaqPage.class, new ResourceModel("faqPage")));
        add(new BottomLinkPanel("forumLink", ForumPage.class, new ResourceModel("forumPage")));

        WebMarkupContainer bannerEltik = new WebMarkupContainer("bannerEltik");
        WebMarkupContainer bannerMagazine = new WebMarkupContainer("bannerMagazine");
        WebMarkupContainer bannerEltiKudic = new WebMarkupContainer("bannerEltiKudic");
        WebMarkupContainer bannerChildQuestion = new WebMarkupContainer("bannerChildQuestion");
        WebMarkupContainer bannerExpert = new WebMarkupContainer("bannerExpert");

        bannerEltik.add(new AjaxEventBehavior("onclick") {
            @Override
            protected void onEvent(AjaxRequestTarget target) {
                throw new RedirectToUrlException("http://eltik.ru//");
            }
        });
        bannerMagazine.add(new AjaxEventBehavior("onclick") {
            @Override
            protected void onEvent(AjaxRequestTarget target) {
                setResponsePage(ChildJournalPage.class);
            }
        });
        bannerEltiKudic.add(new AjaxEventBehavior("onclick") {
            @Override
            protected void onEvent(AjaxRequestTarget target) {
                throw new RedirectToUrlException("http://vdm.ru/");
            }
        });
        bannerChildQuestion.add(new AjaxEventBehavior("onclick") {
            @Override
            protected void onEvent(AjaxRequestTarget target) {
                throw new RedirectToUrlException("http://detskiyvopros.ru/");
            }
        });
        bannerExpert.add(new AjaxEventBehavior("onclick") {
            @Override
            protected void onEvent(AjaxRequestTarget target) {
                throw new RedirectToUrlException("http://www.moo-edd.ru/");
            }
        });

        add(bannerChildQuestion);
        add(bannerEltiKudic);
        add(bannerMagazine);
        add(bannerEltik);
        add(bannerExpert);
    }

    /**
     * @see org.apache.wicket.markup.html.GenericWebPage#GenericWebPage()
     */
    protected BaseEltilandPage() {
        super();
    }

    /**
     * @see org.apache.wicket.markup.html.GenericWebPage#GenericWebPage(IModel)
     */
    protected BaseEltilandPage(IModel<T> model) {
        super(model);
    }

    /**
     * @see org.apache.wicket.markup.html.GenericWebPage#GenericWebPage(PageParameters)
     */
    protected BaseEltilandPage(PageParameters parameters) {
        super(parameters);
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        response.renderCSSReference(ResourcesUtils.CSS_ELT_STYLE);
        response.renderCSSReference(CSS);

        response.renderCSSReference(ResourcesUtils.CSS_JQUERY);
        response.renderCSSReference(ResourcesUtils.CSS_COMPONENTS);
        response.renderCSSReference(ResourcesUtils.CSS_COURSE);
        response.renderCSSReference(ResourcesUtils.CSS_STATIC_CONTENT);
        response.renderCSSReference(ResourcesUtils.CSS_VIDEO);
        response.renderCSSReference(ResourcesUtils.CSS_FORUM);
        response.renderCSSReference(ResourcesUtils.CSS_MAGAZINE);
        response.renderCSSReference(ResourcesUtils.CSS_WEBINAR);
        response.renderCSSReference(ResourcesUtils.CSS_ICONPANEL);
        response.renderCSSReference(ResourcesUtils.CSS_SUBSCRIBE);

        response.renderCSSReference(ResourcesUtils.CSS_TOOLTIP);
        response.renderCSSReference(ResourcesUtils.CSS_TOOLTUP_BOX);

        response.renderCSSReference(ResourcesUtils.FONT_A_CAMPUS);

        response.renderCSSReference(ResourcesUtils.CSS_INDICATOR_PACE);

        response.renderJavaScriptReference(ResourcesUtils.JS_JQUERY);
        response.renderJavaScriptReference(ResourcesUtils.JS_JQUERY_UI);
        response.renderJavaScriptReference(ResourcesUtils.JS_JQUERY_COMPONENTS);
        response.renderJavaScriptReference(ResourcesUtils.JS_JQUERY_FUNCTION);
        response.renderJavaScriptReference(ResourcesUtils.JS_VISUAL_EFECTS);
        response.renderJavaScriptReference(ResourcesUtils.JS_NUMBERFORMATTER);
        response.renderJavaScriptReference(ResourcesUtils.JS_YASHARE);
        response.renderJavaScriptReference(ResourcesUtils.JS_TIMEPICKER);
        response.renderJavaScriptReference(ResourcesUtils.JS_COURSE);
        response.renderJavaScriptReference(ResourcesUtils.JS_INDICATOR);
        response.renderJavaScriptReference(ResourcesUtils.JS_INDICATOR_PACE);

        response.renderOnDomReadyJavaScript(String.format("tryRegisterWicketAjaxOnFailure('%s')",
                getString("unreachableServerMessage").replaceAll("\\n", "")));
        response.renderOnDomReadyJavaScript("createShare('http://eltiland.ru/home', " +
                "'Элтиленд - информационно-образовательная среда для детей, педагогов и родителей');");

        EltiStaticAlerts.renderOKPopups(response);
        EltiStaticAlerts.renderErrorPopups(response);
        EltiStaticAlerts.renderWarningPopups(response);
    }

    /**
     * Internal bottom link panel
     */
    private class BottomLinkPanel extends BaseEltilandPanel {

        /**
         * Panel constructor.
         *
         * @param id        markup id.
         * @param clazz     page clazz to jump.
         * @param textModel model of the caption of the link.
         */
        public BottomLinkPanel(String id, Class clazz, IModel<String> textModel) {
            super(id);

            BookmarkablePageLink link = new BookmarkablePageLink<>("link", clazz);
            link.add(new Label("labelText", textModel));
            add(link);
        }
    }
}