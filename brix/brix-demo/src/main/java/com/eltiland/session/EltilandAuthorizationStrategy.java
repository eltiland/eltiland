package com.eltiland.session;

import com.eltiland.ui.common.IAuthorizedPage;
import org.apache.wicket.Component;
import org.apache.wicket.Page;
import org.apache.wicket.authorization.Action;
import org.apache.wicket.authorization.IAuthorizationStrategy;
import org.apache.wicket.request.component.IRequestableComponent;

/**
 * First check of the authorization strategy.
 * <p/>
 * If anyone is trying to access any page except LoginPage or LogoutPage and they are not authenticated, disallow them
 * to do this.
 *
 * @author knorr
 * @version 1.0
 * @since 8/21/12
 */
public class EltilandAuthorizationStrategy implements IAuthorizationStrategy {

    /**
     * {@inheritDoc}
     */
    @Override
    public <T extends IRequestableComponent> boolean isInstantiationAuthorized(Class<T> componentClass) {
        if (!(Page.class.isAssignableFrom(componentClass))) {
            return true;
        }
        if (IAuthorizedPage.class.isAssignableFrom(componentClass)) {
            return EltilandSession.get().isSignedIn();
        }
        // we may try to chain this call to role checking authorization provider or whatever is below.
        return true;
    }

    /**
     * Skipping check in here. This strategy just passes this through to RoleBasedAuthorizationStrategy.
     */
    @Override
    public boolean isActionAuthorized(Component component, Action action) {
        return true;
    }
}
