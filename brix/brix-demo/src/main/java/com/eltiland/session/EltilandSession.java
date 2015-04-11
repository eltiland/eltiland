package com.eltiland.session;

import com.eltiland.bl.security.EltilandAuthentification;
import com.eltiland.model.user.User;
import org.apache.wicket.Session;
import org.apache.wicket.authroles.authentication.AuthenticatedWebSession;
import org.apache.wicket.authroles.authorization.strategies.role.Roles;
import org.apache.wicket.injection.Injector;
import org.apache.wicket.request.Request;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Properties;

/**
 * Eltiland-related session.
 * Type-safe way to handle our sessions.
 *
 * @author Aleksey Plotnikov
 */
public class EltilandSession extends AuthenticatedWebSession {
    private static final Logger LOGGER = LoggerFactory.getLogger(EltilandSession.class);

    @SpringBean(name = "authenticationManager")
    private AuthenticationManager authenticationManager;
    @SpringBean(name = "eltilandProperties")
    private Properties eltilandProperties;

    public EltilandSession(Request request) {
        super(request);
        Injector.get().inject(this);
    }

    /**
     * Get current session from ThreadLocal already cast to EltilandSession.
     *
     * @return current user's session
     */
    public static EltilandSession get() {
        return (EltilandSession) Session.get();
    }

    /**
     * Returns current user after the successful login.
     *
     * @return current user. The same principal is linked to Spring Security so it will be checked against for the
     *         business method calls.
     */
    public User getCurrentUser() {
        if (isSignedIn()) {
            LOGGER.debug(
                    "Getting current user from Spring Security context. Thread is {}, auth is {}",
                    Thread.currentThread(),
                    SecurityContextHolder.getContext().getAuthentication());
            Object user = SecurityContextHolder.getContext().getAuthentication().getDetails();
            if (user instanceof User) {
                return (User) user;
            } else {
                return null;
            }
            //return (User) SecurityContextHolder.getContext().getAuthentication().getDetails();
        }
        return null;
    }

    /**
     * Update user object in SecurityContextHolder.
     *
     * @param updatedUser same user object (by id)
     * @return updated user object from SecurityContextHolder
     */
    public User updateCurrentUser(User updatedUser) {
        if (isSignedIn()) {
            LOGGER.debug(
                    "Updating current user in Spring Security context. Thread is {}, auth is {}",
                    Thread.currentThread(),
                    SecurityContextHolder.getContext().getAuthentication());

            User contextUser = (User) SecurityContextHolder.getContext().getAuthentication().getDetails();

            if (updatedUser == null) {
                throw new IllegalStateException("Cannot update EltilandAuthentification object for non-existed updated user object!");
            }

            if (contextUser == null) {
                throw new IllegalStateException("Cannot update EltilandAuthentification object for non-existed context user object!");
            }

            if (!contextUser.getId().equals(updatedUser.getId())) {
                throw new IllegalStateException("Cannot update EltilandAuthentification object, user not are same objects!");
            }

            ((EltilandAuthentification) SecurityContextHolder.getContext().getAuthentication()).setDetails(updatedUser);

            return getCurrentUser();
        }
        return null;
    }

    @Override
    public boolean authenticate(String username, String password) {
        boolean authenticated = false;
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, password)
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);
            authenticated = authentication.isAuthenticated();
        } catch (AuthenticationException e) {
            LOGGER.warn("User '{}' failed to login. Reason: {}", username, e.getMessage());
            LOGGER.debug("Details of the failed authentication: {}", e);
            authenticated = false;
        }
        return authenticated;
    }

    @Override
    public Roles getRoles() {
        Roles roles = new Roles();
        if (!isSignedIn()) {
            return roles;
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        for (GrantedAuthority authority : authentication.getAuthorities()) {
            roles.add(authority.getAuthority());
        }
        return roles;
    }

    @Override
    public void signOut() {
        if (getCurrentUser() != null) {
            SecurityContextHolder.getContext().setAuthentication(null);
            signIn(false);
            invalidate();
        }
    }
}
