package com.eltiland.ui.paymentnew;

import com.eltiland.ui.common.OneColumnPage;
import com.eltiland.ui.worktop.BaseWorktopPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.request.mapper.parameter.PageParameters;

/**
 * Page with success label about payment.
 *
 * @author Aleksey Plotnikov
 */
public class PaymentSuccessPage extends OneColumnPage {

    public static final String MOUNT_PATH = "/paySuccess";

    private final BookmarkablePageLink<PaymentSuccessPage> worktopLink
            = new BookmarkablePageLink<>("LinktoWorkTop", BaseWorktopPage.class);

    /**
     * Profile id page parameter.
     */
    public static final String PARAM_ID = "id";

    /**
     * Page constructor.
     *
     * @param parameters page params
     */
    public PaymentSuccessPage(PageParameters parameters) {
        super(parameters);
        add(worktopLink);
        add(new Label("id", parameters.get(PARAM_ID).toString()));
    }
}
