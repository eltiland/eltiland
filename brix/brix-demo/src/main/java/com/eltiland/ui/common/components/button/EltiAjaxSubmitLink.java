package com.eltiland.ui.common.components.button;

import com.eltiland.ui.common.components.UIConstants;
import com.eltiland.ui.common.components.dialog.ELTAlerts;
import com.eltiland.ui.common.components.feedbacklabel.ELTFeedbackLabel;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.IAjaxCallDecorator;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.IModel;
import org.apache.wicket.util.visit.IVisit;
import org.apache.wicket.util.visit.IVisitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementation of {@link org.apache.wicket.ajax.markup.html.AjaxLink} supported indicator.
 *
 * @param <T> type of model object
 */
public abstract class EltiAjaxSubmitLink<T> extends AjaxSubmitLink {

    private static final Logger LOGGER = LoggerFactory.getLogger(EltiAjaxSubmitLink.class);

    private EltiLoadingAjaxDecorator decorator = new EltiLoadingAjaxDecorator(this);

    /**
     * Default constructor.
     *
     * @param id component id
     */
    public EltiAjaxSubmitLink(String id) {
        super(id);
        add(AttributeModifier.append("class", UIConstants.CLASS_LINKBUTTON));
    }

    /**
     * Constructor with model.
     *
     * @param id component id
     */
    public EltiAjaxSubmitLink(String id, IModel<T> model) {
        this(id);
        setModel(model);
    }

    @Override
    protected IAjaxCallDecorator getAjaxCallDecorator() {
        return decorator;
    }

    protected void showFeedbackLabels(final AjaxRequestTarget target, Form form) {
        form.visitChildren(ELTFeedbackLabel.class, new IVisitor<ELTFeedbackLabel, Void>() {

            @Override
            public void component(ELTFeedbackLabel object, IVisit<Void> visit) {
                if (object.isVisibleInHierarchy()) {
                    target.add(object);
                } else {
                    LOGGER.debug("Trying to render feedback label for component {}, label is invisible.", 
                            object.getFeedbackSource().getId());
                }
            }
        });
    }

    @Override
    protected void onError(AjaxRequestTarget target, Form<?> form) {
        ELTAlerts.renderErrorPopup(getString("validationFailedMessage"), target);
        showFeedbackLabels(target, form);
    }

    /**
     * Gets model
     *
     * @return model
     */
    @SuppressWarnings("unchecked")
    public final IModel<T> getModel() {
        return (IModel<T>) getDefaultModel();
    }

    /**
     * Sets model
     *
     * @param model model
     */
    public final void setModel(IModel<T> model) {
        setDefaultModel(model);
    }

    /**
     * Gets model object
     *
     * @return model object
     */
    @SuppressWarnings("unchecked")
    public final T getModelObject() {
        return (T) getDefaultModelObject();
    }

    /**
     * Sets model object
     *
     * @param object object
     */
    public final void setModelObject(T object) {
        setDefaultModelObject(object);
    }
}
