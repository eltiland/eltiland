package com.eltiland.model.payment;

/**
 * Status of the payment.
 *
 * @author Aleksey Plotnikov.
 */
public enum PaidStatus {
    NEW,        /* User produced invoice to paid entitty */
    APPROVED,   /* Admin accepted invoice, and lets user upload some additional docs */
    PAYS,       /* User is paying invoice */
    CONFIRMED   /* User successed in payment */
}
