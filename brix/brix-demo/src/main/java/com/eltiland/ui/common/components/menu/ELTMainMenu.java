package com.eltiland.ui.common.components.menu;

import com.eltiland.ui.common.*;
import com.eltiland.ui.course.CourseListPage;
import com.eltiland.ui.course.TeachingModulesPage;
import com.eltiland.ui.faq.FaqPage;
import com.eltiland.ui.forum.ForumPage;
import com.eltiland.ui.library.LibraryPage;
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
                        new MenuData(TrainingPage.class, getString("trainPage")),
                        new MenuData(CourseListPage.class, getString("coursesPage")),
                        new MenuData(TeachingModulesPage.class, getString("modulePage")),
                        new MenuData(WebinarsPage.class, getString("webinarsPage")),
                        new MenuData(LibraryPage.class, getString("libraryPage")),
                        new MenuData(FaqPage.class, getString("faqPage")),
                        new MenuData(ForumPage.class, getString("forumPage"))));
            }
        };
    }

    @Override
    protected String getListStyle() {
        return "menuContainer";
    }
}
