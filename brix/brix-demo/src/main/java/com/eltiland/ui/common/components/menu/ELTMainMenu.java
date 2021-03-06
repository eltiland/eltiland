package com.eltiland.ui.common.components.menu;

import com.eltiland.ui.common.*;
import com.eltiland.ui.course.TeachingModulesPage;
import com.eltiland.ui.webinars.WebinarsPage;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Eltiland main menu control.
 *
 * @author Aleksey Plotnikov.
 */
public class ELTMainMenu extends ELTMenu {
    /**
     * Panel constructor.
     *
     * @param id markup id.
     */
    public ELTMainMenu(String id, final Class<? extends BaseEltilandPage> clazz) {
        super(id, clazz);
    }

    @Override
    protected IModel<List<MenuData>> getMenuItems() {
        return new LoadableDetachableModel<List<MenuData>>() {
            @Override
            protected List<MenuData> load() {
                return new ArrayList<>(Arrays.asList(
                        new MenuData(HomePage.class, getString("homePage")),
                        new MenuData(FestivalPage.class, getString("festivalPage")),
                        new MenuData(TrainingPage.class, getString("trainPage")),
                        new MenuData(SeminarsPage.class, getString("seminarsPage")),
                        new MenuData(TeachingModulesPage.class, getString("modulePage")),
                        new MenuData(WebinarsPage.class, getString("webinarsPage"))));
            }
        };
    }

    @Override
    protected String getListStyle() {
        return "menuContainer";
    }
}
