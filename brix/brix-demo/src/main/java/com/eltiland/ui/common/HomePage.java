package com.eltiland.ui.common;

import com.eltiland.BrixPanel;
import com.eltiland.bl.PropertyManager;
import com.eltiland.model.Property;
import com.eltiland.ui.common.components.slider.ELTSliderPanel;
import com.eltiland.utils.UrlUtils;
import org.apache.wicket.spring.injection.annot.SpringBean;


/**
 * Eltiland home page.
 */
public class HomePage extends TwoColumnPage {

    @SpringBean
    private PropertyManager propertyManager;

    public static final String MOUNT_PATH = "/home";

    /**
     * Construct.
     */
    public HomePage() {
        super();

        final boolean showSlider = Boolean.parseBoolean(propertyManager.getProperty(Property.SHOW_SLIDER));
        add(new ELTSliderPanel("slider") {
            @Override
            public boolean isVisible() {
                return showSlider;
            }
        });
        add(new BrixPanel("articlesCmsPanel", UrlUtils.createBrixPathForPanel("HOME/articles.html")));
        add(new BrixPanel("forewordCmsPanel", UrlUtils.createBrixPathForPanel("HOME/foreword.html")));
    }
}
