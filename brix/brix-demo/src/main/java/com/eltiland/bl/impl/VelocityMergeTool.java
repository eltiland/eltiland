package com.eltiland.bl.impl;

import com.eltiland.exceptions.VelocityCommonException;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.tools.generic.DateTool;
import org.apache.velocity.tools.generic.EscapeTool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Map;
import java.util.Properties;

/**
 * @author knorr
 * @version 1.0
 * @since 8/3/12
 */
@Component
public class VelocityMergeTool {

    private final static Logger LOGGER = LoggerFactory.getLogger(VelocityMergeTool.class);

    @Autowired
    @Qualifier("eltilandProperties")
    protected Properties applicationProps;

    @Autowired
    private VelocityEngine velocityEngine;

    public String mergeTemplate(Map<String, Object> model, String templateName) throws VelocityCommonException {
        CollectionUtils.mergePropertiesIntoMap(applicationProps, model);
        model.put("esc", new EscapeTool());
        model.put("dateFormat", new DateTool() {
            @Override
            public String format(Object obj) {
                return format(applicationProps.getProperty("application.date.format"), obj);
            }
        });
        VelocityContext velocityContext = new VelocityContext(model);
        try {
            ByteArrayOutputStream templateStream = new ByteArrayOutputStream();
            OutputStreamWriter outputWriter = new OutputStreamWriter(templateStream, "UTF-8");
            velocityEngine.mergeTemplate(templateName, "UTF-8", velocityContext, outputWriter);
            outputWriter.close();
            return templateStream.toString("UTF-8");

        } catch (IOException e) {
            LOGGER.error("Got an exception while trying to merge Velocify template ", e);
            throw new VelocityCommonException(e);
        }
    }

}
