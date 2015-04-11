package com.eltiland.ui.library.components.switchview;

import com.eltiland.ui.common.BaseEltilandPanel;
import com.eltiland.ui.library.LibraryView;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.model.Model;

/**
 * Panel for switching view of records.
 *
 * @author Aleksey Plotnikov.
 */
public abstract class SwitchViewPanel extends BaseEltilandPanel {

    private static final String CSS = "static/css/library/switch.css";

    private WebMarkupContainer listButton = new WebMarkupContainer("listButton");
    private WebMarkupContainer galleryButton = new WebMarkupContainer("galleryButton");
    private WebMarkupContainer gridButton = new WebMarkupContainer("gridButton");

    /**
     * Panel ctor.
     *
     * @param id markup id.
     */
    public SwitchViewPanel(String id, LibraryView view) {
        super(id);

        listButton.add(new AjaxEventBehavior("onclick") {
            @Override
            protected void onEvent(AjaxRequestTarget target) {
                onChangeView(LibraryView.LIST, target);
            }
        });

        galleryButton.add(new AjaxEventBehavior("onclick") {
            @Override
            protected void onEvent(AjaxRequestTarget target) {
                onChangeView(LibraryView.GALLERY, target);
            }
        });

        gridButton.add(new AjaxEventBehavior("onclick") {
            @Override
            protected void onEvent(AjaxRequestTarget target) {
                onChangeView(LibraryView.GRID, target);
            }
        });

        add(listButton);
        add(galleryButton);
        add(gridButton);

        changeButtonState(view);
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
        response.renderCSSReference(CSS);
    }

    public void changeButtonState(LibraryView view) {
        listButton.add(new AttributeModifier("class", "switch_button list"));
        galleryButton.add(new AttributeModifier("class", "switch_button gallery"));
        gridButton.add(new AttributeModifier("class", "switch_button grid"));

        WebMarkupContainer activeContainer = null;
        if (view.equals(LibraryView.LIST)) {
            activeContainer = listButton;
        } else if (view.equals(LibraryView.GALLERY)) {
            activeContainer = galleryButton;
        } else if (view.equals(LibraryView.GRID)) {
            activeContainer = gridButton;
        }
        if (activeContainer != null) {
            activeContainer.add(new AttributeAppender("class", new Model<>("active"), " "));
        }
    }

    /**
     * change view handler.
     *
     * @param view   new view.
     * @param target ajax request target.
     */
    protected abstract void onChangeView(LibraryView view, AjaxRequestTarget target);
}
