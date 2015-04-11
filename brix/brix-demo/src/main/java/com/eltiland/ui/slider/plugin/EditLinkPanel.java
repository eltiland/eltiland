package com.eltiland.ui.slider.plugin;

import com.eltiland.model.Slider;
import com.eltiland.ui.common.BaseEltilandPanel;
import com.eltiland.ui.common.components.button.EltiAjaxLink;
import com.eltiland.ui.common.components.dialog.ELTDialogPanel;
import com.eltiland.ui.common.components.dialog.callback.IDialogUpdateCallback;
import com.eltiland.ui.common.components.textfield.ELTTextField;
import com.eltiland.ui.common.model.GenericDBModel;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Paid Groups management panel.
 */
class EditLinkPanel extends ELTDialogPanel implements IDialogUpdateCallback<Slider> {

    private IDialogActionProcessor<Slider> callback;
    private ELTTextField<String> linkField = new ELTTextField<>(
            "linkField", new ResourceModel("linkField"), new Model<String>(), String.class, true);

    /**
     * Panel constructor.
     *
     * @param id markup id.
     */
    public EditLinkPanel(String id) {
        super(id, new GenericDBModel<>(Slider.class));
        form.add(linkField);
    }

    public void initData(IModel<Slider> sliderModel) {
        setModelObject(sliderModel.getObject());
        linkField.setModelObject(sliderModel.getObject().getLink());
    }

    @Override
    protected String getHeader() {
        return getString("editLinkHeader");
    }

    @Override
    protected List<EVENT> getActionList() {
        return new ArrayList<>(Arrays.asList(EVENT.Save));
    }

    @Override
    protected void eventHandler(EVENT event, AjaxRequestTarget target) {
        Slider slider = (Slider) getModelObject();
        String link = linkField.getModelObject();
        if (!(link.startsWith("http://") || link.startsWith("https://"))) {
            link = "http://" + link;
        }
        slider.setLink(link);
        callback.process(new GenericDBModel<>(Slider.class, slider), target);
    }

    @Override
    public void setUpdateCallback(IDialogActionProcessor<Slider> callback) {
        this.callback = callback;
    }
}
