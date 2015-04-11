package com.eltiland.ui.common.components;

import org.apache.wicket.validation.validator.PatternValidator;

/**
 * Collection of the common constants.
 */
public final class UIConstants {

    public static final int MAX_COUNT_SYMBOL_INTO_DESCRIPTION = 200;

    public static final int ROWS_PER_PAGE = 10;
    public static final int LOWER_ROWS_PER_PAGE = 4;
    public static final String NO_DATA_STRING = " ";

    public static final int DIALOG_BIG_WIDTH = 1064; //1024+40
    public static final int DIALOG_MEDIUM_WIDTH = 860; //820+40
    public static final int DIALOG_SMALL_WIDTH = 620;
    public static final int DIALOG_POPUP_WIDTH = 480;
    public static final int DIALOG_POPUP_WIDTH_SMALL = 325;


    public static final int DIALOG_MEDIUM_HEIGHT = 600;
    public static final int DIALOG_BIG_HEIGHT = 730;
    public static final int DIALOG_POPUP_HEIGHT = 350;

    public static final int ALERT_FADEOUT_TIME = 2000;
    public static final int ALERT_SHOW_TIME_SHORT = 5000;
    public static final int ALERT_SHOW_TIME_LONG = 60000;
    public static final int FILENAME_LENGTH = 12;
    public static final int KILOBYTE = 1024;
    public static final int MEGABYTE = KILOBYTE * KILOBYTE;
    public static final int GIGABYTE = MEGABYTE * KILOBYTE;

    public static final int SUGGEST_SIZE = 10;
    public static final int IMAGE_VIEW_MAX_HEIGHT = 800;
    public static final int MAX_AVATAR_FILE_SIZE_MB = 8;

    public static final String WEBSITE_TEMPLATE_WITH_PROTOCOL = "(http|ftp|https):\\/\\/[\\w\\-_]+(\\.[\\w\\-_]+)+([\\w\\-\\.,@?^=%&amp;:/~\\+#]*[\\w\\-\\@?^=%&amp;/~\\+#])?";

    public static final PatternValidator integerValidator = new PatternValidator("^[0-9]*$") {
        @Override
        protected String resourceKey() {
            return "IConverter.Integer";
        }
    };

    public static PatternValidator phoneNumberValidator = new PatternValidator("\\+?[\\d\\-\\s]{7,}") {
        @Override
        protected String resourceKey() {
            return "PhoneNumberValidator";
        }
    };

    public static PatternValidator skypeValidator = new PatternValidator("[a-zA-Z][a-zA-Z0-9\\.,\\-_]{5,31}") {
        @Override
        protected String resourceKey() {
            return "skypeValidator";
        }
    };


    public static PatternValidator websiteValidator = new PatternValidator("(((file|gopher|news|nntp|telnet|http|ftp|https|ftps|sftp)://)|(www\\.))+(([a-zA-Z0-9\\._-]+\\.[a-zA-Z]{2,6})|([0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}))(/[a-zA-Z0-9\\&amp;%_\\./-~-]*)?") {
        @Override
        protected String resourceKey() {
            return "websiteValidator";
        }
    };

    public static final String CLASS_NEWS_ITEM_ANNOUNCEMENT = "elti-news-item-announcement";
    public static final String CLASS_NEWS_ITEM_TITLE = "elti-news-item-title";
    public static final String CLASS_LINKBUTTON = "elti-linkbutton";
    public static final String CLASS_INDICATOR_PLACEHOLDER = "elti-indicator-placeholder";
    public static final String CLASS_INDICATOR = "elti-indicator";
    public static final String CLASS_DATE = "elti-date";
    public static final String CLASS_DATATABLE = "elti-datatable";
    public static final String CLASS_ITEMPANEL = "elti-item";
    public static final String CLASS_EDITABLE_LABEL = "elti-editable-label";
    public static final String CLASS_MULTI_SELECTOR = "elti-multi-selector";

    public static final String CLASS_CLICKABLE = "clickable";
    public static final String DATE_FORMAT = "dd.MM.yyyy";
    public static final String CSS_TOOLTIP_NORMAL = "normal";
    public static final String CSS_TOOLTIP_ERROR = "error";

    public static final String YOUTUBE_TAG = "http://www.youtube.com";
    public static final String VIDEO_MESSAGE = "Cooбщение содержит видео. Кликните, чтобы просмотреть его.";

    private UIConstants() {

    }

    public static String formatShortDescription(String description, int count) {
        String shortDescription = description;

        if (shortDescription.contains(YOUTUBE_TAG)) {
            return VIDEO_MESSAGE;
        }

        if (shortDescription.length() > count) {
            try {
                shortDescription =
                        shortDescription.substring(0, shortDescription.indexOf(" ", count));
            } catch (StringIndexOutOfBoundsException e) {
                shortDescription = shortDescription.substring(0, count);
            }
            shortDescription = shortDescription + " ...";
        }

        return shortDescription;
    }


    public static String formatFileSize(long sizeBytes) {
        double gigs = 1.0 * sizeBytes / GIGABYTE;
        double megas = 1.0 * sizeBytes / MEGABYTE;
        double kilos = 1.0 * sizeBytes / KILOBYTE;
        if (gigs > 1) {
            return String.format("%.2f GB", gigs);
        }
        if (megas > 1) {
            return String.format("%.2f MB", megas);
        }
        if (kilos > 1) {
            return String.format("%.2f KB", kilos);
        }
        return String.format("%d B", sizeBytes);

    }

    public static String shortenFileName(String fileName) {
        String extension;
        if (!fileName.contains(".")) {
            extension = "";
        } else {
            extension = fileName.substring(fileName.lastIndexOf('.'), fileName.length());
        }

        if ((fileName.length() - extension.length()) <= FILENAME_LENGTH) {
            return fileName;
        }
        return fileName.substring(0, FILENAME_LENGTH) + ".." + extension;
    }
}
