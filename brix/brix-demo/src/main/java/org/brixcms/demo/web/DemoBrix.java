/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.brixcms.demo.web;

import brix.tinymce.TinyMceMarkupEditorFactory;
import com.eltiland.ui.course.plugin.Courses2Plugin;
import com.eltiland.ui.course.plugin.CoursesPlugin;
import com.eltiland.ui.faq.plugin.FaqPlugin;
import com.eltiland.ui.google.plugin.GooglePlugin;
import com.eltiland.ui.google.tile.GoogleTile;
import com.eltiland.ui.magazine.plugin.MagazinePlugin;
import com.eltiland.ui.news.plugin.NewsPlugin;
import com.eltiland.ui.pei.plugin.PeiPlugin;
import com.eltiland.ui.service.plugin.MigrationPlugin;
import com.eltiland.ui.service.plugin.ServicePlugin;
import com.eltiland.ui.slider.plugin.SliderPlugin;
import com.eltiland.ui.subscribe.plugin.SubscribePlugin;
import com.eltiland.ui.tags.plugin.TagPlugin;
import com.eltiland.ui.users.plugin.UserPlugin;
import com.eltiland.ui.webinars.plugin.WebinarsPlugin;
import org.brixcms.Brix;
import org.brixcms.Plugin;
import org.brixcms.auth.AuthorizationStrategy;
import org.brixcms.config.BrixConfig;
import org.brixcms.plugin.site.page.admin.MarkupEditorFactory;
import org.brixcms.plugin.site.page.tile.Tile;

/**
 * Subclass of {@link Brix} that configures demo-specific settings such as plugins, tiles, etc.
 *
 * @author igor.vaynberg
 */
public class DemoBrix extends Brix {
    /**
     * Constructor
     *
     * @param config
     */
    public DemoBrix(BrixConfig config) {
        super(config);

        // register plugins
        config.getRegistry().register(Plugin.POINT, new GooglePlugin());
        config.getRegistry().register(Plugin.POINT, new NewsPlugin(this));
        config.getRegistry().register(Plugin.POINT, new PeiPlugin());
        config.getRegistry().register(Plugin.POINT, new UserPlugin());
        config.getRegistry().register(Plugin.POINT, new CoursesPlugin());
        config.getRegistry().register(Plugin.POINT, new Courses2Plugin());
        config.getRegistry().register(Plugin.POINT, new FaqPlugin());
        config.getRegistry().register(Plugin.POINT, new WebinarsPlugin());
        config.getRegistry().register(Plugin.POINT, new SubscribePlugin());
        config.getRegistry().register(Plugin.POINT, new MagazinePlugin());
        config.getRegistry().register(Plugin.POINT, new SliderPlugin());
        config.getRegistry().register(Plugin.POINT, new TagPlugin());
        config.getRegistry().register(Plugin.POINT, new ServicePlugin());
        config.getRegistry().register(Plugin.POINT, new MigrationPlugin());
        config.getRegistry().register(MarkupEditorFactory.POINT, new TinyMceMarkupEditorFactory());


        // register tiles
        config.getRegistry().register(Tile.POINT, new GoogleTile());

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AuthorizationStrategy newAuthorizationStrategy() {
        // register our simple demo auth strategy
        return new DemoAuthorizationStrategy();
    }
}