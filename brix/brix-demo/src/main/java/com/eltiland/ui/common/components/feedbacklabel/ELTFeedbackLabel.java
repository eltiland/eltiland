package com.eltiland.ui.common.components.feedbacklabel;

import com.eltiland.bl.HtmlCleaner;
import com.eltiland.ui.common.components.UIConstants;
import com.eltiland.ui.common.components.behavior.TooltipBehavior;
import org.apache.wicket.Component;
import org.apache.wicket.feedback.FeedbackMessage;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>Base class for all Labels which support validation feedback messages.</p>
 * <p>During initialisation, FeedbackLabel take component-author of feedback messages ({@link #feedbackSource}}). feedbackSource
 * produce feedback messages is accessed via {@link org.apache.wicket.Component#getFeedbackMessage()} in normal way.
 * During rendering FeedbackLabel, at step
 * {@link org.apache.wicket.Component#onComponentTag(org.apache.wicket.markup.ComponentTag)} feedback message from
 * feedbackSource loaded in "title" attribute of FeedbackLabel tag and changed style of tag.</p>
 * <p>For JavaScript post processing FeedbackLabel tag, use &lt;div&gt; as outer html tag for FeedbackLabel.</p>
 *
 * @author Ihor Cherednichenko
 * @version 1.0
 */
public class ELTFeedbackLabel extends Label {
    private static final Logger LOGGER = LoggerFactory.getLogger(ELTFeedbackLabel.class);
    /**
     * Class to be applied to Error label on validation failure. Also used by JavaScript to setup jQuery-enabled
     * tooltips to show hover title with red background.
     */
    public static final String CSS_VALIDATION_ERROR_CLASS = "errorColor";

    @SpringBean
    private HtmlCleaner htmlCleaner;

    private Component feedbackSource;

    /**
     * Shorthand for fixed messages (not-localized). Use with care!
     */
    public ELTFeedbackLabel(String id, String label, Component feedbackSource) {
        this(id, new Model<>(label), feedbackSource);
    }

    /**
     * Constructor taking the component and arbitrary model. One may use {@link org.apache.wicket.model.ResourceModel}
     * for localized strings for instance.
     *
     * @param id             wicket component id
     * @param model          model to grab label from
     * @param feedbackSource source of the error messages to show here.
     */
    public ELTFeedbackLabel(String id, IModel<?> model, Component feedbackSource) {
        super(id, model);
        setOutputMarkupId(true);
        add(new TooltipBehavior(UIConstants.CSS_TOOLTIP_ERROR));

        this.feedbackSource = feedbackSource;
    }

    /**
     * @return {@code true} if feedbackSource has unrendered messages, {@code false} otherwise.
     */
    public boolean shouldBeRerendered() {
        return feedbackSource.getFeedbackMessage() != null && !feedbackSource.getFeedbackMessage().isRendered();
    }

    @Override
    protected void onComponentTag(ComponentTag tag) {
        super.onComponentTag(tag);

        if (!shouldBeRerendered()) {
            return;
        }
        FeedbackMessage fbMessage = feedbackSource.getFeedbackMessage();
        fbMessage.markRendered();

        if (feedbackSource.getFeedbackMessage().getMessage() == null) {
            LOGGER.warn("Unable to render component feedback message - it is null! Component is "
                    + feedbackSource.getPath());
        }

        String message = htmlCleaner.cleanHtml(fbMessage.getMessage().toString());

        tag.put("title", message);
        tag.put("class", CSS_VALIDATION_ERROR_CLASS);
    }

    public Component getFeedbackSource() {
        return feedbackSource;
    }
}
