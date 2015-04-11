package com.eltiland.bl.impl.generator;

import com.eltiland.bl.pdf.PdfGenerator;
import com.eltiland.exceptions.EltilandManagerException;
import com.eltiland.utils.MimeType;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.configuration.DefaultConfigurationBuilder;
import org.apache.commons.io.IOUtils;
import org.apache.fop.apps.FOUserAgent;
import org.apache.fop.apps.Fop;
import org.apache.fop.apps.FopFactory;
import org.apache.fop.apps.FormattingResults;
import org.springframework.stereotype.Component;
import org.xml.sax.SAXException;

import javax.annotation.PostConstruct;
import javax.xml.transform.*;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.stream.StreamSource;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * PDF generator implementation.
 *
 * @author Aleksey Plotnikov.
 */
@Component
public class PdfGeneratorImpl implements PdfGenerator {
    protected FopFactory fopFactory = null;

    private final String FONT_DIRECTORY = "/fop/";
    private final String IMAGE_DIRECTORY = "/images/";

    @PostConstruct
    protected void init() throws EltilandManagerException {
        fopFactory = FopFactory.newInstance();
        try {
            DefaultConfigurationBuilder cfgBuilder = new DefaultConfigurationBuilder();
            fopFactory.setUserConfig((cfgBuilder.build(getClass().getResourceAsStream("/fop/fop.xml"))));
        } catch (SAXException | ConfigurationException | IOException e) {
            throw new EltilandManagerException("Can not init FOP library", e);
        }
    }

    @Override
    public InputStream generatePDF(InputStream stream) throws EltilandManagerException {
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            FOUserAgent foUserAgent = new FOUserAgent(fopFactory);

            foUserAgent.setURIResolver(new URIResolver() {
                @Override
                public Source resolve(String href, String base) throws TransformerException {
                    if (href.contains(".ttf") || href.contains(".xml")) { // Font information
                        return new StreamSource(getClass().getResourceAsStream(FONT_DIRECTORY + href));
                    } else if (href.contains(".jpg")) {
                        return new StreamSource(getClass().getResourceAsStream(IMAGE_DIRECTORY + href));
                    } else {
                        return new StreamSource();
                    }
                }
            });

            Fop fop = fopFactory.newFop(MimeType.PDF_TYPE, foUserAgent, out);
            // Setup JAXP using identity transformer
            TransformerFactory factory = TransformerFactory.newInstance();
            javax.xml.transform.Transformer transformer = factory.newTransformer(); // identity transformer
            // Setup input stream
            Source src = new StreamSource(new ByteArrayInputStream(IOUtils.toByteArray(stream)));
            // Resulting SAX events (the generated FO) must be piped through
            Result res = new SAXResult(fop.getDefaultHandler());
            // Start XSLT transformation and FOP processing
            transformer.transform(src, res);
            // Result processing
            FormattingResults foResults = fop.getResults();
            out.close();
            return new ByteArrayInputStream(out.toByteArray());
        } catch (Exception e) {
            throw new EltilandManagerException("Can not generate PDF", e);
        }
    }
}
