package com.eltiland.ui.common.components.menu;

import java.io.Serializable;

/**
 * Menu item structure for tab menu.
 *
 * @author Aleksey Plotnikov.
 */
public class TabMenuData implements Serializable {
    private short index;
    private String caption;

    public TabMenuData(short index, String caption) {
        this.index = index;
        this.caption = caption;
    }

    public short getIndex() {
        return index;
    }

    public void setIndex(short index) {
        this.index = index;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }
}
