package com.eltiland.bl.impl;

import com.eltiland.bl.HtmlCleaner;
import org.owasp.validator.html.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;

import java.io.IOException;

/**
 * This class used to fix XSS-injection.
 */
public class HtmlCleanerImpl implements HtmlCleaner {

    protected static final Logger LOGGER = LoggerFactory.getLogger(HtmlCleanerImpl.class);

    private Resource policyResource;

    /**
     * {@inheritDoc}
     */
    public String cleanHtml(String taintedHtml) {
        try {
            Policy policy = Policy.getInstance(policyResource.getInputStream());
            AntiSamy as = new AntiSamy();
            CleanResults cr = as.scan(taintedHtml, policy);
            return cr.getCleanHTML();
        } catch (PolicyException e) {
            LOGGER.error("Tainted html: " + taintedHtml, e);
            throw new RuntimeException(e);
        } catch (ScanException e) {
            LOGGER.error("Tainted html: " + taintedHtml, e);
            throw new RuntimeException(e);
        } catch (IOException e) {
            LOGGER.error("Tainted html: " + taintedHtml, e);
            throw new RuntimeException(e);
        }
    }

    public void setPolicyResource(Resource policyResource) {
        this.policyResource = policyResource;
    }
}

