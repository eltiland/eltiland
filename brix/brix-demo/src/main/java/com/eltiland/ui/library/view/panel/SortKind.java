package com.eltiland.ui.library.view.panel;

/**
 * Sort kind for library record list.
 *
 * @author Aleksey Plotnikov.
 */
public enum SortKind {
    DATE_ASC, DATE_DESC, NAME_ASC, NAME_DESC, RELEVANCE_ASC, RELEVANCE_DESC;

    @Override
    public String toString() {
        switch (this) {
            case DATE_ASC:
                return "Дата (сначала старые)";
            case DATE_DESC:
                return "Дата (сначала новые)";
            case NAME_ASC:
                return "Название (по возрастанию)";
            case NAME_DESC:
                return "Название (по убыванию)";
            case RELEVANCE_ASC:
                return "Релевантность (по возрастанию)";
            case RELEVANCE_DESC:
                return "Релевантность (по убыванию)";
            default:
                return "";
        }
    }

    public boolean isAsc() {
        return this.equals(DATE_ASC) || this.equals(NAME_ASC) || this.equals(RELEVANCE_ASC);
    }

    public String getField() {
        if (this.equals(DATE_ASC) || this.equals(DATE_DESC)) {
            return "publishedDate";
        } else if (this.equals(NAME_ASC) || this.equals(NAME_DESC)) {
            return "name";
        } else {
            return "relevance";
        }
    }

    public static SortKind fromStr(String str, boolean asc) {
        if (str.equals("name") && asc) {
            return NAME_ASC;
        } else if (str.equals("name") && !asc) {
            return NAME_DESC;
        } else if (str.equals("publishedDate") && asc) {
            return DATE_ASC;
        } else if (str.equals("publishedDate") && !asc) {
            return DATE_DESC;
        } else if (str.equals("relevance") && asc) {
            return RELEVANCE_ASC;
        } else if (str.equals("relevance") && !asc) {
            return RELEVANCE_DESC;
        } else {
            return null;
        }
    }
}
