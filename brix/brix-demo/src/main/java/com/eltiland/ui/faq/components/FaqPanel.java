package com.eltiland.ui.faq.components;

import com.eltiland.model.faq.Faq;
import com.eltiland.ui.common.BaseEltilandPanel;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AbstractDefaultAjaxBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

/**
 * Panel for output question and answer.
 *
 * @author Aleksey Plotnikov.
 */
public class FaqPanel extends BaseEltilandPanel<Faq> {

    /**
     * Panel constructor.
     *
     * @param id        markup id.
     * @param faqIModel FAQ model.
     */
    public FaqPanel(String id, IModel<Faq> faqIModel) {
        super(id, faqIModel);

        final FaqAnswerPanel faqAnswerPanel = new FaqAnswerPanel("answerPanel", faqIModel);
        faqAnswerPanel.add(new AttributeModifier("id", new Model<>(getMarkupFaqId())));

        WebMarkupContainer link = new WebMarkupContainer("questionLink");
        link.add(new Label("questionText", faqIModel.getObject().getQuestion()));
        link.add(new AttributeModifier("id", new Model<>(getMarkupFaqLinkId())));

        link.add(new AbstractDefaultAjaxBehavior() {
            @Override
            protected void onComponentTag(ComponentTag tag) {
                String js = "if(document.getElementById('" + getMarkupFaqId() + "').offsetWidth>0) " +
                        "{$('#" + getMarkupFaqId() + "').hide('slow')} " +
                        "else {$('#" + getMarkupFaqId() + "').show('slow')}";
                tag.put("onclick", js);
            }

            @Override
            protected void respond(AjaxRequestTarget target) {
            }
        });
        add(link);
        add(faqAnswerPanel);
    }

    private class FaqAnswerPanel extends BaseEltilandPanel<Faq> {
        protected FaqAnswerPanel(String id, IModel<Faq> faqIModel) {
            super(id, faqIModel);

            add(new Label("answerText", faqIModel.getObject().getAnswer()));
        }
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
        response.renderOnDomReadyJavaScript("$('#" + getMarkupFaqId() + "').hide()");
        response.renderOnDomReadyJavaScript(
                "document.getElementById('" + getMarkupFaqLinkId() + "').style.cursor='pointer';");
    }

    private String getMarkupFaqId() {
        return "faq" + getModelObject().getNumber();
    }

    private String getMarkupFaqLinkId() {
        return "faqlink" + getModelObject().getNumber();
    }
}
