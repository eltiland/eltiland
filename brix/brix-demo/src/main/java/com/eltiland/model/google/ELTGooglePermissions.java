package com.eltiland.model.google;

/**
 * Structure for handling all Google Permissions data for Eltiland.
 *
 * @author Aleksey Plotnikov.
 */
public class ELTGooglePermissions {

    public enum ROLE {
        OWNER("owner"),
        READER("reader"),
        WRITER("writer");

        private String text;

        @Override
        public String toString() {
            return text;
        }

        private ROLE(String text) {
            this.text = text;
        }
    }

    public enum TYPE {
        USER("user"),
        GROUP("group"),
        DOMAIN("domain"),
        ANYONE("anyone");

        private String text;

        @Override
        public String toString() {
            return text;
        }

        private TYPE(String text) {
            this.text = text;
        }
    }

    private ROLE role;
    private TYPE type;
    private String value;

    public ROLE getRole() {
        return role;
    }

    public void setRole(ROLE role) {
        this.role = role;
    }

    public TYPE getType() {
        return type;
    }

    public void setType(TYPE type) {
        this.type = type;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public ELTGooglePermissions(ROLE role, TYPE type, String value) {
        this.role = role;
        this.type = type;
        this.value = value;
    }

    public ELTGooglePermissions(ROLE role, TYPE type) {
        this.role = role;
        this.type = type;
        this.value = "default";
    }
}
