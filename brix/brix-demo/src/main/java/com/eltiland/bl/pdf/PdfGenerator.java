package com.eltiland.bl.pdf;

import com.eltiland.exceptions.EltilandManagerException;

import java.io.InputStream;

/**
 * Interface for PDF generator.
 *
 * @author Aleksey Plotnikov
 */
public interface PdfGenerator {
    /**
     * Generate PDF from given stream.
     *
     * @param stream source stream
     * @return stream to new generated PDF
     */
    InputStream generatePDF(InputStream stream) throws EltilandManagerException;
}
