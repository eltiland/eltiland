package com.eltiland.ui.common.components.menu;

import com.eltiland.ui.common.BaseEltilandPage;
import com.eltiland.ui.common.pages.DocumentsPage;
import com.eltiland.ui.common.pages.EducationPage;
import com.eltiland.ui.common.pages.InfoPage;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Side panel for home page.
 *
 * @author Aleksey Plotnikov.
 */
public class ELTLeftMenu extends ELTMenu {
    public ELTLeftMenu(String id, final Class<? extends BaseEltilandPage> clazz) {
        super(id, clazz);
    }

    @Override
    protected IModel<List<MenuData>> getMenuItems() {
        return new LoadableDetachableModel<List<MenuData>>() {
            @Override
            protected List<MenuData> load() {
                return new ArrayList<>(Arrays.asList(
                        new MenuData(InfoPage.class, getString("infoPage")),
                        new MenuData(EducationPage.class, getString("educationPage")),
                        new MenuData(DocumentsPage.class, getString("documentsPage"))));
            }
        };
    }

    @Override
    protected String getMenuStyle() {
        return "sideMenu";
    }
}
