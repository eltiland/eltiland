package com.eltiland.ui.magazine;

import com.eltiland.bl.GenericManager;
import com.eltiland.bl.magazine.ClientManager;
import com.eltiland.bl.magazine.MagazineManager;
import com.eltiland.exceptions.EltilandManagerException;
import com.eltiland.model.magazine.Client;
import com.eltiland.model.magazine.Magazine;
import com.eltiland.ui.common.TwoColumnPage;
import com.eltiland.ui.common.components.button.EltiAjaxLink;
import com.eltiland.ui.common.components.dialog.Dialog;
import com.eltiland.ui.common.components.dialog.callback.IDialogNewCallback;
import com.eltiland.ui.common.model.GenericDBModel;
import com.eltiland.ui.magazine.panels.ClientCreatePanel;
import com.eltiland.ui.magazine.panels.MagazineItemPanel;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.IAjaxCallDecorator;
import org.apache.wicket.ajax.markup.html.navigation.paging.AjaxPagingNavigator;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.PageableListView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Page for output list of the magazines.
 *
 * @author Aleksey Plotnikov.
 */
public class MagazinePage extends TwoColumnPage {
    @SpringBean
    private MagazineManager magazineManager;
    @SpringBean
    private ClientManager clientManager;
    @SpringBean
    private GenericManager genericManager;

    private static final Logger LOGGER = LoggerFactory.getLogger(MagazinePage.class);

    public static String MOUNT_PATH = "magazine";

    private static final int PAGE_COUNT = 10;

    private List<Long> idList = new ArrayList<>();

    private IModel<List<Magazine>> magazineList = new LoadableDetachableModel<List<Magazine>>() {
        @Override
        protected List<Magazine> load() {
            return magazineManager.getListOfMagazines();
        }
    };

    private Dialog<ClientCreatePanel> createClientDialog = new Dialog<ClientCreatePanel>("createClientDialog", 360) {
        @Override
        public ClientCreatePanel createDialogPanel(String id) {
            return new ClientCreatePanel(id);
        }

        @Override
        public void registerCallback(ClientCreatePanel panel) {
            panel.setNewCallback(new IDialogNewCallback.IDialogActionProcessor<Client>() {
                @Override
                public void process(IModel<Client> model, AjaxRequestTarget target) {
                    close(target);
                    Client client = model.getObject();
                    for (Long id : idList) {
                        Magazine magazine = genericManager.getObject(Magazine.class, id);
                        genericManager.initialize(client, client.getMagazines());
                        client.getMagazines().add(magazine);
                    }
                    try {
                        clientManager.updateClient(client);
                    } catch (EltilandManagerException e) {
                        LOGGER.error("Cannot update client entity", e);
                        throw new WicketRuntimeException("Cannot update client entity", e);
                    }
                    throw new RestartResponseException(MagazinePayPage.class,
                            new PageParameters().add(MagazinePayPage.PARAM_ID, client.getId()));
                }
            });
            super.registerCallback(panel);
        }
    };

    private EltiAjaxLink clientButton = new EltiAjaxLink("clientButton") {
        @Override
        public void onClick(AjaxRequestTarget ajaxRequestTarget) {
            createClientDialog.show(ajaxRequestTarget);
        }

        @Override
        public boolean isEnabled() {
            return !(idList.isEmpty());
        }

        @Override
        protected IAjaxCallDecorator getAjaxCallDecorator() {
            return null;
        }

        @Override
        public boolean isVisible() {
            return !(magazineList.getObject().isEmpty());
        }
    };

    public MagazinePage(PageParameters parameters) {
        super(parameters);

        PageableListView<Magazine> listView = new PageableListView<Magazine>("magazineList", magazineList, PAGE_COUNT) {
            @Override
            protected void populateItem(final ListItem item) {
                boolean selected = idList.contains(((Magazine) item.getModelObject()).getId());

                item.add(new MagazineItemPanel("magazineItem",
                        new GenericDBModel<>(Magazine.class, (Magazine) item.getModelObject()), selected) {
                    @Override
                    public void onSelected(AjaxRequestTarget target, boolean value) {
                        Magazine magazine = (Magazine) item.getModelObject();
                        if (value) {
                            idList.add(magazine.getId());
                        } else {
                            idList.remove(magazine.getId());
                        }
                        target.add(clientButton);
                    }
                });
            }
        };

        add(listView);

        add(new AjaxPagingNavigator("navigator", listView) {
            @Override
            public boolean isVisible() {
                return magazineManager.getMagazineCount() > PAGE_COUNT;
            }
        });

        add(clientButton.setOutputMarkupId(true));
        add(createClientDialog);
    }
}
