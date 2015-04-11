package com.eltiland.ui.common.components.menu;

import com.eltiland.ui.common.BaseEltilandPage;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Internal menu data class.
 *
 * @author Aleksey Plotnikov.
 */
class MenuData implements Serializable {
    private Class<? extends BaseEltilandPage> clazz;
    private String caption;
    private List<MenuData> subItems = new ArrayList<>();

    public MenuData(Class<? extends BaseEltilandPage> clazz, String caption) {
        this.clazz = clazz;
        this.caption = caption;
    }

    public MenuData(String caption, List<MenuData> subItems) {
        this.caption = caption;
        this.subItems = subItems;
    }

    public Class<? extends BaseEltilandPage> getClazz() {
        return clazz;
    }

    public String getCaption() {
        return caption;
    }

    public List<MenuData> getSubItems() {
        return subItems;
    }

    public void setSubItems(List<MenuData> subItems) {
        this.subItems = subItems;
    }
}
