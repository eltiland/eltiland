package com.eltiland.ui.common.components.behavior;

import org.apache.wicket.Component;
import org.apache.wicket.behavior.Behavior;

/**
 * A behavior for a single component.
 */
public abstract class SingleComponentBehavior extends Behavior {
    private Component component = null;

    @Override
    public void bind(Component component) {
        if (this.component != null && component != this.component) {
            throw new IllegalStateException("This behavior can be attached to only one component");
        }
        component.setOutputMarkupId(true);
        this.component = component;
    }

    public Component getComponent() {
        return component;
    }
}
