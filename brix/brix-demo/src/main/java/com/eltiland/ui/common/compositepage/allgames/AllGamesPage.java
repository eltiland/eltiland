package com.eltiland.ui.common.compositepage.allgames;

import com.eltiland.BrixPanel;
import com.eltiland.ui.common.TwoColumnPage;
import com.eltiland.utils.UrlUtils;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.WebMarkupContainer;

/**
 */
public class AllGamesPage extends TwoColumnPage {

    public static final String MOUNT_PATH = "/games";

    public AllGamesPage() {
        add(new BrixPanel("aboutFlashGameKoza", UrlUtils.createBrixPathForPanel("ALLGAMES/volk_koza_kapusta.html")));
        add(new BrixPanel("aboutFlashGameLightbot", UrlUtils.createBrixPathForPanel("ALLGAMES/lightbot.html")));

        add(new BrixPanel("aboutFlashGameFrogs", UrlUtils.createBrixPathForPanel("ALLGAMES/frogs.html")));
        add(new WebMarkupContainer("flashGameFrogs").add(new AttributeModifier("src", UrlUtils.FROGS_GAME)));

        add(new BrixPanel("aboutFlashGameVolk", UrlUtils.createBrixPathForPanel("ALLGAMES/aboutFlashGameVolk.html")));
        add(new WebMarkupContainer("flashGameVolk").add(new AttributeModifier("src", UrlUtils.VOLK_KOZA_KAPUSTA_GAME_PATH)));

        add(new BrixPanel("aboutFlashGameHanoi", UrlUtils.createBrixPathForPanel("ALLGAMES/aboutFlashGameHanoi.html")));
        add(new WebMarkupContainer("flashGameHanoi").add(new AttributeModifier("src", UrlUtils.HANOI_GAME_PATH)));

        add(new BrixPanel("additionalBlock", UrlUtils.createBrixPathForPanel("ALLGAMES/additionalGames.html")));

    }
}
