package com.eltiland.bl;

/**
 * This class used to fix XSS-injection.
 */
public interface HtmlCleaner {
    /**
     * Clean html from XSS vulnerability.
     *
     * @param taintedHtml tainted html that may have XSS vulnerability.
     * @return html without XSS
     */
    String cleanHtml(String taintedHtml);
}
