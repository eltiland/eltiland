package com.eltiland.utils;

import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.brixcms.plugin.site.SitePlugin;

/**
 * @author Igor Cherednichenko
 * @version 1.0
 */
public class UrlUtils {

    public static final String LOGIN_PAGE_MOUNT_PATH = "/login";
    public static final String RESET_PAGE_MOUNT_PATH = "/reset";
    public static final String UNSUBSCRIBE_PAGE_MOUNT_PATH = "/unsubscribe";
    public static final String REGISTER_PAGE_MOUNT_PATH = "/register";
    public static final String WEBINAR_MPAYMENT_PATH = "/webinarMPayment";
    public static final String MAGAZINE_DOWNLOAD_LINK = "/downloadMagazine";
    public static final String PAYMENT_LINK = "/payment";

    public static final String HANOI_GAME_PATH = "/static/flash/towers_of_hanoi.swf";
    public static final String VOLK_KOZA_KAPUSTA_GAME_PATH = "/static/flash/volk_koza_kapusta.swf";
    public static final String FROGS_GAME = "/static/flash/frogs.swf";

    public static final String SECRET_CODE_PARAMETER_NAME = "secret";
    public static final String RESET_CODE_PARAMETER_NAME = "reset";
    public static final String DLINK_PARAMETER_NAME = "id";
    public static final String UNSUBSCRIBE_CODE_PARAMETER_NAME = "unscb";
    public static final String BRIX_PAGE_ROOT = "/CMS";
    public static final String BRIX_PANEL_ROOT = "/CMS-PANELS";

    public static final String PAYMENT_CODE_PARAMETER_NAME = "payment";

    public static String urlForPage(Class<? extends WebPage> pageClass, PageParameters parameters) {
        return RequestCycle.get().urlFor(pageClass, parameters).toString();
    }

    public static String createBrixPathForPage(PageParameters params) {
        StringBuilder builder = new StringBuilder(SitePlugin.get().getSiteRootPath());
        builder.append(BRIX_PAGE_ROOT);

        for (int i = 0; i < params.getIndexedCount(); i++) {
            builder.append("/").append(params.get(i));
        }
        return builder.toString();
    }

    public static String createBrixPathForPanel(String relPath) {
        StringBuilder builder = new StringBuilder(SitePlugin.get().getSiteRootPath());
        builder.append(BRIX_PANEL_ROOT);
        if (!relPath.startsWith("/")) {
            builder.append("/");
        }
        builder.append(relPath);
        return builder.toString();
    }

    public static String createBrixPathForPage(String relPath) {
        StringBuilder builder = new StringBuilder(SitePlugin.get().getSiteRootPath());
        builder.append(BRIX_PAGE_ROOT);
        if (!relPath.startsWith("/")) {
            builder.append("/");
        }
        builder.append(relPath);
        return builder.toString();
    }


    public static enum StandardIcons {

        ICONS_DEFAULT_PEI("static/images/icons/thumb/default_pei.png"),
        ICONS_DEFAULT_PARENT("static/images/icons/thumb/default_parent.png"),
        ICONS_DEFAULT_CHILD("static/images/icons/thumb/default_child.png"),
        ICONS_DEFAULT_NO_ACTIVE_CHILD("static/images/icons/thumb/default_child_noactive.png"),
        ICONS_DEFAULT_COURSE("static/images/course/course-standart-small-icon.png"),

        /**
         * Records
         */
        ICONS_SIMPLE_RECORD("static/images/icons/thumb/record.png"),
        ICONS_ARTICLE("static/images/icons/thumb/article.png"),
        ICONS_COLLECTION("static/images/icons/thumb/collection.png"),
        ICONS_CURRICULUM("static/images/icons/thumb/curriculum.png"),
        ICONS_TASK("static/images/icons/thumb/task.png"),
        ICONS_LESSON("static/images/icons/thumb/lesson.png"),

        /**
         * Groups
         */
        ICONS_DEFAULT_GROUP("static/images/icons/thumb/group.png"),
        ICONS_DEFAULT_CONTEST("static/images/icons/thumb/competition.png"),
        ICONS_DEFAULT_SECTION("static/images/icons/thumb/section.png"),

        /**
         * Default for files
         */
        ICONS_IMAGE("static/images/icons/thumb/image.png"),
        ICONS_TEXT("static/images/icons/thumb/text.png"),
        ICONS_AUDIO("static/images/icons/thumb/audio.png"),
        ICONS_VIDEO("static/images/icons/thumb/video.png"),
        ICONS_APPLICATION("static/images/icons/thumb/application.png"),
        ICONS_RECORD("static/images/icons/thumb/other.png"),
        ICONS_SIMPLEACTIVITY("static/images/icons/thumb/simpleactivity.png"),

        /**
         * Default types of library
         */
        ICON_ITEM_DOCUMENT("static/images/item/document.png"),
        ICON_ITEM_PRESENTATION("static/images/item/presentation.png"),
        ICON_ITEM_VIDEO("static/images/item/video.png"),
        ICON_ITEM_ARCHIVE("static/images/item/archive.png"),
        ICON_ITEM_COURSE("static/images/item/course.png"),
        ICON_ITEM_WEBINAR("static/images/item/webinar.png"),
        ICON_ITEM_RECORD("static/images/item/record.png"),

        /**
         * Files
         */
        ICONS_TXT("static/images/icons/thumb/txt.png"),
        ICONS_RTF("static/images/icons/thumb/rtf.png"),

        ICONS_PPT("static/images/icons/thumb/ppt.png"),
        ICONS_DOC("static/images/icons/thumb/doc.png"),
        ICONS_XLS("static/images/icons/thumb/xls.png"),
        ICONS_XLSX("static/images/icons/thumb/xlsx.png"),
        ICONS_DOCX("static/images/icons/thumb/docx.png"),
        ICONS_PPTX("static/images/icons/thumb/pptx.png"),
        ICONS_PDF("static/images/icons/thumb/pdf.png"),
        ICONS_ZIP("static/images/icons/thumb/zip.png"),
        ICONS_RAR("static/images/icons/thumb/rar.png"),
        ICONS_SWF("static/images/icons/thumb/swf.png"),

        ICONS_MPEG("static/images/icons/thumb/mpeg.png"),
        ICONS_AVI("static/images/icons/thumb/avi.png"),
        ICONS_WMV("static/images/icons/thumb/wmv.png"),
        ICONS_MP4("static/images/icons/thumb/mp4.png"),
        ICONS_MKV("static/images/icons/thumb/mkv.png"),
        ICONS_FLV("static/images/icons/thumb/flv.png"),
        ICONS_MOV("static/images/icons/thumb/mov.png"),

        ICONS_WAV("static/images/icons/thumb/wav.png"),
        ICONS_MP3("static/images/icons/thumb/mp3.png"),
        ICONS_FLAC("static/images/icons/thumb/flac.png"),
        ICONS_WMA("static/images/icons/thumb/wma.png");

        private String path;

        private StandardIcons(String path) {
            this.path = path;
        }

        public String getPath() {
            return path;
        }
    }

    private UrlUtils() {

    }

}