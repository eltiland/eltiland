package com.eltiland.bl.impl.magazine;

import com.eltiland.bl.GenericManager;
import com.eltiland.bl.impl.ManagerImpl;
import com.eltiland.bl.magazine.ClientManager;
import com.eltiland.exceptions.ConstraintException;
import com.eltiland.exceptions.EltilandManagerException;
import com.eltiland.model.magazine.Client;
import com.eltiland.utils.DateUtils;
import org.apache.commons.lang.RandomStringUtils;
import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Manager for Client entity.
 *
 * @author Aleksey Plotnikov
 */
@Component
public class ClientManagerImpl extends ManagerImpl implements ClientManager {
    @Autowired
    private GenericManager genericManager;

    @Override
    @Transactional
    public Client createClient(Client client) throws EltilandManagerException {
        try {
            return genericManager.saveNew(client);
        } catch (ConstraintException e) {
            throw new EltilandManagerException("Cannot create client entity", e);
        }
    }

    @Override
    @Transactional
    public Client updateClient(Client client) throws EltilandManagerException {
        try {
            return genericManager.update(client);
        } catch (ConstraintException e) {
            throw new EltilandManagerException("Cannot update client entity", e);
        }
    }

    @Override
    @Transactional
    public void payClientMagazines(Client client) throws EltilandManagerException {
        client.setActive(true);
        client.setDate(DateUtils.getCurrentDate());
        client.setCode(RandomStringUtils.randomAlphanumeric(10));
        client.setStatus(true);
        updateClient(client);
    }

    @Override
    @Transactional(readOnly = true)
    public Client getClientByCode(String code) {
        Criteria criteria = getCurrentSession().createCriteria(Client.class);
        criteria.add(Restrictions.eq("code", code));
        return (Client) criteria.uniqueResult();
    }

    @Override
    @Transactional(readOnly = true)
    public int getAppliedClientsCount() {
        Criteria criteria = getCurrentSession().createCriteria(Client.class);
        criteria.add(Restrictions.eq("status", true));
        return criteria.list().size();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Client> getAppliedClients(int index, int count, String sProperty, boolean isAsc) {
        Criteria criteria = getCurrentSession().createCriteria(Client.class);
        criteria.add(Restrictions.eq("status", true));
        criteria.setFirstResult(index);
        criteria.setMaxResults(count);
        criteria.addOrder(isAsc ? Order.asc(sProperty) : Order.desc(sProperty));
        return criteria.list();
    }
}
