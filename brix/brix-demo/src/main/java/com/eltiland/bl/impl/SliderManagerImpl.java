package com.eltiland.bl.impl;

import com.eltiland.bl.GenericManager;
import com.eltiland.bl.SliderManager;
import com.eltiland.exceptions.ConstraintException;
import com.eltiland.exceptions.EltilandManagerException;
import com.eltiland.model.Slider;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Slider Manager implementation.
 *
 * @author Aleksey Plotnikov
 */
@Component
public class SliderManagerImpl extends ManagerImpl implements SliderManager {

    @Autowired
    private GenericManager genericManager;

    @Override
    @Transactional
    public void moveUp(Slider slider) throws EltilandManagerException {
        int order = slider.getOrder();

        if( order > 0 ) {
            Slider slider2 = getSliderByOrder(order - 1);
            swapSliders(slider2, slider);
        }
    }

    @Override
    @Transactional
    public void moveDown(Slider slider) throws EltilandManagerException {
        int order = slider.getOrder();
        int count = genericManager.getEntityCount(Slider.class, null, null);
        if ((order + 1) < count) {
            Slider slider2 = getSliderByOrder(order + 1);
            swapSliders(slider, slider2);
        }
    }


    @Transactional
    private Slider getSliderByOrder(int order) {
        Criteria criteria = getCurrentSession().createCriteria(Slider.class);
        criteria.add(Restrictions.eq("order", order));
        return (Slider) criteria.uniqueResult();
    }

    @Transactional
    private void swapSliders(Slider slider1, Slider slider2) throws EltilandManagerException {
        int order = slider1.getOrder();
        int order2 = slider2.getOrder();

        slider1.setOrder(order2);
        slider2.setOrder(order);

        try {
            genericManager.update(slider1);
            genericManager.update(slider2);
        } catch (ConstraintException e) {
            throw new EltilandManagerException("Constraint exception", e);
        }
    }
}