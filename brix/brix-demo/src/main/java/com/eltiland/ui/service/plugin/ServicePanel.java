package com.eltiland.ui.service.plugin;

import com.eltiland.bl.*;
import com.eltiland.bl.drive.GoogleDriveManager;
import com.eltiland.bl.impl.integration.IconsLoader;
import com.eltiland.bl.impl.integration.IndexCreator;
import com.eltiland.bl.user.UserManager;
import com.eltiland.bl.webinars.WebinarServiceManager;
import com.eltiland.exceptions.ConstraintException;
import com.eltiland.exceptions.EltilandManagerException;
import com.eltiland.exceptions.GoogleDriveException;
import com.eltiland.model.course.Course;
import com.eltiland.model.course.CourseSession;
import com.eltiland.model.course.test.TestCourseItem;
import com.eltiland.model.course.test.UserTestAttempt;
import com.eltiland.model.file.File;
import com.eltiland.model.google.ELTGooglePermissions;
import com.eltiland.model.google.GoogleDriveFile;
import com.eltiland.model.user.User;
import com.eltiland.ui.common.BaseEltilandPanel;
import com.eltiland.ui.common.components.behavior.ConfirmationDialogBehavior;
import com.eltiland.ui.common.components.button.EltiAjaxLink;
import com.eltiland.ui.common.components.dialog.ELTAlerts;
import com.eltiland.ui.common.components.file.ELTFilePanel;
import com.eltiland.ui.common.model.GenericDBListModel;
import com.eltiland.ui.google.buttons.GoogleUploadButton;
import com.eltiland.utils.DateUtils;
import com.eltiland.utils.MimeType;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AbstractPromptReceiver;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.injection.Injector;
import org.apache.wicket.model.IModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.brixcms.workspace.Workspace;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.*;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.List;

/**
 * Service function panel.
 *
 * @author Aleksey Plotnikov.
 */
public class ServicePanel extends BaseEltilandPanel<Workspace> {

    protected static final Logger LOGGER = LoggerFactory.getLogger(ServicePanel.class);

    @SpringBean
    private IconsLoader iconsLoader;
    @SpringBean
    private IndexCreator indexCreator;
    @SpringBean
    private GenericManager genericManager;
    @SpringBean
    private CourseUserDataManager courseUserDataManager;
    @SpringBean
    private FileManager fileManager;
    @SpringBean
    private UserManager userManager;
    @SpringBean
    private CourseManager courseManager;
    @SpringBean
    private EmailMessageManager emailMessageManager;
    @SpringBean(name = "webinarServiceV3Impl")
    private WebinarServiceManager webinarServiceManager;
    @SpringBean
    private GoogleDriveManager googleDriveManager;

    private static final JsonFactory JSON_FACTORY =
            JacksonFactory.getDefaultInstance();

    private static HttpTransport HTTP_TRANSPORT;

    private static final List<String> SCOPES =
            Arrays.asList(DriveScopes.DRIVE_METADATA_READONLY);

    private static FileDataStoreFactory DATA_STORE_FACTORY;

    private static final java.io.File DATA_STORE_DIR = new java.io.File("C:/tmp");

    protected ServicePanel(String id, IModel<Workspace> workspaceIModel) {
        super(id, workspaceIModel);
        Injector.get().inject(this);

        add(new EltiAjaxLink("createCourse") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                //

               // try {
                   /* HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
                    DATA_STORE_FACTORY = new FileDataStoreFactory(DATA_STORE_DIR);

                    InputStream in = new FileInputStream("C:/tmp/client_secret.json");
                    GoogleClientSecrets clientSecrets =
                            GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));


                    final GoogleCredential credential = new GoogleCredential.Builder().setTransport(HTTP_TRANSPORT).setJsonFactory(JSON_FACTORY)
                            .setClientSecrets(clientSecrets).build();

                    Drive drive = new Drive.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential)
                            .setHttpRequestInitializer(new HttpRequestInitializer() {
                                @Override
                                public void initialize(HttpRequest httpRequest) throws IOException {
                                    credential.initialize(httpRequest);
                                    httpRequest.setConnectTimeout(300 * 60000);
                                    httpRequest.setReadTimeout(300 * 60000);
                                }
                            }).build();

                    com.google.api.services.drive.model.File body = new com.google.api.services.drive.model.File();
                    body.setTitle("Test45");
                    body.setDescription("Test");
                    body.setMimeType("application/msword");
                    boolean convert = !(file.getMimeType().equals(MimeType.PDF_TYPE));
                    return insert(file, body, true);

//                    GoogleAuthorizationCodeFlow flow =
//                            new GoogleAuthorizationCodeFlow.Builder(
//                                    HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
//                                    .setDataStoreFactory(DATA_STORE_FACTORY)
//                                    .setAccessType("offline")
//                                    .build();
//                    Credential credential = new AuthorizationCodeInstalledApp(
//                            flow, new LocalServerReceiver()).authorize("user");
                    System.out.println(
                            "Credentials saved to " + DATA_STORE_DIR.getAbsolutePath());
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (GeneralSecurityException e) {
                    e.printStackTrace();
                }*/

                GoogleDriveFile driveFile = genericManager.getObject(GoogleDriveFile.class, (long) 79880);
                try {
                    String link = googleDriveManager.getWebContentLink(driveFile);
                } catch (GoogleDriveException e) {
                    e.printStackTrace();
                }

//                User user = userManager.getUserByEmail("eltroot1@gmail.com");
//
//                Course course = new Course();
//                course.setTraining(true);
//                course.setAuthor(user);
//                course.setCreationDate(DateUtils.getCurrentDate());
//                course.setName("Организация инклюзивного образования в дошкольной образовательной организации");
//                course.setStatus(false);
//
//                course.setPublished(false);
//                try {
//                    courseManager.createCourse(course);
//                } catch (EltilandManagerException e) {
//                    e.printStackTrace();
//                }
//
//                CourseSession session = new CourseSession();
//                session.setActive(true);
//                session.setStartDate(DateUtils.getCurrentDate());
//                session.setFinishDate(DateUtils.getCurrentDate());
//                session.setPrejoinDate(DateUtils.getCurrentDate());
//                session.setCourse(course);
//
//                try {
//                    genericManager.saveNew(session);
//                } catch (ConstraintException e) {
//                    e.printStackTrace();
//                }

            }
        }.add(new ConfirmationDialogBehavior()));


        add(new EltiAjaxLink("reloadButton") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                iconsLoader.reloadIcons();
                ELTAlerts.renderOKPopup(getString("successMessage"), target);
            }
        }.add(new ConfirmationDialogBehavior()));

        add(new EltiAjaxLink("recreateSearchIndexesButton") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                indexCreator.doRebuildIndex();
                ELTAlerts.renderOKPopup(getString("recreateSearchIndexesMessage"), target);
            }
        }.add(new ConfirmationDialogBehavior()));

        add(new EltiAjaxLink("createStandartUserData") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                for (Course course : genericManager.getEntityList(Course.class, "id")) {
                    try {
                        courseUserDataManager.createStandart(course);
                    } catch (EltilandManagerException e) {
                        e.printStackTrace();
                    }
                }

                ELTAlerts.renderOKPopup(getString("success"), target);
            }
        }.add(new ConfirmationDialogBehavior()));

        final ELTFilePanel filePanel = new ELTFilePanel("file", new GenericDBListModel<File>(File.class));
        add(filePanel);
        add(new EltiAjaxLink("save") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                List<Integer> userIds = Arrays.asList(66855, 67125, 67126, 67124, 16510, 67289, 63815, 67284);
                for( Integer id : userIds ) {
                    UserTestAttempt attempt = new UserTestAttempt();
                    attempt.setUser(userManager.getUserById((long)id));
                    attempt.setTest(genericManager.getObject(TestCourseItem.class, (long) 68530));
                    attempt.setAttemptCount(1);
                    attempt.setAttemptLimit(3);
                    attempt.setCompleted(true);
                    LOGGER.info(String.format("Created user %d", id));
                    try {
                        genericManager.saveNew(attempt);
                    } catch (ConstraintException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        add(new EltiAjaxLink("webinarSend") {
            @Override
            public void onClick(AjaxRequestTarget target) {
            }
        });

        add(new EltiAjaxLink("createProperty") {
            @Override
            public void onClick(AjaxRequestTarget target) {
            /*    Course course = new Course();
                course.setName("Организация инклюзивного образования в дошкольной образовательной организации");
                course.setStatus(false);
                course.setPublished(false);
                course.setAutoJoin(true);

                try {
                    genericManager.saveNew(course);
                    ELTAlerts.renderOKPopup("Created OK", target);
                } catch (ConstraintException e) {
                    e.printStackTrace();
                }
          /*      Property property = new Property();
                property.setValue("Любое использование материала, полностью или частично, без разрешения правообладателя, запрещается и влечет наказание в соответствии с Уголовным кодексом РФ (ст. 146, 147, 180)");
                property.setProperty("course_author_warning");

                try {
                    genericManager.saveNew(property);
                    ELTAlerts.renderOKPopup("Created OK", target);
                } catch (ConstraintException e) {
                    e.printStackTrace();
                }*/

            //    GoogleDriveFile driveFile = genericManager.getObject(GoogleDriveFile.class, (long) 73783);

                Document doc = null, doc2 = null;
                try {
                    doc = Jsoup.connect("https://docs.google.com/document/d/1ZdqwMGvLciS0mAr2AzWFhBJ9Pecuq-uHLYlhsskvxZQ/preview").get();
                    doc2 = Jsoup.connect("https://docs.google.com/document/d/1ZdqwMGvLciS0mAr2AzWFhBJ9Pecuq-uHLYlhsskvxZQ/edit").get();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                String text = doc.body().text();


        /*        try {
                    googleDriveManager.getWebContentLink(driveFile);
                } catch (GoogleDriveException e) {
                    e.printStackTrace();
                }*/
            }
        }.add(new ConfirmationDialogBehavior()));

     /*   add(new EltiAjaxLink("fixGoogle") {
            @Override
            public void onClick(AjaxRequestTarget ajaxRequestTarget) {
                GoogleDriveFile driveFile = genericManager.getObject(GoogleDriveFile.class, (long) 30561);
                try {
                    googleDriveManager.insertPermission(driveFile, new ELTGooglePermissions(ELTGooglePermissions.ROLE.OWNER, ELTGooglePermissions.TYPE.USER, "eltiland.portal@gmail.com"));
                    googleDriveManager.publishDocument(driveFile);
                    int a = 0;
                } catch (GoogleDriveException e) {
                    e.printStackTrace();
                }
            }
        });*/

        add(new GoogleUploadButton("fixGoogle") {
            @Override
            public void onClick(GoogleDriveFile gFile) {

            }

            @Override
            public List<String> getAvailibleMimeTypes() {
                return MimeType.getDocumentTypes();
            }
        });

//        Form form = new Form("form");
//        add(form);
//
//        final FileUploadField uploadField = new FileUploadField("filePanel");
//        form.add(uploadField);
//        form.add(new EltiAjaxSubmitLink("submit") {
//            @Override
//            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
//                uploadField.getFileUpload().get
//            }
//        });
    }
}
