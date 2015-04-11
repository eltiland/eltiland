package com.eltiland.ui.magazine.panels;

import com.eltiland.bl.FileManager;
import com.eltiland.model.file.File;
import com.eltiland.model.magazine.Magazine;
import com.eltiland.ui.common.BaseEltilandPanel;
import com.eltiland.ui.common.components.avatar.AvatarPreviewPanel;
import com.eltiland.ui.common.components.button.EltiAjaxLink;
import com.eltiland.ui.common.model.GenericDBModel;
import com.eltiland.ui.magazine.MagazineAboutPage;
import com.eltiland.utils.UrlUtils;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 * Panel for output one magazine.
 *
 * @author Aleksey Plotnikov.
 */
public abstract class MagazineItemPanel extends BaseEltilandPanel<Magazine> {
    @SpringBean
    private FileManager fileManager;

    private boolean isSelected;

    private AvatarPreviewPanel coverPanel = new AvatarPreviewPanel("cover",
            UrlUtils.StandardIcons.ICONS_IMAGE) {
        @Override
        protected boolean isDescriptionVisible() {
            return false;
        }

        @Override
        protected boolean outputFullVersion() {
            return true;
        }
    };

    public MagazineItemPanel(String id, IModel<Magazine> magazineIModel, final boolean selected) {
        super(id, magazineIModel);
        isSelected = selected;
        add(coverPanel.setOutputMarkupId(true));

        Magazine magazine = magazineIModel.getObject();
        if (magazine.getCover() != null) {
            File file = fileManager.getFileById(magazine.getCover().getId());
            coverPanel.initEditMode(new GenericDBModel<>(File.class, file));
        }

        add(new Label("name", magazine.getName()));
        add(new Label("description", String.format(getString("topic"), magazine.getTopic())));
        add(new EltiAjaxLink("aboutLink") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                throw new RestartResponseException(MagazineAboutPage.class,
                        new PageParameters().add(
                                MagazineAboutPage.PARAM_ID, MagazineItemPanel.this.getModelObject().getId()));
            }
        });

        add(new Label("price", String.format(getString("priceValue"), magazine.getPrice().toString())));

        final WebMarkupContainer checkBox = new WebMarkupContainer("checkBox");
        checkBox.add(new AttributeModifier("class", isSelected ? "checkBox selected" : "checkBox unselected"));
        add(checkBox);

        checkBox.add(new AjaxEventBehavior("onclick") {
            @Override
            protected void onEvent(AjaxRequestTarget target) {
                isSelected = !isSelected;
                checkBox.add(new AttributeModifier("class", isSelected ? "checkBox selected" : "checkBox unselected"));
                target.add(checkBox);
                onSelected(target, isSelected);
            }
        });
    }

    public abstract void onSelected(AjaxRequestTarget target, boolean value);
}
