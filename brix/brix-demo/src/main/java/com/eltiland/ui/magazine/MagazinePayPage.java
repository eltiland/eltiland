package com.eltiland.ui.magazine;

import com.eltiland.bl.GenericManager;
import com.eltiland.model.magazine.Client;
import com.eltiland.model.magazine.Magazine;
import com.eltiland.ui.common.TwoColumnPage;
import com.eltiland.ui.common.components.button.back.BackButton;
import com.eltiland.ui.common.components.button.paybuttons.MagazinePayButton;
import com.eltiland.ui.common.model.GenericDBListModel;
import com.eltiland.ui.common.model.GenericDBModel;
import org.apache.wicket.Application;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.math.BigDecimal;
import java.util.ArrayList;

/**
 * Page for paying for magazines.
 *
 * @author Aleksey Plotnikov.
 */
public class MagazinePayPage extends TwoColumnPage {

    @SpringBean
    private GenericManager genericManager;

    public static String MOUNT_PATH = "magPay";

    public static final String PARAM_ID = "id";

    private Label totalLabel = new Label("total", new Model<String>());
    private int index = 1;

    private IModel<Client> clientIModel = new GenericDBModel<>(Client.class);

    private MagazinePayButton magazinePayButton = new MagazinePayButton("payButton");

    public MagazinePayPage(PageParameters parameters) {
        super(parameters);

        if (parameters.isEmpty()) {
            throw new WicketRuntimeException("parameters must not be empty");
        }
        Long id = parameters.get(PARAM_ID).toLong();

        //Check input parameter
        if (id == null) {
            throw new RestartResponseException(Application.get().getApplicationSettings().getInternalErrorPage());
        }

        clientIModel.setObject(genericManager.getObject(Client.class, id));
        if (clientIModel.getObject() == null) {
            throw new RestartResponseException(Application.get().getApplicationSettings().getInternalErrorPage());
        }


        Client client = clientIModel.getObject();
        genericManager.initialize(client, client.getMagazines());

        add(new ListView<Magazine>("magazineList",
                new GenericDBListModel<>(Magazine.class, new ArrayList<>(client.getMagazines()))) {
            @Override
            protected void populateItem(ListItem<Magazine> components) {
                Client client = clientIModel.getObject();
                components.add(new Label("magazineItem", String.format(getString("selected"),
                        String.valueOf(index++),
                        components.getModel().getObject().getName(),
                        components.getModelObject().getPrice().toString())));
                genericManager.initialize(client, client.getMagazines());
                if (index > client.getMagazines().size()) {
                    index = 1;
                }
            }
        });

        add(new BackButton("backButton"));
        add(totalLabel);
        add(magazinePayButton);

        BigDecimal total = BigDecimal.valueOf(0);
        for (Magazine magazine : client.getMagazines()) {
            total = total.add(magazine.getPrice());
        }

        magazinePayButton.setPaymentData(client);

        totalLabel.setDefaultModelObject(String.format(getString("all"), total.toString()));
    }
}
