package com.eltiland.bl.impl.integration;

import com.eltiland.bl.GenericManager;
import com.eltiland.bl.validators.FileValidator;
import com.eltiland.model.file.File;
import com.eltiland.utils.UrlUtils;
import org.apache.commons.io.IOUtils;
import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.web.context.support.ServletContextResource;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

/**
 * @author knorr
 * @version 1.0
 * @since 8/6/12
 */
public class IconsLoader {

    private static final Logger LOGGER = LoggerFactory.getLogger(IconsLoader.class);

    @Autowired
    private FileValidator fileValidator;

    @Autowired
    private SessionFactory sessionFactory;
    @Autowired
    private GenericManager genericManager;

    @Autowired
    private ApplicationContext context;

    public void reloadIcons() {
        Session session = null;
        try {
            session = sessionFactory.openSession();

            //get set on all standard icons name (enum name)
            List<String> standardIconsFromUrlUtilsNameList = new ArrayList<>();
            for (UrlUtils.StandardIcons icon : UrlUtils.StandardIcons.values()) {
                standardIconsFromUrlUtilsNameList.add(icon.name());
            }

            //load standard icons from DB
            session.beginTransaction();
            Criteria criteria = session.createCriteria(File.class)
                    .add(Restrictions.in("name", standardIconsFromUrlUtilsNameList))
                    .setFetchMode("previewBody", FetchMode.JOIN)
                    .setFetchMode("body", FetchMode.JOIN);
            List<File> standardIconsFromDBList = criteria.list();
            session.getTransaction().commit();

            //iterate over files from DISK and update it into DB
            for (UrlUtils.StandardIcons icon : UrlUtils.StandardIcons.values()) {
                //find related file icon from DB
                File fileFromDB = null;
                for (File file : standardIconsFromDBList) {
                    if (file.getName().equals(icon.name())) {
                        fileFromDB = file;
                        break;
                    }
                }

                if (fileFromDB == null) {
                    throw new IllegalStateException("Some of standard icons was not founded in DB! \n"
                            + "Icon " + icon.name() + " was not founded!");
                }

                //find related file icon from DISK
                Resource resource = context.getResource(icon.getPath());
                if (resource == null) {
                    throw new IllegalStateException("Some of standard icons was not loaded from DISK correct! \n"
                            + "Icon file " + icon.getPath() + " was not founded!");
                }

                //update existed file from DISK to DB
                try {
                    fileFromDB.setType(URLConnection.getFileNameMap().getContentTypeFor(resource.getFilename())); //bad solution
                    fileFromDB.setSize(resource.getFile().length());
                    fileFromDB.getPreviewBody().setFilename(((ServletContextResource) resource).getPathWithinContext().substring(1));

                    FileInputStream fileInputStream = new FileInputStream(resource.getFile());
                    fileFromDB.getBody().setBody(IOUtils.toByteArray(fileInputStream));
                    fileInputStream.close();
                } catch (IOException e) {
                    throw new RuntimeException("Error during processing standard icon file!", e);
                }

                session.beginTransaction();
                session.update(fileFromDB.getPreviewBody());
                session.update(fileFromDB.getBody());
                session.update(fileFromDB);
                session.getTransaction().commit();
            }
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }
}