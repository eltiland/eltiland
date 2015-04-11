package com.eltiland.ui.google.tile;

import com.eltiland.model.google.GooglePage;
import com.eltiland.ui.common.components.button.EltiAjaxLink;
import com.eltiland.ui.common.components.dialog.Dialog;
import com.eltiland.ui.common.components.dialog.callback.IDialogSelectCallback;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.brixcms.jcr.wrapper.BrixNode;
import org.brixcms.plugin.site.page.tile.admin.TileEditorPanel;

/**
 * Google tile element editor.
 *
 * @author Aleksey Plotnikov.
 */
public class GoogleTileEditor extends TileEditorPanel {
    private String name;

    private TextField nameField = new TextField<>("name", new PropertyModel<String>(this, "name"));

    private Dialog<GooglePanelSelector> googlePanelSelectorDialog =
            new Dialog<GooglePanelSelector>("googlePageSelectorDialog", 385) {
                @Override
                public GooglePanelSelector createDialogPanel(String id) {
                    return new GooglePanelSelector(id);
                }

                @Override
                public void registerCallback(GooglePanelSelector panel) {
                    super.registerCallback(panel);
                    panel.setSelectCallback(new IDialogSelectCallback.IDialogActionProcessor<GooglePage>() {
                        @Override
                        public void process(IModel<GooglePage> model, AjaxRequestTarget target) {
                            close(target);
                            nameField.setModelObject(model.getObject().getName());
                            target.add(nameField);
                        }
                    });
                }
            };

    private EltiAjaxLink selectButton = new EltiAjaxLink("select") {
        @Override
        public void onClick(AjaxRequestTarget target) {
            googlePanelSelectorDialog.show(target);
        }
    };

    public GoogleTileEditor(String id) {
        super(id);
        add(nameField);
        add(selectButton);
        add(googlePanelSelectorDialog);
        nameField.setEnabled(false);
        nameField.setOutputMarkupId(true);
    }

    @Override
    public void load(BrixNode node) {
        if (node.hasProperty("name")) {
            name = node.getProperty("name").getString();
        }
    }

    @Override
    public void save(BrixNode node) {
        node.setProperty("name", name);
    }
}
