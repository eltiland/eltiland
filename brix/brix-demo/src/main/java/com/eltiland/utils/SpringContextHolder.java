package com.eltiland.utils;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * Specific class to hold spring context - for the cases where we need to have a spring context in non-spring-aware
 * code (for instance, for FieldBridge implementations converting arbitrary values to strings to feed to lucene).
 * <p/>
 * <strong>Please use with care! May throw illegal state exception if not yet initialized!</strong>
 */
public class SpringContextHolder implements ApplicationContextAware {
    private static ApplicationContext context;

    public void setApplicationContext(ApplicationContext context) throws BeansException {
        this.context = context;
    }

    public static ApplicationContext getApplicationContext() {
        if (context == null) {
            throw new IllegalStateException("Trying to obtain spring ApplicationContext when it is not yet " +
                    "initialized! Someone has mis-used SpringContextHolder (to be used with great care)");
        }
        return context;
    }

}
