package com.eltiland.bl.pdf;

import com.eltiland.exceptions.EltilandManagerException;
import com.eltiland.model.webinar.WebinarRecordPayment;
import com.eltiland.model.webinar.WebinarUserPayment;

import java.io.InputStream;

/**
 * Webinar certificate generator.
 *
 * @author Aleksey Plotnikov.
 */
public interface WebinarCertificateGenerator {

    /**
     * Generate certificate for given webinar user about listening given webinar.
     *
     * @param user Webinar user data.
     */
    InputStream generateCertificate(WebinarUserPayment user) throws EltilandManagerException;

    /**
     * Generate certificate for given user, which bought webinar record.
     *
     * @param user Webinar user data.
     */
    InputStream generateRecordCertificate(WebinarRecordPayment user) throws EltilandManagerException;
}
