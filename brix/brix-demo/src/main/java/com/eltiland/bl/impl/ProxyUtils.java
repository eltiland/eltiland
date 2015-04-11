package com.eltiland.bl.impl;

import com.eltiland.model.Identifiable;
import org.hibernate.proxy.HibernateProxy;

/**
 * Class for get the real object of proxy object
 *
 * @author Pavel Androschuk
 */
public class ProxyUtils {
    @SuppressWarnings("unchecked")
    public static <T> T unproxy(T object) {
        if (object == null) {
            return null;
        }

        if (object instanceof HibernateProxy) {
            object = (T) ((HibernateProxy) object).getHibernateLazyInitializer().getImplementation();
        }
        return object;
    }
}
