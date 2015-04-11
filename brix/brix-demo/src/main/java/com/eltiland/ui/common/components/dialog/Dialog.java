package com.eltiland.ui.common.components.dialog;

import com.eltiland.ui.common.components.ResourcesUtils;
import com.eltiland.ui.common.components.button.EltiAjaxLink;
import com.eltiland.ui.common.components.dialog.callback.IDialogCloseCallback;
import org.apache.wicket.Component;
import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.IAjaxCallDecorator;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.IHeaderContributor;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.border.Border;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.util.visit.IVisit;
import org.apache.wicket.util.visit.IVisitor;

/**
 * Poseidon version of the wicket {@link org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow}.
 * <p/>
 * Differences are:
 * <ul>
 * <li>Restyling - total one.</li>
 * <li>Removing handlers on resize/move.</li>
 * <li>Removing closing cross.</li>
 * <li>Added post-show-form-focus behavior.</li>
 * </ul>
 */
public abstract class Dialog<T extends Panel> extends ModalWindow implements IHeaderContributor {
    private static final String PANEL_ID = "innerDialogPanel";

    private boolean isCloseCrossVisible = true;
    private boolean isCloseCrossEnable = true;

    private WrapperPanel wPanel;
    private boolean isDialogPanelAttached = false;

    /**
     * Default constructor.
     *
     * @param id wicket component id
     */
    private Dialog(String id) {
        super(id);

        setOutputMarkupId(true);
        setResizable(false);

        //add(CssPackageResource.getHeaderContribution(ResourcesUtils.CSS_ELT_MODAL_WINDOW));
        add(new Behavior() {
            @Override
            public void renderHead(Component component, IHeaderResponse response) {
                response.renderCSSReference(ResourcesUtils.CSS_ELT_MODAL_WINDOW);
                response.renderOnDomReadyJavaScript(
                        " if(!$(document).data('wicketWindowCloseBound')) {"
                                + " $(document).data('wicketWindowCloseBound', true); "
                                + " $(document).bind('keydown', function(evt) {\n"
                                + "    if (evt.keyCode == 27 && "
                                + "         (window.Wicket.Window.current != null "
                                + "         || window.parent.Wicket.Window.current != null)) {\n"
                                + getCloseJavacript() + "\n"
                                + "        evt.preventDefault();\n"
                                + "        evt.stopPropagation();\n"
                                + "    }\n"
                                + "  });\n"
                                + "};\n");
            }
        });
        setWindowClosedCallback(new WindowClosedCallback() {
            public void onClose(AjaxRequestTarget target) {
                // PLEASE DO NOT REMOVE DEFAULT CLOSE CALLBACK (THIS).

                // it does nothing here, we just need it for wicket to send request to server, and actually hide the
                // content panel from component tree.
                // See the PoseidonWindowClosedBehavior below.

                // If no WindowClosedCallback provided, Wicket would not even notify server but just hide the modal
                // window from browser's DOM, while leaving whole subtree of modal window content not hidden by means
                // of wicket hierarchy.
            }
        });
    }

    /**
     * Dialog dimension defined constructor.
     *
     * @param id           wicket component id
     * @param initialWidth dialog initial width.
     */
    public Dialog(String id, int initialWidth) {
        this(id);

        setUseInitialHeight(false);
        setInitialWidth(initialWidth);
    }

    /**
     * Dialog dimension defined constructor.
     *
     * @param id            wicket component id
     * @param initialWidth  dialog initial width.
     * @param initialHeight dialog initial height.
     */
    public Dialog(String id, int initialWidth, int initialHeight) {
        this(id);

        setUseInitialHeight(true);
        setInitialWidth(initialWidth);
        setInitialHeight(initialHeight);
    }


    /**
     * Override this method for implement custom callbacks.<p/>
     * Because this method is responsible for close by cross action,
     * super call is should be present in override.<p/>
     *
     * @param panel dialog panel. This panel should be used instead of {@link #getDialogPanel()} because,
     *              registerCallback() will call before super.setPanel().
     */
    public void registerCallback(T panel) {
        wPanel.setCloseCallback(new IDialogCloseCallback.IDialogActionProcessor() {
            public void process(AjaxRequestTarget target) {
                close(target);
            }
        });
    }

    /**
     * Implement this method for attach content panel to dialog.
     *
     * @param id wicket component id for created panel.
     * @return Return new dialog panel.
     */
    public abstract T createDialogPanel(String id);

    /**
     * Search dialog panel in child tree of components, if it was attached before, or attach new one.
     *
     * @return Return attached dialog panel.
     */
    public T getDialogPanel() {
        WebMarkupContainer dialogPanelPlaceholder = (WebMarkupContainer) getContent();

        if (isDialogPanelAttached) {
            final Component[] dialogPanel = new Component[1];
            dialogPanelPlaceholder.visitChildren(new IVisitor<Component, Object>() {
                @Override
                public void component(Component component, IVisit<Object> objectIVisit) {
                    if (component.getId().equals(PANEL_ID)) {
                        objectIVisit.stop();
                        dialogPanel[0] = component;
                    }
                }
            });
            return (T) dialogPanel[0];
        } else {
            //if not found, attach new
            return attachDialogPanel();
        }
    }

    /**
     * This method get dialog panel from overridden method {@link #createDialogPanel(String)}
     * and attach it to dialog using {@link ModalWindow#setContent(org.apache.wicket.Component)}.
     * This method manage state of internal flag {@link #isDialogPanelAttached} to serve dialog panel state.
     *
     * @return panel attached to dialog
     */
    private T attachDialogPanel() {
        T panel = createDialogPanel(PANEL_ID);
        if (panel == null) {
            throw new IllegalStateException("Dialog panel is missing! Cannot process dialog!");
        }
        setContent(panel);
        isDialogPanelAttached = true;

        return panel;
    }

    public void detachDialogPanel() {
        isDialogPanelAttached = false;
    }

    public void setCloseCrossVisible(boolean isCloseCrossVisible) {
        this.isCloseCrossVisible = isCloseCrossVisible;
    }

    public void setCloseCrossEnable(boolean isCloseCrossEnable) {
        this.isCloseCrossEnable = isCloseCrossEnable;
    }

    /**
     * DON'T USE THIS. FOR INTERNAL PURPOSE ONLY!<p/>
     * Attach content panel with {@link #createDialogPanel(String)}
     *
     * @param component dialog panel
     * @return modal window with new content panel
     */
    @Override
    public ModalWindow setContent(Component component) {
        //prevent directly use getContentId()
        if (!component.getId().equals(PANEL_ID)) {
            throw new WicketRuntimeException("Dialog content panel id is wrong. Panel ID:"
                    + component.getId() + "; dialog panel placeholder ID: " + PANEL_ID);
        }

        //wrap dialog panel by panel with close cross
        wPanel = new WrapperPanel(getContentId(), (T) component);

        //register action call back
        registerCallback((T) component);

        // we need this for POSEIDON.setFocus(). see getShowJavascript() below.
        component.setOutputMarkupId(true);

        return super.setContent(wPanel);
    }

    @Override
    public void show(AjaxRequestTarget target) {

        //if dialog panel was not attached, create and attach it before show
        if (!isDialogPanelAttached) {
            attachDialogPanel();

        }
        onBeforeShow(target, getDialogPanel());
        //show
        super.show(target);
    }

    /**
     * Override this method to perform actions before show Dialog
     *
     * @param target ajax target
     * @param panel  dialog panel
     */
    protected void onBeforeShow(AjaxRequestTarget target, T panel) {
    }

    @Override
    protected CharSequence getShowJavaScript() {
        //     Hack in some JS to remove the onMove handlers
        StringBuilder showJS = new StringBuilder();
        showJS.append(" ");
        showJS.append("window.setTimeout(function(){\n");
        showJS.append("Wicket.Window.create(settings).show();\n");
        showJS.append("var popupWindow = Wicket.Window.get();\n");
        showJS.append("var nullHandler = function() {};\n");
        showJS.append("if(popupWindow != null) {\n");
        showJS.append("popupWindow.bind(popupWindow.caption, nullHandler);\n");
        showJS.append("popupWindow.bind(popupWindow.bottomRight, nullHandler);\n");
        showJS.append("popupWindow.bind(popupWindow.bottomLeft, nullHandler);\n");
        showJS.append("popupWindow.bind(popupWindow.bottom, nullHandler);\n");
        showJS.append("popupWindow.bind(popupWindow.left, nullHandler);\n");
        showJS.append("popupWindow.bind(popupWindow.right, nullHandler);\n");
        showJS.append("popupWindow.bind(popupWindow.topLeft, nullHandler);\n");
        showJS.append("popupWindow.bind(popupWindow.topRight, nullHandler);\n");
        showJS.append("popupWindow.bind(popupWindow.top, nullHandler);\n");
        showJS.append("}; \n");
        showJS.append("fixHeader(); \n");
        showJS.append("$(window).resize(function(){refreshModalWindowsDimensions();centerCurrentDialog();}); \n");
        showJS.append(String.format("ELTI.setFocus($('#%s'))", getContent().getMarkupId()));
        showJS.append("}, 0); \n");
        return showJS.toString();
    }

    /**
     * Close dialog action.<p/>
     * Override this method for change default on close action
     *
     * @param target ajax target
     */
    protected void onClose(AjaxRequestTarget target) {
    }

    @Override
    public void close(AjaxRequestTarget target) {
        onClose(target);
        super.close(target);
    }

    /**
     * DON'T USE THIS. FOR INTERNAL PURPOSE ONLY!<p/>
     * Set behaviour for disable redirect confirmation dialog.
     *
     * @param response response header
     */
    public final void renderHead(IHeaderResponse response) {
        super.renderHead(response);
        response.renderOnDomReadyJavaScript(
                String.format("Wicket.Window.unloadConfirmation = %s;", false));
        response.renderCSSReference(ResourcesUtils.CSS_ELT_MODAL_WINDOW);
    }

    /**
     * Absolute-style border for dialog wrapper panel.
     * This border required predefined and fixed size of dialog.
     */
    private class AbsoluteStyleDialogBorder extends Border {

        public AbsoluteStyleDialogBorder(String id) {
            super(id);
        }
    }

    /**
     * Relative-style border for dialog wrapper panel.
     * This border can resize as dialog panel.
     */
    private class RelativeStyleDialogBorder extends Border {

        public RelativeStyleDialogBorder(String id) {
            super(id);
        }
    }

    /**
     * Override this for construct dialog with customised dialog border.
     *
     * @param id wicket component id for border construction.
     * @return new Border instance.
     */
    protected Border getDialogBorder(String id) {
        if (isUseInitialHeight()) {
            return new AbsoluteStyleDialogBorder(id);

        }
        return new RelativeStyleDialogBorder(id);
    }

    /**
     * Wrapper panel with close cross. This panel wrap content dialog panel.
     */
    private class WrapperPanel extends Panel implements IDialogCloseCallback {
        private EltiAjaxLink<Void> buttonClose =
                new EltiAjaxLink<Void>("buttonClose") {
                    @Override
                    public void onClick(AjaxRequestTarget target) {
                        onCloseCallback.process(target);
                    }

                    @Override
                    protected void onConfigure() {
                        super.onConfigure();
                        setVisible(Dialog.this.isCloseCrossVisible);
                    }

                    @Override
                    protected IAjaxCallDecorator getAjaxCallDecorator() {
                        return null;
                    }
                };

        public WrapperPanel(String id, T dialogPanel) {
            super(id);

            add(buttonClose);

            Border dialogBorder = Dialog.this.getDialogBorder("dialogBorder");

            add(dialogBorder.setRenderBodyOnly(true));
            dialogBorder.add(dialogPanel);
        }

        /**
         * Callback close action processor.
         */
        protected IDialogCloseCallback.IDialogActionProcessor onCloseCallback;

        /**
         * {@inheritDoc}
         */
        public void setCloseCallback(IDialogCloseCallback.IDialogActionProcessor callback) {
            this.onCloseCallback = callback;
        }
    }


}

