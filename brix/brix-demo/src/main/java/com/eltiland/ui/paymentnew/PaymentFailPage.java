package com.eltiland.ui.paymentnew;

import com.eltiland.ui.common.TwoColumnPage;
import com.eltiland.ui.payment.SuccessPaymentPage;
import com.eltiland.ui.worktop.BaseWorktopPage;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.request.mapper.parameter.PageParameters;

/**
 * Page with fail label about payment.
 *
 * @author Aleksey Plotnikov
 */
public class PaymentFailPage extends TwoColumnPage {

    public static final String MOUNT_PATH = "/payFail";
    private final BookmarkablePageLink<SuccessPaymentPage> worktopLink
            = new BookmarkablePageLink<>("LinktoWorkTop", BaseWorktopPage.class);

    /**
     * Page constructor.
     *
     * @param parameters page params
     */
    public PaymentFailPage(PageParameters parameters) {
        super(parameters);
        add(worktopLink);
    }
}
