package com.eltiland.bl.impl.drive;

import com.eltiland.bl.FileManager;
import com.eltiland.bl.GenericManager;
import com.eltiland.bl.drive.ContentManager;
import com.eltiland.bl.drive.GoogleDriveManager;
import com.eltiland.bl.impl.ManagerImpl;
import com.eltiland.bl.impl.integration.FileUtility;
import com.eltiland.exceptions.ConstraintException;
import com.eltiland.exceptions.EltilandManagerException;
import com.eltiland.exceptions.GoogleDriveException;
import com.eltiland.model.file.File;
import com.eltiland.model.google.Content;
import com.eltiland.model.google.ELTGoogleFile;
import com.eltiland.model.google.ELTGooglePermissions;
import com.eltiland.model.google.GoogleDriveFile;
import com.eltiland.utils.MimeType;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.http.*;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.ParentReference;
import com.google.api.services.drive.model.Permission;
import com.google.api.services.drive.model.Revision;
import com.google.api.services.drive.model.RevisionList;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.wicket.util.resource.IResourceStream;
import org.apache.wicket.util.resource.ResourceStreamNotFoundException;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.*;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Properties;

/**
 * Google Drive API manager.
 *
 * @author Aleksey Plotnikov.
 */
@Component
public class GoogleDriveManagerImpl extends ManagerImpl implements GoogleDriveManager {

    @Autowired
    @Qualifier("eltilandProperties")
    private Properties eltilandProps;

    @Autowired
    private GenericManager genericManager;
    @Autowired
    private FileUtility fileUtility;
    @Autowired
    private FileManager fileManager;
    @Autowired
    private ContentManager contentManager;

    private Drive authorize() throws GoogleDriveException {
        HttpTransport httpTransport = new NetHttpTransport();
        JsonFactory jsonFactory = new JacksonFactory();

        try {
            final GoogleCredential credential = new GoogleCredential.Builder()
                    .setTransport(httpTransport)
                    .setJsonFactory(jsonFactory)
                    .setServiceAccountId(eltilandProps.getProperty("gdrive.accountId"))
                    .setServiceAccountScopes(Arrays.asList(DriveScopes.DRIVE))
                    .setServiceAccountPrivateKeyFromP12File(
                            new java.io.File(eltilandProps.getProperty("gdrive.keyfile")))
                    .build();
            return new Drive.Builder(httpTransport, jsonFactory, credential)
                    .setHttpRequestInitializer(new HttpRequestInitializer() {
                        @Override
                        public void initialize(HttpRequest httpRequest) throws IOException {
                            credential.initialize(httpRequest);
                            httpRequest.setConnectTimeout(300 * 60000);
                            httpRequest.setReadTimeout(300 * 60000);
                        }
                    }).build();
        } catch (IOException | GeneralSecurityException e) {
            throw new GoogleDriveException(GoogleDriveException.ERROR_AUTH, e);
        }
    }

    private com.google.api.services.drive.model.File getFile(Drive drive, GoogleDriveFile file)
            throws GoogleDriveException {
        try {
            return drive.files().get(file.getGoogleId()).execute();
        } catch (IOException e) {
            throw new GoogleDriveException(GoogleDriveException.ERROR_GETFILE, e);
        }
    }

    @Transactional
    private GoogleDriveFile insert(
            ELTGoogleFile file, com.google.api.services.drive.model.File body, boolean isConvert)
            throws GoogleDriveException {
        try {
            File tFile = fileManager.getFileById(file.getFile().getId());
            IResourceStream resource = fileUtility.getFileResource(tFile.getBody().getHash());
            AbstractInputStreamContent mediaContent =
                    new InputStreamContent(file.getMimeType(), resource.getInputStream());

            Drive drive = authorize();
            com.google.api.services.drive.model.File googleFile =
                    drive.files().insert(body, mediaContent).setConvert(isConvert).execute();

            GoogleDriveFile gFile = new GoogleDriveFile();
            gFile.setGoogleId(googleFile.getId());
            gFile.setMimeType(file.getMimeType());
            genericManager.saveNew(gFile);
            return gFile;
        } catch (IOException | ConstraintException | ResourceStreamNotFoundException e) {
            throw new GoogleDriveException(GoogleDriveException.ERROR_INSERT, e);
        }
    }

    @Override
    @Transactional
    public GoogleDriveFile insertFile(ELTGoogleFile file) throws GoogleDriveException {
        com.google.api.services.drive.model.File body = new com.google.api.services.drive.model.File();
        body.setTitle(file.getName());
        body.setDescription(file.getDescription());
        body.setMimeType(file.getMimeType());
        boolean convert = !(file.getMimeType().equals(MimeType.PDF_TYPE));
        return insert(file, body, convert);
    }

    @Override
    @Transactional
    public GoogleDriveFile insertFile(ELTGoogleFile file, GoogleDriveFile folder) throws GoogleDriveException {
        if (folder == null) {
            return insertFile(file);
        }
        com.google.api.services.drive.model.File body = new com.google.api.services.drive.model.File();
        body.setTitle(file.getName());
        body.setDescription(file.getDescription());
        body.setMimeType(file.getMimeType());
        body.setParents(Arrays.asList(new ParentReference().setId(folder.getGoogleId())));
        boolean convert = !(file.getMimeType().equals(MimeType.PDF_TYPE));
        return insert(file, body, convert);
    }

    @Override
    @Transactional
    public GoogleDriveFile insertFolder(String name) throws GoogleDriveException {
        com.google.api.services.drive.model.File body = new com.google.api.services.drive.model.File();
        body.setTitle(name);
        body.setDescription(name);
        body.setMimeType(MimeType.GFOLDER_TYPE);

        try {
            Drive drive = authorize();
            com.google.api.services.drive.model.File googleFile =
                    drive.files().insert(body).execute();
            GoogleDriveFile gFile = new GoogleDriveFile();
            gFile.setGoogleId(googleFile.getId());
            gFile.setMimeType(MimeType.GFOLDER_TYPE);
            genericManager.saveNew(gFile);
            return gFile;
        } catch (GoogleDriveException | IOException | ConstraintException e) {
            throw new GoogleDriveException(GoogleDriveException.ERROR_CREATE_FOLDER, e);
        }
    }

    @Override
    @Transactional
    public void deleteFile(GoogleDriveFile file) throws GoogleDriveException {
        try {
            Drive drive = authorize();
            drive.files().delete(file.getGoogleId()).execute();

            genericManager.delete(file);
        } catch (IOException | EltilandManagerException e) {
            throw new GoogleDriveException(GoogleDriveException.ERROR_DELETE, e);
        }
    }

    @Override
    public GoogleDriveFile createEmptyDoc(String name, GoogleDriveFile.TYPE type) throws GoogleDriveException {

        long fileId = 0;
        if (type.equals(GoogleDriveFile.TYPE.DOCUMENT)) {
            fileId = new Long(eltilandProps.getProperty("gdrive.doc.empty"));
        } else if (type.equals(GoogleDriveFile.TYPE.PRESENTATION)) {
            fileId = new Long(eltilandProps.getProperty("gdrive.pres.empty"));
        }

        File emptyDocFile = genericManager.getObject(File.class, fileId);
        if (emptyDocFile != null) {
            GoogleDriveFile gFile = insertFile(new ELTGoogleFile(emptyDocFile, name, name, emptyDocFile.getType()));
            publishDocument(gFile);
            insertPermission(gFile,
                    new ELTGooglePermissions(ELTGooglePermissions.ROLE.WRITER, ELTGooglePermissions.TYPE.ANYONE));
            return gFile;
        } else {
            throw new GoogleDriveException(GoogleDriveException.ERROR_NO_EMPTY_FILE);
        }
    }

    @Override
    public InputStream downloadFile(GoogleDriveFile file) throws GoogleDriveException {
        return download(file, file.getMimeType());
    }

    @Override
    public InputStream downloadFileAsPDF(GoogleDriveFile file) throws GoogleDriveException {
        return download(file, MimeType.PDF_TYPE);
    }

    @Override
    @Transactional(rollbackFor = GoogleDriveException.class)
    public void cacheFile(GoogleDriveFile file) throws GoogleDriveException {
        InputStream fileStream = download(file, MimeType.HTML_TYPE);
        if (fileStream == null) {
            throw new GoogleDriveException(GoogleDriveException.EROOR_CACHING_FORMAT);
        }

        BufferedReader br = null;
        StringBuilder sb = new StringBuilder();

        String line;
        try {
            br = new BufferedReader(new InputStreamReader(fileStream));
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
        } catch (IOException e) {
            throw new GoogleDriveException(GoogleDriveException.EROOR_CACHING, e);
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    throw new GoogleDriveException(GoogleDriveException.EROOR_CACHING, e);
                }
            }
        }

        String stContent = sb.toString();

        genericManager.initialize(file, file.getContent());
        boolean toCreate = file.getContent() == null;
        Content content = (toCreate) ? new Content() : file.getContent();

        content.setContent(stContent);
        if (toCreate) {
            contentManager.create(content);
        } else {
            contentManager.update(content);
        }

        file.setContent(content);
        try {
            genericManager.update(file);
        } catch (ConstraintException e) {
            throw new GoogleDriveException(GoogleDriveException.EROOR_CACHING, e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<GoogleDriveFile> getFilesToCache() {
        Criteria criteria = getCurrentSession().createCriteria(GoogleDriveFile.class);
        criteria.add(Restrictions.in("mimeType", MimeType.getWordDocumentTypes()));
        return criteria.list();
    }

    @Override
    public void insertPermission(GoogleDriveFile file, ELTGooglePermissions permission) throws GoogleDriveException {
        Drive drive = authorize();
        try {
            Permission newPermission = new Permission();
            newPermission.setValue(permission.getValue());
            newPermission.setType(permission.getType().toString());
            newPermission.setRole(permission.getRole().toString());
            drive.permissions().insert(file.getGoogleId(), newPermission).execute();
        } catch (IOException e) {
            throw new GoogleDriveException(GoogleDriveException.ERROR_PERM, e);
        }
    }

    @Override
    public void publishDocument(GoogleDriveFile file) throws GoogleDriveException {
        Drive drive = authorize();
        try {
            RevisionList list = drive.revisions().list(file.getGoogleId()).execute();

            List<Revision> revisions = list.getItems();
            Revision revision = revisions.get(list.getItems().size() - 1);
            String revisionId = revision.getId();

            Revision revision2 = drive.revisions().get(file.getGoogleId(), revisionId).execute();
            revision2.setPublished(true);
            drive.revisions().update(file.getGoogleId(), revisionId, revision2).execute();
        } catch (IOException e) {
            throw new GoogleDriveException(GoogleDriveException.ERROR_PUBLISH, e);
        }

        // insert permissions to access the document.
    /*    insertPermission(file, new ELTGooglePermissions(
                ELTGooglePermissions.ROLE.WRITER, ELTGooglePermissions.TYPE.ANYONE));
     /*   insertPermission(file, new ELTGooglePermissions(
                ELTGooglePermissions.ROLE.WRITER, ELTGooglePermissions.TYPE.USER,
                eltilandProps.getProperty("gdrive.mail")));*/
    }

    private InputStream download(GoogleDriveFile file, String type) throws GoogleDriveException {
        Drive drive = authorize();
        com.google.api.services.drive.model.File gFile = getFile(drive, file);

        if (type.equals(MimeType.DOC_TYPE)) {
            type = MimeType.DOCX_TYPE;
        }
        if (type.equals(MimeType.PPT_TYPE)) {
            type = MimeType.PPTX_TYPE;
        }

        String url;
        if (file.getMimeType().equals(MimeType.PDF_TYPE)) {
            url = gFile.getWebContentLink();
        } else if (Objects.equals(type, MimeType.HTML_TYPE)) {
            url = gFile.getExportLinks().get(type);
        } else {
            url = ((gFile.getDownloadUrl() != null) && (gFile.getDownloadUrl().length() > 0))
                    ? gFile.getDownloadUrl() : gFile.getExportLinks().get(type);
        }

        try {
            HttpClient client = new HttpClient();
            GetMethod method = new GetMethod(url);
            int statusCode = client.executeMethod(method);
            if (statusCode != 200) {
                return null;
            }
            return new ByteArrayInputStream(method.getResponseBody());
        } catch (IOException e) {
            throw new GoogleDriveException(GoogleDriveException.ERROR_DOWNLOAD, e);
        }
    }
}
