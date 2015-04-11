package com.eltiland.ui.subscribe.plugin.tab.email;

import brix.tinymce.TinyMceEnabler;
import com.eltiland.bl.EmailManager;
import com.eltiland.exceptions.EltilandManagerException;
import com.eltiland.exceptions.EmailException;
import com.eltiland.model.subscribe.Email;
import com.eltiland.ui.common.BaseEltilandPanel;
import com.eltiland.ui.common.CKEditorFull;
import com.eltiland.ui.common.components.button.EltiAjaxSubmitLink;
import com.eltiland.ui.common.components.dialog.Dialog;
import com.eltiland.ui.common.components.dialog.ELTAlerts;
import com.eltiland.ui.common.components.dialog.ELTDialogPanel;
import com.eltiland.ui.common.components.dialog.callback.IDialogNewCallback;
import com.eltiland.ui.common.components.dialog.callback.IDialogUpdateCallback;
import com.eltiland.ui.common.components.textfield.ELTTextArea;
import com.eltiland.ui.common.model.GenericDBModel;
import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Email property panel.
 *
 * @author Aleksey Plotnikov.
 */
public class EmailPropertyPanel extends ELTDialogPanel
        implements IDialogNewCallback<Email>, IDialogUpdateCallback<Email> {

    @SpringBean
    private EmailManager emailManager;
    @SpringBean(name = "eltilandProperties")
    private Properties eltilandProps;

    private IModel<Email> mailModel;

    private IDialogNewCallback.IDialogActionProcessor<Email> newCallback;
    private IDialogUpdateCallback.IDialogActionProcessor<Email> updateCallback;

    protected static final Logger LOGGER = LoggerFactory.getLogger(EmailPropertyPanel.class);

    private ELTTextArea headerField = new ELTTextArea(
            "headerField", new ResourceModel("headerLabel"), new Model<String>(), true) {
        @Override
        protected boolean isFillToWidth() {
            return true;
        }
    };

    private CKEditorFull contentField;

    public EmailPropertyPanel(String id, Dialog dialog) {
        super(id, new GenericDBModel<>(Email.class));

        contentField = new CKEditorFull("contentField", dialog);

        form.add(headerField);
        form.add(contentField);

        headerField.addMaxLengthValidator(100);
    }

    @Override
    protected String getHeader() {
        return mailModel == null ? getString("addEmailHeader") : getString("modifyEmailHeader");
    }

    @Override
    protected List<EVENT> getActionList() {
        return Arrays.asList(EVENT.Add, EVENT.Save);
    }

    @Override
    protected boolean actionSelector(EVENT event) {
        switch (event) {
            case Add:
                return mailModel == null;
            case Save:
                return mailModel != null;
            default:
                return false;
        }
    }

    @Override
    protected void eventHandler(EVENT event, AjaxRequestTarget target) {
        Email email;
        switch (event) {
            case Add:
                email = new Email();
                email.setHeader(headerField.getModelObject());
                email.setContent(formatContent(contentField.getData()));
                try {
                    emailManager.createEmail(email);
                } catch (EmailException e) {
                    ELTAlerts.renderErrorPopup(e.getMessage(), target);
                    return;
                }
                newCallback.process(new GenericDBModel<>(Email.class, email), target);

                break;
            case Save:
                email = mailModel.getObject();
                email.setHeader(headerField.getModelObject());
                email.setContent(formatContent(contentField.getData()));
                try {
                    email = emailManager.updateEmail(email);
                } catch (EmailException e) {
                    ELTAlerts.renderErrorPopup(e.getMessage(), target);
                    return;
                }
                updateCallback.process(new GenericDBModel<>(Email.class, email), target);

                break;
        }
    }

    public void initEditMode(Email email) {
        mailModel = new GenericDBModel<>(Email.class, email);
        headerField.setModelObject(email.getHeader());
        contentField.setData(email.getContent());
    }

    public void initCreateMode() {
        mailModel = null;
        headerField.setModelObject(null);
        contentField.setData(null);
    }


    @Override
    public void setNewCallback(IDialogNewCallback.IDialogActionProcessor<Email> callback) {
        this.newCallback = callback;
    }

    @Override
    public void setUpdateCallback(IDialogUpdateCallback.IDialogActionProcessor<Email> callback) {
        this.updateCallback = callback;
    }

    private String formatContent(String content) {
        HTMLLinkExtractor extractor = new HTMLLinkExtractor();
        Vector<HtmlLink> links = extractor.grabHTMLLinks(content);

        String homePage = eltilandProps.getProperty("application.base.url");
        String result = content;

        for (HtmlLink link : links) {
            if (!(link.getLink().contains("http://"))) {
                String oldLink = "<a href=\"" + link.getLink() + "\">" + link.getLinkText() + "</a>";
                String newLink = "<a href=\"" + homePage + "/" + link.getLink() + "\">" + link.getLinkText() + "</a>";
                result = result.replace(oldLink, newLink);
            }
        }

        return result;
    }

    class HTMLLinkExtractor {

        private Pattern patternTag, patternLink;
        private Matcher matcherTag, matcherLink;

        private static final String HTML_A_TAG_PATTERN = "(?i)<a([^>]+)>(.+?)</a>";
        private static final String HTML_A_HREF_TAG_PATTERN =
                "\\s*(?i)href\\s*=\\s*(\"([^\"]*\")|'[^']*'|([^'\">\\s]+))";


        public HTMLLinkExtractor() {
            patternTag = Pattern.compile(HTML_A_TAG_PATTERN);
            patternLink = Pattern.compile(HTML_A_HREF_TAG_PATTERN);
        }

        /**
         * Validate html with regular expression
         *
         * @param html html content for validation
         * @return Vector links and link text
         */
        public Vector<HtmlLink> grabHTMLLinks(final String html) {

            Vector<HtmlLink> result = new Vector<HtmlLink>();

            matcherTag = patternTag.matcher(html);

            while (matcherTag.find()) {

                String href = matcherTag.group(1); // href
                String linkText = matcherTag.group(2); // link text

                matcherLink = patternLink.matcher(href);

                while (matcherLink.find()) {

                    String link = matcherLink.group(1); // link
                    HtmlLink obj = new HtmlLink();
                    obj.setLink(link);
                    obj.setLinkText(linkText);

                    result.add(obj);

                }

            }

            return result;

        }
    }

    class HtmlLink {

        String link;
        String linkText;

        HtmlLink() {
        }

        @Override
        public String toString() {
            return new StringBuffer("Link : ").append(this.link)
                    .append(" Link Text : ").append(this.linkText).toString();
        }

        public String getLink() {
            return link;
        }

        public void setLink(String link) {
            this.link = replaceInvalidChar(link);
        }

        public String getLinkText() {
            return linkText;
        }

        public void setLinkText(String linkText) {
            this.linkText = linkText;
        }

        private String replaceInvalidChar(String link) {
            link = link.replaceAll("'", "");
            link = link.replaceAll("\"", "");
            return link;
        }

    }
}
