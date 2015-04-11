package com.eltiland.utils;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @author knorr
 * @version 1.0
 * @since 8/1/12
 */
public class MimeTypes {

    public static class MimeType implements Serializable {

        private String typeIdentifier;

        private UrlUtils.StandardIcons icon;

        public MimeType(String typeIdentifier, UrlUtils.StandardIcons icon) {
            this.typeIdentifier = typeIdentifier;
            this.icon = icon;
        }

        public String getTypeIdentifier() {
            return typeIdentifier;
        }

        public UrlUtils.StandardIcons getIcon() {
            return icon;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof MimeType)) return false;

            MimeType mimeType = (MimeType) o;

            return typeIdentifier.equals(mimeType.typeIdentifier);
        }

        @Override
        public int hashCode() {
            return typeIdentifier.hashCode();
        }
    }

    public static final MimeType IMAGE = new MimeType("image", UrlUtils.StandardIcons.ICONS_IMAGE);
    public static final MimeType TEXT = new MimeType("text", UrlUtils.StandardIcons.ICONS_TEXT);
    public static final MimeType AUDIO = new MimeType("audio", UrlUtils.StandardIcons.ICONS_AUDIO);
    public static final MimeType VIDEO = new MimeType("video", UrlUtils.StandardIcons.ICONS_VIDEO);
    public static final MimeType APPLICATION = new MimeType("application", UrlUtils.StandardIcons.ICONS_APPLICATION);
    public static final MimeType RECORD = new MimeType("record", UrlUtils.StandardIcons.ICONS_RECORD);

    public static final List<MimeType> ALL_SUPPORTED_TYPES = Collections.unmodifiableList(Arrays
            .asList(IMAGE, TEXT, AUDIO, VIDEO, APPLICATION));

    public static MimeType getTypeOf(String type) {
        for (MimeType mimeType : ALL_SUPPORTED_TYPES) {
            if (type.startsWith(mimeType.getTypeIdentifier())) {
                return mimeType;
            }
        }
        throw new IllegalArgumentException(String.format("Type: %s isn't recognized", type));
    }

    /**
     * Just helper class, prevent instantiation.
     */
    private MimeTypes() {
    }
}
