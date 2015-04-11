package com.eltiland.bl.impl;

import com.eltiland.bl.AddressManager;
import com.eltiland.model.PostalAddress;
import com.eltiland.ui.common.components.UIConstants;
import org.hibernate.Query;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Address manager, mostly for suggest boxes etc.
 */
@Component
public class AddressManagerImpl extends ManagerImpl implements AddressManager {

    @Override
    @Transactional(readOnly = true)
    public List<String> getCitiesSuggestions(String query) {
        Query q = getCurrentSession().getNamedQuery("address.cities.suggest");
        q.setParameter("input", "%"+ query+"%").setMaxResults(UIConstants.SUGGEST_SIZE);
        return q.list();
    }

    @Override
    @Transactional
    public void createAddress(PostalAddress address) {
        //TODO: make this a real country selector.
        address.setCountryCode("RU");
        getCurrentSession().persist(address);
    }

    @Override
    @Transactional
    public void updateAddress(PostalAddress address) {
        getCurrentSession().merge(address);
    }
}
