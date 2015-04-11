package com.eltiland.ui.magazine;

import com.eltiland.bl.GenericManager;
import com.eltiland.bl.impl.integration.FileUtility;
import com.eltiland.bl.magazine.ClientManager;
import com.eltiland.exceptions.EltilandManagerException;
import com.eltiland.model.magazine.Client;
import com.eltiland.model.magazine.Magazine;
import com.eltiland.ui.common.TwoColumnPage;
import com.eltiland.ui.common.components.button.AjaxDownloadLink;
import com.eltiland.ui.common.components.dialog.ELTAlerts;
import com.eltiland.ui.common.model.GenericDBListModel;
import com.eltiland.ui.common.model.GenericDBModel;
import org.apache.wicket.Application;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.resource.IResourceStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;

/**
 * Page for donwloading magazines.
 *
 * @author Aleksey Plotnikov.
 */
public class MagazineDownloadPage extends TwoColumnPage {

    @SpringBean
    private ClientManager clientManager;
    @SpringBean
    private GenericManager genericManager;
    @SpringBean
    private FileUtility fileUtility;

    private static final Logger LOGGER = LoggerFactory.getLogger(MagazinePage.class);

    public static String MOUNT_PATH = "downloadMagazine";

    public static final String PARAM_ID = "id";

    private IModel<Client> clientIModel = new GenericDBModel<>(Client.class);

    public MagazineDownloadPage(PageParameters parameters) {
        super(parameters);

        if (parameters.isEmpty()) {
            throw new WicketRuntimeException("parameters must not be empty");
        }
        String id = parameters.get(PARAM_ID).toString();

        //Check input parameter
        if (id == null) {
            throw new RestartResponseException(Application.get().getApplicationSettings().getInternalErrorPage());
        }

        clientIModel.setObject(clientManager.getClientByCode(id));
        if (clientIModel.getObject() == null) {
            throw new RestartResponseException(Application.get().getApplicationSettings().getInternalErrorPage());
        }

        Client client = clientIModel.getObject();
        genericManager.initialize(client, client.getMagazines());

        add(new ListView<Magazine>("magazineList",
                new GenericDBListModel<>(Magazine.class, new ArrayList<>(client.getMagazines()))) {
            @Override
            protected void populateItem(final ListItem<Magazine> components) {
                AjaxDownloadLink link = new AjaxDownloadLink("link") {
                    @Override
                    public String getFileName() {
                        genericManager.initialize(components.getModelObject(), components.getModelObject().getContent());
                        String string = components.getModelObject().getContent().getName();
                        return string;
                    }

                    @Override
                    public IResourceStream getResourceStream() {
                        Magazine magazine = components.getModelObject();
                        genericManager.initialize(magazine, magazine.getContent());
                        genericManager.initialize(magazine.getContent(), magazine.getContent().getBody());

                        IResourceStream resourceStream =
                                fileUtility.getFileResource(magazine.getContent().getBody().getHash());
                        return resourceStream;
                    }

                    @Override
                    public void onClick(AjaxRequestTarget target) {
                        Client client = clientIModel.getObject();
                        genericManager.initialize(client, client.getDownloadedMagazines());
                        if (client.getDownloadedMagazines().contains(components.getModelObject())) {
                            ELTAlerts.renderErrorPopup(getString("error"), target);
                        } else {
                            super.onClick(target);
                            client.getDownloadedMagazines().add(components.getModelObject());
                            try {
                                clientManager.updateClient(client);
                            } catch (EltilandManagerException e) {
                                LOGGER.error("Cannot update client entity", e);
                                throw new WicketRuntimeException("Cannot update client entity", e);
                            }
                        }
                    }
                };
                link.add(new Label("downloadLabel",
                        String.format(getString("link"), components.getModelObject().getName())));

                components.add(link);
            }
        });
    }
}
