package com.eltiland.ui.payment;

import com.eltiland.ui.common.TwoColumnPage;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import com.eltiland.ui.worktop.BaseWorktopPage;

/**
 * Page with fail label about payment.
 *
 * @author Aleksey Plotnikov
 */
public class FailPaymentPage extends TwoColumnPage {

    public static final String MOUNT_PATH = "/paymentFail";
    private final BookmarkablePageLink<SuccessPaymentPage> worktopLink
            = new BookmarkablePageLink<>("LinktoWorkTop", BaseWorktopPage.class);

    /**
     * Page constructor.
     *
     * @param parameters page params
     */
    public FailPaymentPage(PageParameters parameters) {
        super(parameters);
        add(worktopLink);
    }
}
