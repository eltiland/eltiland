package com.eltiland.utils;

import org.apache.wicket.validation.validator.PatternValidator;

/**
 * Common string utils.
 *
 * @author Aleksey Plotnikov
 */
public class StringUtils {
    public static final PatternValidator emailValidator =
            new PatternValidator("^[-a-z0-9!#$%&'*+/=?^_`{|}~]+(?:\\.[-a-z0-9!#$%&'*+/=?^_`{|}~]+)*@(?:[a-z0-9]([-a-z0-9]{0,61}[a-z0-9])?\\.)*(?:aero|arpa|asia|biz|cat|com|coop|edu|gov|info|int|jobs|mil|mobi|museum|name|net|org|pro|tel|travel|[a-z][a-z])$") {
                @Override
                protected String resourceKey() {
                    return "emailValidator";
                }
            };


    public static String truncate(String str, int maxLen) {
        if (str != null && str.length() > maxLen) {
            return str.substring(0, maxLen - 3) + "...";
        }
        return str;
    }
}
