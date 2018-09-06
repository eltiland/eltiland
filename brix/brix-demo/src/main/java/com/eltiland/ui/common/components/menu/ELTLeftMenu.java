package com.eltiland.ui.common.components.menu;

import com.eltiland.ui.common.BaseEltilandPage;
import com.eltiland.ui.common.TeachingPage;
import com.eltiland.ui.common.compositepage.children.ChildrenPage;
import com.eltiland.ui.common.compositepage.parents.ParentsPage;
import com.eltiland.ui.common.compositepage.teachers.TeachersPage;
import com.eltiland.ui.forum.ForumPage;
import com.eltiland.ui.video.VideoPage;
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
                        new MenuData(TeachingPage.class, getString("aboutPage")),
                        new MenuData(ParentsPage.class, getString("parentsPage")),
                        new MenuData(ChildrenPage.class, getString("childPage")),
                        new MenuData(TeachersPage.class, getString("teachersPage")),
                        new MenuData(VideoPage.class, getString("videoPage")),
                        new MenuData(ForumPage.class, getString("forumPage"))));
            }
        };
    }

    @Override
    protected String getMenuStyle() {
        return "sideMenu";
    }
}
