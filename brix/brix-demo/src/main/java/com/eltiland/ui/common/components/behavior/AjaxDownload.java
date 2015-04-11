package com.eltiland.ui.common.components.behavior;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.behavior.AbstractAjaxBehavior;
import org.apache.wicket.request.IRequestCycle;
import org.apache.wicket.request.UrlEncoder;
import org.apache.wicket.request.handler.resource.ResourceStreamRequestHandler;
import org.apache.wicket.util.resource.IResourceStream;

/**
 * Ajax-powered download.
 * <p/>
 * This behavior should be added to (component, page) and then invoked with the ajax request target (from the AjaxLink
 * handler for instance.
 * <p/>
 * After initiate(), it renders a window.location=${this.behaviorUrl} to the wicket's ajax js evaluate. This leads
 * to an interesting behavior when we can at the same call update interface and ask user to download a file.
 */
public abstract class AjaxDownload extends AbstractAjaxBehavior {
    private boolean addAntiCache;

    /**
     * Default constructor, adding anti-cache symbols.
     */
    public AjaxDownload() {
        this(true);
    }

    /**
     * May change if add anti cache (what for?).
     *
     * @param addAntiCache if to add anti-cache part of the URL.
     */
    public AjaxDownload(boolean addAntiCache) {
        super();
        this.addAntiCache = addAntiCache;
    }

    /**
     * Call this method to initiate the download.
     *
     * @param target to add to
     */
    public void initiate(AjaxRequestTarget target) {
        String url = getCallbackUrl().toString();

        if (addAntiCache) {
            if (url.contains("?")) {
                url = url + "&";
            } else {
                url = url + "?";
            }
            url = url + "antiCache=" + System.currentTimeMillis();
        }

        target.appendJavaScript("window.location.href='" + url + "'");
    }

    /**
     * {@inheritDoc}
     */
    public final void onRequest() {
        getComponent().getRequestCycle().scheduleRequestHandlerAfterCurrent(
                new ResourceStreamRequestHandler(getResourceStream(),
                        UrlEncoder.FULL_PATH_INSTANCE.encode(getFileName(), "UTF-8")) {
                    @Override
                    public void respond(IRequestCycle requestCycle) {
                        super.respond(requestCycle);
                        onRespond(requestCycle);
                    }
                });
    }

    /**
     * Override this method for release used resources.
     *
     * @param requestCycle may override onRespond for specific handler
     * @see ResourceStreamRequestHandler#getFileName()
     */
    protected void onRespond(IRequestCycle requestCycle) {
    }

    /**
     * Override this method for a file name which will let the browser prompt with a save/open dialog.
     *
     * @return file name to assing to a file. If none, then browser would save something default (like download.pdf).
     * @see ResourceStreamRequestHandler#getFileName()
     */
    protected String getFileName() {
        return null;
    }

    /**
     * Hook method providing the actual resource stream.
     *
     * @return resourceStream of what we need to download. For in-memory or generated resources
     */
    protected abstract IResourceStream getResourceStream();
}
