package com.eltiland.model;

import com.eltiland.model.file.File;

import javax.mail.internet.InternetAddress;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * @author knorr
 * @version 1.0
 * @since 7/24/12
 */
public class EmailMessage {

    private String subject;

    private String text;
    private List<FileContent> fileContentList=new ArrayList<>();
    private List<File> fileList = new ArrayList<>();
    private InputStream fileStream;

    private InternetAddress sender = new InternetAddress();

    private List<InternetAddress> recipients = new ArrayList<InternetAddress>();

    private List<InternetAddress> cc = new ArrayList<InternetAddress>();

    private List<InternetAddress> dcc = new ArrayList<InternetAddress>();

    public List<InternetAddress> getDcc() {
        return dcc;
    }

    public void setDcc(List<InternetAddress> dcc) {
        this.dcc = dcc;
    }

    public List<InternetAddress> getCc() {
        return cc;
    }

    public void setCc(List<InternetAddress> cc) {
        this.cc = cc;
    }

    public List<InternetAddress> getRecipients() {
        return recipients;
    }

    public void setRecipients(List<InternetAddress> recipients) {
        this.recipients = recipients;
    }

    public InternetAddress getSender() {
        return sender;
    }

    public void setSender(InternetAddress sender) {
        this.sender = sender;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public List<FileContent> getFileContentList() {
        return fileContentList;
    }

    public void setFileContentList(List<FileContent> fileContentList) {
        this.fileContentList = fileContentList;
    }
}
