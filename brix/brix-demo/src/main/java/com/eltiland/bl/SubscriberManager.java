package com.eltiland.bl;

import com.eltiland.exceptions.EltilandManagerException;
import com.eltiland.exceptions.SubscriberException;
import com.eltiland.model.subscribe.Subscriber;

import java.util.List;

/**
 * Subscriber entity manager.
 *
 * @author Aleksey Plotnikov.
 */
public interface SubscriberManager {
    /**
     * Create and persists new subscriber entity.
     *
     * @param subscriber subscriber to create.
     * @return newly created subscriber.
     */
    Subscriber createSubscriber(Subscriber subscriber) throws SubscriberException;

    /**
     * Removes subscriber entity.
     *
     * @param subscriber subscriber to remove.
     */
    void deleteSubscriber(Subscriber subscriber) throws SubscriberException;

    /**
     * Updates subscriber entity.
     *
     * @param subscriber subscriber to update.
     * @return updated subscriber.
     */
    Subscriber updateSubscriber(Subscriber subscriber) throws SubscriberException;

    /**
     * @return count of active subscribers.
     */
    int getActiveSubscriberCount();

    /**
     * @return list of active subscribers.
     */
    List<Subscriber> getActiveSubscriberList(int first, int count, String sortProperty, boolean isAsc);

    /**
     * @return list of active subscribers.
     */
    List<Subscriber> getActiveSubscriberList();

    /**
     * Check if subscriber with given email already present.
     *
     * @param email email to check.
     * @return TRUE if subscriber already exists.
     */
    boolean checkSubscriberByEmail(String email);

    /**
     * Get Subscriber entity by unsubscribe code.
     *
     * @param unCode unsubscribe code.
     * @return entity, which correspond to given code.
     */
    Subscriber getSubscriberByUnCode(String unCode);
}
