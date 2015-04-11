package com.eltiland.ui.google.tile;

import com.eltiland.bl.GenericManager;
import com.eltiland.bl.drive.GooglePageManager;
import com.eltiland.model.google.GoogleDriveFile;
import com.eltiland.model.google.GooglePage;
import com.eltiland.ui.common.model.GenericDBModel;
import com.eltiland.ui.google.ELTGoogleDriveEditor;
import org.apache.wicket.markup.html.panel.EmptyPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.brixcms.jcr.api.JcrNode;
import org.brixcms.jcr.wrapper.BrixNode;

/**
 * Google view panel.
 *
 * @author Aleksey Plotnikov.
 */
public class GoogleTilePanel extends Panel {
    @SpringBean
    private GenericManager genericManager;
    @SpringBean
    private GooglePageManager googlePageManager;

    private IModel<GooglePage> pageModel = new GenericDBModel<>(GooglePage.class);

    public GoogleTilePanel(String id, IModel<BrixNode> model) {
        super(id, model);

        JcrNode tileNode = model.getObject();
        String name = tileNode.getProperty("name").getString();
        if (name != null) {
            GooglePage page = googlePageManager.getPageByName(name);
            if (page != null) {
                genericManager.initialize(page, page.getContent());
                pageModel.setObject(page);
                add(new ELTGoogleDriveEditor("googlePanel",
                        new GenericDBModel<>(GoogleDriveFile.class, page.getContent()),
                        ELTGoogleDriveEditor.MODE.VIEW, GoogleDriveFile.TYPE.DOCUMENT));
            } else {
                add(new EmptyPanel("googlePanel"));
            }
        } else {
            add(new EmptyPanel("googlePanel"));
        }
    }
}
