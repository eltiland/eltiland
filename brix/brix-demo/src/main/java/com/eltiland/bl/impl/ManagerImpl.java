package com.eltiland.bl.impl;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Base class for all the spring beans out here.
 *
 * @author Aleksey Plotnikov
 */
public class ManagerImpl {
    @Autowired
    private SessionFactory sessionFactory;

    /**
     * Retrieves current session. If the method has been marked with @Transactional(readOnly=true) then the session
     * will be readonly.
     *
     * @return Session current locally (thread-local) bound session.
     */
    public Session getCurrentSession() {
        return sessionFactory.getCurrentSession();
    }

    /**
     * Creates an Order object basing on the info given in params.
     * {@link org.hibernate.criterion.Order} may be used later for appending to
     * criterias or to queries as a part.
     *
     * @param alias       alias of the sortable object. E.g. when we do <code>select e from Entity e </code> and want
     *                    to have an order on it, we would pass e as a param.
     * @param sProperty   property to sort on.
     * @param isAscending whether sort is to be ascending
     * @return constructed object of type Order
     */
    public Order parseOrderCriteria(String alias, String sProperty, boolean isAscending) {
        if (sProperty == null) {
            sProperty = alias + ".id";
        }

        //if (!sProperty.contains(".")) {
        sProperty = alias + "." + sProperty;
        // }

        Order order;
        if (isAscending) {
            order = Order.asc(sProperty);
        } else {
            order = Order.desc(sProperty);
        }

        return order;
    }
}
