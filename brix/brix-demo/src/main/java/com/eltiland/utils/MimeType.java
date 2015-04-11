package com.eltiland.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * MIME Types
 */
public class MimeType {

    public static final String PDF_TYPE = "application/pdf";
    public static final String DOC_TYPE = "application/msword";
    public static final String DOCX_TYPE = "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
    public static final String ODT_TYPE = "application/vnd.oasis.opendocument.text";

    public static final String GFOLDER_TYPE = "application/vnd.google-apps.folder";

    public static final String PPT_TYPE = "application/vnd.ms-powerpoint";
    public static final String PPTX_TYPE = "application/vnd.openxmlformats-officedocument.presentationml.presentation";
    public static final String ODP_TYPE = "application/vnd.oasis.opendocument.presentation";

    public static final String JPEG_TYPE = "image/jpeg";
    public static final String PNG_TYPE = "image/png";
    public static final String BMP_TYPE = "image/bmp";

    public static final String ZIP_TYPE = "application/zip";
    public static final String OCTET_TYPE = "application/octet-stream";
    public static final String XZIP_TYPE = "application/x-zip-compressed";
    public static final String RAR_TYPE = "application/x-rar-compressed";


    public static List<String> getDocumentTypes() {
        return new ArrayList<>(Arrays.asList(DOC_TYPE, DOCX_TYPE, PDF_TYPE, ODT_TYPE));
    }

    public static List<String> getPresentationTypes() {
        return new ArrayList<>(Arrays.asList(PPT_TYPE, PPTX_TYPE, ODP_TYPE));
    }

    public static List<String> getImageTypes() {
        return new ArrayList<>(Arrays.asList(JPEG_TYPE, PNG_TYPE, BMP_TYPE));
    }

    public static List<String> getArchiveTypes() {
        return new ArrayList<>(Arrays.asList(ZIP_TYPE, XZIP_TYPE, OCTET_TYPE, RAR_TYPE));
    }

    public static String getExtension(String mimeType) {
        switch (mimeType) {
            case MimeType.DOC_TYPE:
            case MimeType.DOCX_TYPE:
                return ".docx";
            case MimeType.PPT_TYPE:
            case MimeType.PPTX_TYPE:
                return ".pptx";
            case MimeType.JPEG_TYPE:
                return ".jpg";
            case MimeType.PNG_TYPE:
                return ".png";
            case MimeType.BMP_TYPE:
                return ".bmp";
            case MimeType.ZIP_TYPE:
            case MimeType.XZIP_TYPE:
                return ".zip";
            case MimeType.OCTET_TYPE:
                return ".exe";
            case MimeType.RAR_TYPE:
                return ".rar";
            default:
                return "";
        }
    }
}
