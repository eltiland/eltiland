package com.eltiland.bl;

import com.eltiland.exceptions.EltilandManagerException;
import com.eltiland.exceptions.EmailException;
import com.eltiland.model.user.User;
import com.eltiland.model.webinar.Webinar;
import com.eltiland.model.webinar.WebinarMultiplyPayment;
import com.eltiland.model.webinar.WebinarUserPayment;

import java.util.List;

/**
 * Interface for managing Webinar's Usersmulptiply payments.
 *
 * @author Aleksey Plotnikov.
 */
public interface WebinarMultiplyPaymentManager {

    /**
     * Get user payment by its pay link.
     *
     * @param payLink pay link.
     */
    WebinarMultiplyPayment getPaymentByLink(String payLink);

    /**
     * Pay for webinar user payment.
     *
     * @param payment webinar user payment.
     */
    boolean payWebinarUserPayment(WebinarMultiplyPayment payment) throws EltilandManagerException;
}
