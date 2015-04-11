package com.eltiland.ui.google.tile;

import org.apache.wicket.Component;
import org.apache.wicket.model.IModel;
import org.brixcms.jcr.wrapper.BrixNode;
import org.brixcms.plugin.site.page.tile.Tile;
import org.brixcms.plugin.site.page.tile.admin.TileEditorPanel;

/**
 * Google page tile for admin.
 *
 * @author Aleksey Plotnikov.
 */
public class GoogleTile implements Tile {
    @Override
    public String getDisplayName() {
        return "Google Page Tile";
    }

    @Override
    public String getTypeName() {
        return "com.eltiland.ui.google.tile.GoogleTile";
    }

    @Override
    public TileEditorPanel newEditor(String id, IModel<BrixNode> tileContainerNode) {
        return new GoogleTileEditor(id);
    }

    @Override
    public Component newViewer(String id, IModel<BrixNode> tileNode) {
        return new GoogleTilePanel(id, tileNode);
    }

    @Override
    public boolean requiresSSL(IModel<BrixNode> tileNode) {
        return false;
    }
}
