package com.eltiland.model;

/**
 * User: vadim.didenko
 * Date: 10.09.12
 * Time: 23:01
 * File attached to e-mail
 */
public class FileContent {
    private String type;
    private String path;
    private byte[] content;

    public String getType() {
        return type;
    }

    public String getPath() {
        return path;
    }

    public byte[] getContent() {
        return content;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }
}
