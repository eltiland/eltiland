package com.eltiland.bl.impl.tags;

import com.eltiland.bl.GenericManager;
import com.eltiland.bl.impl.ManagerImpl;
import com.eltiland.bl.tags.TagEntityManager;
import com.eltiland.exceptions.EltilandManagerException;
import com.eltiland.model.tags.Tag;
import com.eltiland.model.tags.TagEntity;
import org.hibernate.Criteria;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * Manager for Tag Category entity.
 *
 * @author Aleksey Plotnikov
 */
@Component
public class TagEntityManagerImpl extends ManagerImpl implements TagEntityManager {
    @Autowired
    private GenericManager genericManager;

    @Override
    @Transactional(readOnly = true)
    public boolean checkTagPresent(Long entity_id, Long tag_id) {
        Criteria criteria = getCurrentSession().createCriteria(TagEntity.class);
        criteria.add(Restrictions.eq("entity", entity_id));
        criteria.add(Restrictions.eq("tag", tag_id));

        return !(criteria.list().isEmpty());
    }

    @Override
    @Transactional
    public void deleteTag(Long entity_id, Long tag_id) throws EltilandManagerException {
        Criteria criteria = getCurrentSession().createCriteria(TagEntity.class);
        criteria.add(Restrictions.eq("entity", entity_id));
        criteria.add(Restrictions.eq("tag", tag_id));

        TagEntity entity = (TagEntity) criteria.uniqueResult();
        if (entity != null) {
            genericManager.delete(entity);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<Tag> getEntityTags(Long entity_id) {
        Criteria criteria = getCurrentSession().createCriteria(TagEntity.class);
        criteria.add(Restrictions.eq("entity", entity_id));

        List<Tag> result = new ArrayList<>();
        List<TagEntity> entities = criteria.list();
        for (TagEntity entity : entities) {
            result.add(genericManager.getObject(Tag.class, entity.getTag()));
        }
        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Long> getEntityIds(List<Tag> tags) {
        List<Long> result = new ArrayList<>();

        Criteria criteria = getCurrentSession().createCriteria(TagEntity.class);
        Disjunction or = Restrictions.disjunction();
        for (Tag tag : tags) {
            or.add(Restrictions.eq("tag", tag.getId()));
        }
        criteria.add(or);

        List<TagEntity> entities = criteria.list();
        for (TagEntity entity : entities) {
            if (!(result.contains(entity.getEntity()))) {
                result.add(entity.getEntity());
            }
        }

        return result;
    }

    @Override
    @Transactional
    public void deleteTagEntity(Long tag_id) throws EltilandManagerException {
        Criteria criteria = getCurrentSession().createCriteria(TagEntity.class);
        criteria.add(Restrictions.eq("tag", tag_id));
        List<TagEntity> entityList = criteria.list();

        for (TagEntity entity : entityList) {
            genericManager.delete(entity);
        }
    }

    @Override
    public void deleteTagEntityById(Long entity_id) throws EltilandManagerException {
        Criteria criteria = getCurrentSession().createCriteria(TagEntity.class);
        criteria.add(Restrictions.eq("entity", entity_id));
        List<TagEntity> entityList = criteria.list();

        for (TagEntity entity : entityList) {
            genericManager.delete(entity);
        }
    }
}
