package com.eltiland.ui.common.components;

import org.apache.wicket.Component;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.IModelComparator;

/**
 * Utility class used for purposes when we create instances of readonly objects multiple times,
 * but one instance will be enough. <br />
 * Examples:
 * <ul>
 * <li>Action columns don't have titles but we need to provide empty model for each column.
 * Use {@link ReadonlyObjects#EMPTY_DISPLAY_MODEL}</li>
 * <li> {@link ReadonlyObjects#REFERENCE_COMPARATOR}</li>
 * </ul>
 */
public final class ReadonlyObjects {
    /**
     * Empty display readonly model.
     */
    public static final IModel<String> EMPTY_DISPLAY_MODEL = new AbstractReadOnlyModel<String>() {
        @Override
        public String getObject() {
            return "";
        }
    };
    /**
     * Compare model objects by reference.
     */
    public static final IModelComparator REFERENCE_COMPARATOR = new IModelComparator() {
        @Override
        public boolean compare(Component component, Object newObject) {
            // compare by reference
            return component.getDefaultModelObject() == newObject;
        }
    };

    private ReadonlyObjects() {
    }
}
