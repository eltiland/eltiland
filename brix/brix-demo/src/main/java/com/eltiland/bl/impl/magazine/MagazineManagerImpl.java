package com.eltiland.bl.impl.magazine;

import com.eltiland.bl.CountableManager;
import com.eltiland.bl.FileManager;
import com.eltiland.bl.GenericManager;
import com.eltiland.bl.impl.ManagerImpl;
import com.eltiland.bl.magazine.MagazineManager;
import com.eltiland.exceptions.ConstraintException;
import com.eltiland.exceptions.CountableException;
import com.eltiland.exceptions.EltilandManagerException;
import com.eltiland.exceptions.FileException;
import com.eltiland.model.magazine.Magazine;
import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Manager of forum group entity.
 *
 * @author Aleksey Plotnikov
 */
@Component
public class MagazineManagerImpl extends ManagerImpl implements MagazineManager {
    @Autowired
    private GenericManager genericManager;
    @Autowired
    private FileManager fileManager;
    @Autowired
    private CountableManager<Magazine> countableManager;

    @Override
    @Transactional
    public Magazine createMagazine(Magazine magazine) throws EltilandManagerException, CountableException, FileException {
        if (magazine.getContent() != null) {
            if (magazine.getContent().getId() == null) {
                fileManager.saveFile(magazine.getContent());
            }
        }
        if (magazine.getCover() != null) {
            if (magazine.getCover().getId() == null) {
                fileManager.saveFile(magazine.getCover());
            }
        }

        magazine.setActive(true);
        return countableManager.create(magazine);
    }

    @Override
    @Transactional
    public Magazine updateMagazine(Magazine magazine) throws EltilandManagerException, FileException {
        if (magazine.getContent() != null) {
            if (magazine.getContent().getId() == null) {
                fileManager.saveFile(magazine.getContent());
            }
        }
        if (magazine.getCover() != null) {
            if (magazine.getCover().getId() == null) {
                fileManager.saveFile(magazine.getCover());
            }
        }

        try {
            return genericManager.update(magazine);
        } catch (ConstraintException e) {
            throw new EltilandManagerException("Constraint violation when updating magazine", e);
        }
    }

    @Override
    @Transactional
    public void deleteMagazine(Magazine magazine) throws FileException, CountableException {
        genericManager.initialize(magazine, magazine.getCover());
        genericManager.initialize(magazine, magazine.getContent());
        if (magazine.getCover() != null) {
            fileManager.deleteFile(magazine.getCover());
        }
        if (magazine.getContent() != null) {
            fileManager.deleteFile(magazine.getContent());
        }
        magazine.setActive(false);
        countableManager.pseudoDelete(magazine);
    }

    @Override
    @Transactional(readOnly = true)
    public int getMagazineCount() {
        Criteria criteria = getCurrentSession().createCriteria(Magazine.class);
        criteria.add(Restrictions.eq("active", true));
        return criteria.list().size();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Magazine> getListOfMagazines() {
        Criteria criteria = getCurrentSession().createCriteria(Magazine.class);
        criteria.add(Restrictions.eq("active", true));
        criteria.addOrder(Order.asc("index"));
        return criteria.list();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Magazine> getListOfMagazines(int index, int count, String sProperty, boolean isAsc) {
        Criteria criteria = getCurrentSession().createCriteria(Magazine.class);
        criteria.add(Restrictions.eq("active", true));
        criteria.setFirstResult(index);
        criteria.setMaxResults(count);
        criteria.addOrder(isAsc ? Order.asc(sProperty) : Order.desc(sProperty));
        return criteria.list();
    }
}
