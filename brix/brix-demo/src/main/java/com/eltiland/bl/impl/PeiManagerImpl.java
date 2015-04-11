package com.eltiland.bl.impl;

import com.eltiland.bl.FileManager;
import com.eltiland.bl.GenericManager;
import com.eltiland.bl.PeiManager;
import com.eltiland.bl.validators.PeiValidator;
import com.eltiland.exceptions.ConstraintException;
import com.eltiland.exceptions.EltilandManagerException;
import com.eltiland.exceptions.FileException;
import com.eltiland.exceptions.PeiException;
import com.eltiland.model.Pei;
import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.search.Search;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Pei Manager, containing methods related to PEI.
 *
 * @author Aleksey Plotnikov
 */
@Component
public class PeiManagerImpl extends ManagerImpl implements PeiManager {

    @Autowired
    private PeiValidator peiValidator;
    @Autowired
    private FileManager fileManager;
    @Autowired
    private GenericManager genericManager;

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(rollbackFor = PeiException.class)
    public Pei createPei(Pei pei) throws PeiException {

        peiValidator.validate(pei);

        if (pei.getAvatar() != null) {
            if (pei.getAvatar().getId() == null) {
                try {
                    fileManager.saveFile(pei.getAvatar());
                } catch (FileException e) {
                    throw new PeiException(PeiException.SAVE_AVATAR_ERROR);
                }
            }
        }

        try {
            genericManager.saveNew(pei);
        } catch (ConstraintException e) {
            throw new PeiException(PeiException.CREATE_ERROR);
        }
        return pei;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(rollbackFor = PeiException.class)
    public Pei updatePei(Pei pei) throws PeiException {

        peiValidator.validate(pei);

        if (pei.getAvatar() != null) {
            if (pei.getAvatar().getId() == null) {
                try {
                    fileManager.saveFile(pei.getAvatar());
                } catch (FileException e) {
                    throw new PeiException(PeiException.SAVE_AVATAR_ERROR);
                }
            }
        }

        try {
            pei = genericManager.update(pei);
            Search.getFullTextSession(getCurrentSession()).index(pei);
            return pei;
        } catch (ConstraintException e) {
            throw new PeiException(PeiException.UPDATE_ERROR);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(rollbackFor = PeiException.class)
    public void deletePei(Pei toDelete) throws PeiException {
        try {
            genericManager.delete(toDelete);
        } catch (EltilandManagerException e) {
            throw new PeiException(PeiException.DELETE_ERROR);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public List<Pei> getPeiList(int index, Integer count, String sProperty, boolean isAscending, String searchString) {
        Criteria criteria = getCurrentSession().createCriteria(Pei.class);
        if (searchString != null) {
            if (!searchString.isEmpty()) {
                String text = "%" + searchString + "%";
                criteria.add(Restrictions.disjunction()
                        .add(Restrictions.ilike("name", text))
                        .add(Restrictions.ilike("address", text))
                        .add(Restrictions.ilike("email", text))
                        .add(Restrictions.ilike("manager", text))
                        .add(Restrictions.ilike("description", text))
                );
            }
        }
        criteria.addOrder(isAscending ? Order.asc(sProperty) : Order.desc(sProperty));
        return criteria.setFirstResult(index).setMaxResults(count).list();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public int getPeiListCount(String searchString) {
        Criteria criteria = getCurrentSession().createCriteria(Pei.class);
        if (searchString != null) {
            if (!searchString.isEmpty()) {
                String text = "%" + searchString + "%";
                criteria.add(Restrictions.disjunction()
                        .add(Restrictions.ilike("name", text))
                        .add(Restrictions.ilike("address", text))
                        .add(Restrictions.ilike("email", text))
                        .add(Restrictions.ilike("manager", text))
                        .add(Restrictions.ilike("description", text))
                );
            }
        }
        return criteria.list().size();
    }
}