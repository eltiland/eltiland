package com.eltiland.ui.library;

/**
 * Enum for library record view kinds.
 *
 * @author Aleksey Plotnikov.
 */
public enum LibraryView {
    LIST, GALLERY, GRID;

    @Override
    public String toString() {
        switch (this) {
            case LIST:
                return "list";
            case GALLERY:
                return "gallery";
            case GRID:
                return "grid";
            default:
                return "";
        }
    }

    public static LibraryView fromStr(String str) {
        if (str == null) {
            return null;
        }
        switch (str) {
            case "list":
                return LIST;
            case "gallery":
                return GALLERY;
            case "grid":
                return GRID;
            default:
                return null;
        }
    }
}
