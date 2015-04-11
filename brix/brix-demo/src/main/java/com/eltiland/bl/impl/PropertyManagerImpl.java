package com.eltiland.bl.impl;

import com.eltiland.bl.GenericManager;
import com.eltiland.bl.PropertyManager;
import com.eltiland.exceptions.ConstraintException;
import com.eltiland.exceptions.EltilandManagerException;
import com.eltiland.model.Property;
import org.hibernate.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Manager for Property entity.
 *
 * @author Aleksey Plotnikov
 */
@Component
public class PropertyManagerImpl extends ManagerImpl implements PropertyManager {

    @Autowired
    private GenericManager genericManager;

    @Override
    @Transactional(readOnly = true)
    public String getProperty(String key) {
        return getPropertyByKey(key).getValue();
    }

    @Override
    @Transactional
    public void saveProperty(String key, String value) throws EltilandManagerException {
        Property property = getPropertyByKey(key);
        property.setValue(value);

        try {
            genericManager.update(property);
        } catch (ConstraintException e) {
            throw new EltilandManagerException("Cannot update property", e);
        }
    }

    @Transactional(readOnly = true)
    private Property getPropertyByKey(String key) {
        Query q = getCurrentSession()
                .createQuery("select property from Property as property"
                        + " where property.property = :key");
        q.setParameter("key", key);

        return (Property) q.uniqueResult();
    }
}
