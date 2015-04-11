package com.eltiland.ui.common.components.button;

import com.eltiland.ui.common.components.behavior.AjaxDownload;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.IRequestCycle;
import org.apache.wicket.util.resource.IResourceStream;

/**
 * Shorthand for the link initiating download.
 * <p/>
 * This class makes you to override the getResource() and getFilename() methods and will ask user to download the given
 * stream as a file.
 *
 * @param <T> model type
 * @see AjaxDownload
 */
public abstract class AjaxDownloadLink<T> extends AjaxLink<T> {

    private AjaxDownload ajaxDownloadBehavior = new AjaxDownload() {
        @Override
        protected IResourceStream getResourceStream() {
            return AjaxDownloadLink.this.getResourceStream();
        }

        @Override
        protected String getFileName() {
            return AjaxDownloadLink.this.getFileName();
        }

        @Override
        protected void onRespond(IRequestCycle requestCycle) {
            AjaxDownloadLink.this.onRespond(requestCycle);
        }
    };

    /**
     * See original {@link AjaxDownload#onRespond(org.apache.wicket.request.IRequestCycle)}.
     *
     * @param requestCycle cycle of the current request.
     */
    public void onRespond(IRequestCycle requestCycle) {
        // do-nothing
    }

    /**
     * See original {@link AjaxDownload#getFileName()}.
     *
     * @return filename for AjaxDownload
     */
    public abstract String getFileName();

    /**
     * See original {@link AjaxDownload#getResourceStream()}.
     *
     * @return resource stream
     */
    public abstract IResourceStream getResourceStream();

    public AjaxDownloadLink(String id) {
        super(id);
        setOutputMarkupId(true);
        add(ajaxDownloadBehavior);
    }

    public AjaxDownloadLink(String id, IModel<T> model) {
        super(id, model);
        setOutputMarkupId(true);
        add(ajaxDownloadBehavior);
    }

    @Override
    public void onClick(AjaxRequestTarget target) {
        ajaxDownloadBehavior.initiate(target);
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
        response.renderOnDomReadyJavaScript("$('#" + getMarkupId() + "').click(function(e){e.stopPropagation()});");
    }
}
