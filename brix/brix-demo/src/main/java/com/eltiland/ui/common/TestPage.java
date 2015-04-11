package com.eltiland.ui.common;

import org.apache.wicket.extensions.markup.html.repeater.data.table.DataTable;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.PropertyColumn;
import org.apache.wicket.markup.repeater.data.IDataProvider;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.util.ListModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Aleks
 * Date: 23.01.15
 * Time: 11:50
 * To change this template use File | Settings | File Templates.
 */

class Man {
    private String name = "";

    public Man(String name) {
        this.name = name;
    }
}

class ManManager {
    private static ManManager instance;
    private List<Man> men;

    public static ManManager get() {
        if (instance == null) {
            instance = new ManManager();
        }

        return instance;
    }

    public ManManager() {
        men = new ArrayList<>();
        men.add(new Man("Bob"));
        men.add(new Man("Marry"));
        men.add(new Man("Dog"));
    }

    public List<Man> getMen() {
        return men;
    }
}

class TransientModel<T> extends AbstractReadOnlyModel<T> {
    private transient T object;

    public TransientModel(T object) {
        this.object = object;
    }

    @Override
    public T getObject() {
        if (object == null) {
            throw new NullPointerException("TransientModel object is detached");
        }
        return object;
    }
}

public class TestPage extends TwoColumnPage {
    public static final String MOUNT_PATH = "/test";

    public TestPage(PageParameters parameters) {
        super(parameters);

        List<Man> men = ManManager.get().getMen();
        final IModel<List<Man>> menModel = new ListModel<>(men);

        List<IColumn<Man>> columns = new ArrayList<>();
        columns.add(new PropertyColumn<Man>(Model.of("Name"), "name"));
        IDataProvider<Man> provider = new IDataProvider<Man>() {
            @Override
            public Iterator<? extends Man> iterator(int i, int i1) {
                return menModel.getObject().iterator();
            }

            @Override
            public int size() {
                return menModel.getObject().size();
            }

            @Override
            public IModel<Man> model(Man man) {
                return new TransientModel<Man>(man);
            }

            @Override
            public void detach() {

            }
        };

        DataTable<Man> table = new DataTable<>("dataTable", columns, provider, 20);
        add(table);
    }
}
