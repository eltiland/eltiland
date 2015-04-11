/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.brixcms.demo.web;

import com.eltiland.CMSPage;
import com.eltiland.session.EltilandAuthorizationStrategy;
import com.eltiland.session.EltilandSession;
import com.eltiland.ui.common.*;
import com.eltiland.ui.common.components.ResourcesUtils;
import com.eltiland.ui.common.compositepage.allgames.AllGamesPage;
import com.eltiland.ui.common.compositepage.children.ChildrenPage;
import com.eltiland.ui.common.compositepage.gazeta.ChildJournalPage;
import com.eltiland.ui.common.compositepage.ipadclub.IPadClubPage;
import com.eltiland.ui.common.compositepage.parents.ParentsPage;
import com.eltiland.ui.common.compositepage.teachers.TeachersPage;
import com.eltiland.ui.common.resource.ImageResourceReference;
import com.eltiland.ui.course.*;
import com.eltiland.ui.faq.FaqPage;
import com.eltiland.ui.forum.ForumMessagePage;
import com.eltiland.ui.forum.ForumPage;
import com.eltiland.ui.forum.ForumThreadPage;
import com.eltiland.ui.google.pages.GoogleDocEditPage;
import com.eltiland.ui.google.pages.GoogleDocViewPage;
import com.eltiland.ui.google.pages.GooglePresEditPage;
import com.eltiland.ui.google.pages.GooglePresViewPage;
import com.eltiland.ui.library.LibraryAdminPage;
import com.eltiland.ui.library.LibraryEditRecordPage;
import com.eltiland.ui.library.LibraryPage;
import com.eltiland.ui.library.panels.view.ImagePreviewPage;
import com.eltiland.ui.library.panels.view.RecordViewPage;
import com.eltiland.ui.login.LoginPage;
import com.eltiland.ui.login.LogoutPage;
import com.eltiland.ui.login.RegisterPage;
import com.eltiland.ui.login.ResetPasswordPage;
import com.eltiland.ui.magazine.MagazineAboutPage;
import com.eltiland.ui.magazine.MagazineDownloadPage;
import com.eltiland.ui.magazine.MagazinePage;
import com.eltiland.ui.magazine.MagazinePayPage;
import com.eltiland.ui.payment.*;
import com.eltiland.ui.paymentnew.PaymentFailPage;
import com.eltiland.ui.paymentnew.PaymentPage;
import com.eltiland.ui.paymentnew.PaymentProcessingPage;
import com.eltiland.ui.paymentnew.PaymentSuccessPage;
import com.eltiland.ui.subscribe.SubscribePage;
import com.eltiland.ui.subscribe.UnsubscribePage;
import com.eltiland.ui.video.VideoPage;
import com.eltiland.ui.webinars.WebinarsPage;
import com.eltiland.ui.worktop.BaseWorktopPage;
import com.eltiland.ui.worktop.simple.ProfileViewPage;
import org.apache.wicket.Page;
import org.apache.wicket.RuntimeConfigurationType;
import org.apache.wicket.authroles.authentication.AbstractAuthenticatedWebSession;
import org.apache.wicket.markup.html.SecurePackageResourceGuard;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.resource.ContextRelativeResource;
import org.apache.wicket.resource.IPropertiesFactoryContext;
import org.apache.wicket.resource.PropertiesFactory;
import org.apache.wicket.resource.XmlFilePropertiesLoader;
import org.apache.wicket.spring.injection.annot.SpringComponentInjector;
import org.brixcms.Brix;
import org.brixcms.Path;
import org.brixcms.config.BrixConfig;
import org.brixcms.config.PrefixUriMapper;
import org.brixcms.config.UriMapper;
import org.brixcms.demo.web.admin.AdminPage;
import org.brixcms.jcr.JcrSessionFactory;
import org.brixcms.jcr.api.JcrSession;
import org.brixcms.plugin.site.SitePlugin;
import org.brixcms.web.nodepage.BrixNodePageUrlMapper;
import org.brixcms.workspace.Workspace;
import org.brixcms.workspace.WorkspaceManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.ImportUUIDBehavior;
import java.util.*;

/**
 * Application object for your web application. If you want to run this application without deploying, run the Start
 * class.
 */
public final class WicketApplication extends AbstractWicketApplication {
    private static final Logger log = LoggerFactory.getLogger(WicketApplication.class);

    private static final Map<Long, Date> usersOnline = new HashMap<Long, Date>();

    public static final int USERONLINE_CLEAN_PERIOD = 1 * 60 * 1000;

    public static final int INACTIVITY_USER_PERIOD = 1 * 60 * 1000;

    private static final Logger LOGGER = LoggerFactory.getLogger(WicketApplication.class);

    /**
     * brix instance
     */
    private Brix brix;
    private final Timer currentlyOnlineClearTimer = new Timer();

    public Brix getBrix() {
        return brix;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public Class<? extends Page> getHomePage() {
        return HomePage.class;
    }

    public List<Long> getRandomOnlineUsersIds(int limit) {
        ArrayList<Long> listToReturn = new ArrayList<Long>();
        for (Long key : usersOnline.keySet()) {
            listToReturn.add(key);
            if (listToReturn.size() >= limit)
                break;
        }
        return listToReturn;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void init() {
        super.init();

        getRequestCycleListeners().add(new WicketRequestCycleListener() {
            @Override
            public void onBeginRequest(RequestCycle cycle) {
                if (EltilandSession.get().getCurrentUser() == null) {
                    return;
                }

                usersOnline.put(EltilandSession.get().getCurrentUser().getId(), new Date());
            }
        });

        currentlyOnlineClearTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                LOGGER.debug("Cleaning stalled online user ids (inactive)");

                ArrayList<Long> toRemove = new ArrayList<Long>();
                Date now = new Date();
                for (Map.Entry<Long, Date> entry : usersOnline.entrySet()) {
                    if (now.getTime() - entry.getValue().getTime() > INACTIVITY_USER_PERIOD) {
                        toRemove.add(entry.getKey());
                    }
                }

                for (Long val : toRemove) {
                    usersOnline.remove(val);
                }
                LOGGER.debug("Cleaned {} online user ids as stalled (inactive for some time).", toRemove.size());

            }
        }, 0, USERONLINE_CLEAN_PERIOD);

        SecurePackageResourceGuard guard = (SecurePackageResourceGuard) getResourceSettings().getPackageResourceGuard();
        //adding this to make TinyMCE dialogs work.
        guard.addPattern("+*.htm");

        //Spring component injector
        getComponentInstantiationListeners().add(new SpringComponentInjector(WicketApplication.this));

        getResourceSettings().setPropertiesFactory(new TempPropertiesResourceFactory(getResourceSettings()));
        getResourceSettings().setLocalizer(new EltiLocalizer());

        getSecuritySettings().setAuthorizationStrategy(new EltilandAuthorizationStrategy());
        getSecuritySettings().setUnauthorizedComponentInstantiationListener(this);

        getMarkupSettings().setDefaultMarkupEncoding("UTF-8");

        final JcrSessionFactory sf = getJcrSessionFactory();
        final WorkspaceManager wm = getWorkspaceManager();

        //TODO: replace next command
        getApplicationSettings().setAccessDeniedPage(BrixNodePageUrlMapper.HomePage.class);
        getApplicationSettings().setPageExpiredErrorPage(LoginPage.class);

        try {
            // create uri mapper for the cms
            // we are mounting the cms on the root, and getting the workspace name from the
            // application properties
            UriMapper mapper = new PrefixUriMapper(Path.ROOT) {
                public Workspace getWorkspaceForRequest(RequestCycle requestCycle, Brix brix) {
                    final String name = getProperties().getJcrDefaultWorkspace();
                    SitePlugin sitePlugin = SitePlugin.get(brix);
                    return sitePlugin.getSiteWorkspace(name, getProperties().getWorkspaceDefaultState());
                }
            };

            // create brix configuration
            BrixConfig config = new BrixConfig(sf, wm, mapper);
            config.setHttpPort(getProperties().getHttpPort());
            config.setHttpsPort(getProperties().getHttpsPort());

            // create brix instance and attach it to this application
            brix = new DemoBrix(config);
            brix.attachTo(this);
            initializeRepository();
            initDefaultWorkspace();

            // we don't need this here anymore since 1.5 - idea of
            // requestcycleprocessor has been replaced by requestmappers
            // getRequestCycleListeners().add(new
            // BrixRequestCyclerocessor(brix));
        } catch (Exception e) {
            log.error("Exception in WicketApplication init()", e);
        } finally {
            // since we accessed session factory we also have to perform cleanup
            cleanupSessionFactory();
        }
        //shared resources.
        mountResource("/resource/image", new ImageResourceReference());

        //Pages
        mountPage(RegisterPage.MOUNT_PATH, RegisterPage.class);
        mountPage(LoginPage.MOUNT_PATH, LoginPage.class);
        mountPage(CMSPage.MOUNT_PATH, CMSPage.class);
        mountPage(ChildJournalPage.MOUNT_PATH, ChildJournalPage.class);
        mountPage(HomePage.MOUNT_PATH, HomePage.class);
        mountPage(AllGamesPage.MOUNT_PATH, AllGamesPage.class);
        mountPage(ParentsPage.MOUNT_PATH, ParentsPage.class);
        mountPage(ChildrenPage.MOUNT_PATH, ChildrenPage.class);
        mountPage(FaqPage.MOUNT_PATH, FaqPage.class);
        mountPage(TeachersPage.MOUNT_PATH, TeachersPage.class);
        mountPage(SubscribePage.MOUNT_PATH, SubscribePage.class);
        mountPage(LogoutPage.MOUNT_PATH, LogoutPage.class);
        mountPage(BaseWorktopPage.MOUNT_PATH, BaseWorktopPage.class);
        mountPage(SuccessPaymentPage.MOUNT_PATH, SuccessPaymentPage.class);
        mountPage(FailPaymentPage.MOUNT_PATH, FailPaymentPage.class);
        mountPage(com.eltiland.ui.payment.W1PaymentPage.MOUNT_PATH, W1PaymentPage.class);
        mountPage(WebinarPaymentPage.MOUNT_PATH, WebinarPaymentPage.class);
        mountPage(WebinarMultiplyPaymentPage.MOUNT_PATH, WebinarMultiplyPaymentPage.class);
        mountPage(WebinarsPage.MOUNT_PATH, WebinarsPage.class);
        mountPage(ProfileViewPage.MOUNT_PATH, ProfileViewPage.class);
        mountPage(CoursePage.MOUNT_PATH, CoursePage.class);
        mountPage(CoursePayPage.MOUNT_PATH, CoursePayPage.class);
        mountPage(CourseListPage.MOUNT_PATH, CourseListPage.class);
        mountPage(CourseListenersPage.MOUNT_PATH, CourseListenersPage.class);
        mountPage(AboutPage.MOUNT_PATH, AboutPage.class);
        mountPage(CourseContentPage.MOUNT_PATH, CourseContentPage.class);
        mountPage(VideoPage.MOUNT_PATH, VideoPage.class);
        mountPage(ForumPage.MOUNT_PATH, ForumPage.class);
        mountPage(IPadClubPage.MOUNT_PATH, IPadClubPage.class);
        mountPage(UnsubscribePage.MOUNT_PATH, UnsubscribePage.class);
        mountPage(ForumThreadPage.MOUNT_PATH, ForumThreadPage.class);
        mountPage(ForumMessagePage.MOUNT_PATH, ForumMessagePage.class);
        mountPage(MagazinePage.MOUNT_PATH, MagazinePage.class);
        mountPage(MagazineAboutPage.MOUNT_PATH, MagazineAboutPage.class);
        mountPage(MagazinePayPage.MOUNT_PATH, MagazinePayPage.class);
        mountPage(MagazineDownloadPage.MOUNT_PATH, MagazineDownloadPage.class);
        mountPage(RecordPaymentPage.MOUNT_PATH, RecordPaymentPage.class);
        mountPage(LibraryPage.MOUNT_PATH, LibraryPage.class);
        mountPage(LibraryAdminPage.MOUNT_PATH, LibraryAdminPage.class);
        mountPage(LibraryEditRecordPage.MOUNT_PATH, LibraryEditRecordPage.class);
        mountPage(GoogleDocViewPage.MOUNT_PATH, GoogleDocViewPage.class);
        mountPage(GooglePresViewPage.MOUNT_PATH, GooglePresViewPage.class);
        mountPage(GoogleDocEditPage.MOUNT_PATH, GoogleDocEditPage.class);
        mountPage(GooglePresEditPage.MOUNT_PATH, GooglePresEditPage.class);
        mountPage(RecordViewPage.MOUNT_PATH, RecordViewPage.class);
        mountPage(ImagePreviewPage.MOUNT_PATH, ImagePreviewPage.class);
        mountPage(TeachingPage.MOUNT_PATH, TeachingPage.class);
        mountPage(TrainingPage.MOUNT_PATH, TrainingPage.class);
        mountPage(CourseControlPage.MOUNT_PATH, CourseControlPage.class);
        mountPage(CourseNewContentPage.MOUNT_PATH, CourseNewContentPage.class);
        mountPage(CourseEditPage.MOUNT_PATH, CourseEditPage.class);
        mountPage(CourseItemPage.MOUNT_PATH, CourseItemPage.class);
        mountPage(CourseNewPage.MOUNT_PATH, CourseNewPage.class);
        mountPage(PaymentPage.MOUNT_PATH, PaymentPage.class);
        mountPage(PaymentSuccessPage.MOUNT_PATH, PaymentSuccessPage.class);
        mountPage(PaymentFailPage.MOUNT_PATH, PaymentFailPage.class);
        mountPage(PaymentProcessingPage.MOUNT_PATH, PaymentProcessingPage.class);
        mountPage(ResetPasswordPage.MOUNT_PATH, ResetPasswordPage.class);

        getSharedResources().add(ResourcesUtils.COURSE_CONTENT_FOLDER,
                new ContextRelativeResource(ResourcesUtils.IMAGE_COURSE_CONTENT_FOLDER_ITEM));
        getSharedResources().add(ResourcesUtils.COURSE_CONTENT_TEST,
                new ContextRelativeResource(ResourcesUtils.IMAGE_COURSE_CONTENT_TEST_ITEM));
        getSharedResources().add(ResourcesUtils.COURSE_CONTENT_DOCUMENT,
                new ContextRelativeResource(ResourcesUtils.IMAGE_COURSE_CONTENT_DOCUMENT_ITEM));
        getSharedResources().add(ResourcesUtils.COURSE_CONTENT_PARENT_CLOSED,
                new ContextRelativeResource(ResourcesUtils.IMAGE_COURSE_CONTENT_PARENT_CLOSED));
        getSharedResources().add(ResourcesUtils.COURSE_CONTENT_PARENT_OPEN,
                new ContextRelativeResource(ResourcesUtils.IMAGE_COURSE_CONTENT_PARENT_OPEN));
        getSharedResources().add(ResourcesUtils.COURSE_CONTENT_VIDEO,
                new ContextRelativeResource(ResourcesUtils.IMAGE_COURSE_CONTENT_VIDEO));
        getSharedResources().add(ResourcesUtils.COURSE_CONTENT_WEBINAR,
                new ContextRelativeResource(ResourcesUtils.IMAGE_COURSE_CONTENT_WEBINAR));
        getSharedResources().add(ResourcesUtils.COURSE_CONTENT_PRESENTATION,
                new ContextRelativeResource(ResourcesUtils.IMAGE_COURSE_CONTENT_PRESENTATION));
        getSharedResources().add(ResourcesUtils.COURSE_CONTENT_CONTROL,
                new ContextRelativeResource(ResourcesUtils.IMAGE_COURSE_CONTENT_CONTROL));


        /*
        * Next aliases used by brix.
        */
        mountPage("/imageUpload", UploadImagePage.class);
        mountPage("/admin", AdminPage.class);

        if (getConfigurationType() == RuntimeConfigurationType.DEVELOPMENT) {
            setDebugMode(true);
        }
    }

    private void setDebugMode(boolean isDebug) {
        getDebugSettings().setOutputMarkupContainerClassName(isDebug);
        getDebugSettings().setAjaxDebugModeEnabled(isDebug);
    }

    @Override
    protected Class<? extends AbstractAuthenticatedWebSession> getWebSessionClass() {
        return EltilandSession.class;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        currentlyOnlineClearTimer.cancel();
    }

    @Override
    protected Class<? extends WebPage> getSignInPageClass() {
        return LoginPage.class;
    }

    /**
     * Allow Brix to perform repository initialization
     */
    private void initializeRepository() {
        try {
            brix.initRepository();
        } finally {
            // cleanup any sessions we might have created
            cleanupSessionFactory();
        }
    }

    private void initDefaultWorkspace() {
        try {
            final String defaultState = getProperties().getWorkspaceDefaultState();
            final String wn = getProperties().getJcrDefaultWorkspace();
            final SitePlugin sp = SitePlugin.get(brix);

            if (!sp.siteExists(wn, defaultState)) {
                Workspace w = sp.createSite(wn, defaultState);
                JcrSession session = brix.getCurrentSession(w.getId());

                session.importXML("/", getClass().getResourceAsStream("workspace.xml"),
                        ImportUUIDBehavior.IMPORT_UUID_COLLISION_REPLACE_EXISTING);

                brix.initWorkspace(w, session);

                session.save();
            }
        } catch (Exception e) {
            throw new RuntimeException("Could not initialize jackrabbit workspace with Brix", e);
        }
    }

    private static class TempPropertiesResourceFactory extends PropertiesFactory {

        public TempPropertiesResourceFactory(IPropertiesFactoryContext context) {
            super(context);
            getPropertiesLoaders().add(new XmlFilePropertiesLoader("xml"));
        }

    }
}
