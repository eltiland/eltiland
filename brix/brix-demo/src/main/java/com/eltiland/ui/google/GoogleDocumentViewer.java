package com.eltiland.ui.google;

import com.eltiland.bl.GenericManager;
import com.eltiland.model.google.GoogleDriveFile;
import com.eltiland.ui.common.BaseEltilandPanel;
import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Panel for output (read-only mode) Google Drive document.
 *
 * @author Aleksey Plotnikov.
 */
class GoogleDocumentViewer extends BaseEltilandPanel<GoogleDriveFile> {

    @SpringBean
    private GenericManager genericManager;

    private static final Logger LOGGER = LoggerFactory.getLogger(GoogleDocumentViewer.class);

    private String googleResponse = "";

    public GoogleDocumentViewer(String id, IModel<GoogleDriveFile> googleDriveFileIModel) {
        super(id, googleDriveFileIModel);

        genericManager.initialize(getModelObject(), getModelObject().getContent());

        if( getModelObject().getContent() == null ) {
            String link = "https://docs.google.com/document/d/" + getModelObject().getGoogleId() + "/pub?embedded=true";
            try {
                URL url = new URL(link);
                BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    googleResponse += inputLine;
                }
            } catch (IOException e) {
                LOGGER.error("Error while getting response from Google Drive", e);
                throw new WicketRuntimeException("Error while getting response from Google Drive", e);
            }
        } else {
            googleResponse = getModelObject().getContent().getContent();
        }

        String contentString = parseContent("body");
        contentString = contentString.replace("body", "div");

        // correct images URL
        contentString = contentString.replace("pubimage?id",
                "https://docs.google.com/document/d/" + getModelObject().getGoogleId() + "/pubimage?id");

        Label label = new Label("content", contentString);
        label.setEscapeModelStrings(false);
        label.setMarkupId("gContainer");
        add(label);
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
        response.renderString(parseContent("style"));
        response.renderOnDomReadyJavaScript("$(\"#gContainer\").children('div').removeClass()");
        response.renderOnDomReadyJavaScript("filterGoogleStyles()");
    }

    private String parseContent(String tag) {
        Pattern contentPattern = Pattern.compile("<" + tag + "[\\s\\S]*?<\\/" + tag + ">");
        Matcher m = contentPattern.matcher(googleResponse);
        String contentString = null;
        if (m.find()) {
            contentString = m.group();
        }
        return contentString;
    }
}
