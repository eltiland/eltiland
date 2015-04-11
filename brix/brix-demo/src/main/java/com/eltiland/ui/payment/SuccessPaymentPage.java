package com.eltiland.ui.payment;

import com.eltiland.ui.common.TwoColumnPage;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import com.eltiland.ui.worktop.BaseWorktopPage;

/**
 * Page with success label about payment.
 *
 * @author Aleksey Plotnikov
 */
public class SuccessPaymentPage extends TwoColumnPage {

    public static final String MOUNT_PATH = "/paymentSuccess";
    private final BookmarkablePageLink<SuccessPaymentPage> worktopLink
            = new BookmarkablePageLink<>("LinktoWorkTop", BaseWorktopPage.class);

    /**
     * Page constructor.
     *
     * @param parameters page params
     */
    public SuccessPaymentPage(PageParameters parameters) {
        super(parameters);
        add(worktopLink);
    }
}
