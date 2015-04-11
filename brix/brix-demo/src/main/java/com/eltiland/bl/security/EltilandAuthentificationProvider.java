package com.eltiland.bl.security;

import com.eltiland.bl.utils.HashesUtils;
import com.eltiland.model.user.User;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Eltiland implementation of the Spring Authentication Provider.
 * <p/>
 * Takes user with roles from database.
 */
public class EltilandAuthentificationProvider implements AuthenticationProvider {
    @Autowired
    private SessionFactory sessionFactory;


    private static final Logger LOGGER = LoggerFactory.getLogger(EltilandAuthentificationProvider.class);

    @Override
    @Transactional(readOnly = true)
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        UsernamePasswordAuthenticationToken authToken = (UsernamePasswordAuthenticationToken) authentication;
        String email = (String) authToken.getPrincipal();
        String password = (String) authToken.getCredentials();

        if ("".equals(email)) {
            LOGGER.debug("Empty email, not logging in.");
            return authentication;
        }
        if ("".equals(password)) {
            LOGGER.debug("Empty password, exiting.");
            return authentication;
        }
        //load user information
        Query q = sessionFactory.getCurrentSession().createQuery(""
                + " select distinct u from User u "
                + " where u.email=:email "
                + "     and u.password=:passwordSHA1hex"
                + "     and u.confirmationDate!=null");
        q.setParameter("email", email);
        q.setParameter("passwordSHA1hex", HashesUtils.getSHA1inHex(password));

        List<User> users = q.list();

        if (users.size() == 0) {
            LOGGER.debug("No user found with '{}' email and password given.", email);
            return authentication;
        }

        if (users.size() > 1) {
            LOGGER.warn("Found 2 users with the same initials - '{}'!", email);
            return authentication;
        }

        User user = users.get(0);

        EltilandAuthentification eltilandAuthentification = new EltilandAuthentification(null);

        eltilandAuthentification.setAuthenticated(true);
        eltilandAuthentification.setDetails(user);

        //erase fetched permissions to prevent serialisation error
        sessionFactory.getCurrentSession().evict(user);


        LOGGER.info("User Successfull Login. User: (initials: {} id: {})",
                new Object[]{user.getEmail(), user.getId()});


        return eltilandAuthentification;
    }


    @Override
    public boolean supports(Class<? extends Object> authentication) {
        return authentication == UsernamePasswordAuthenticationToken.class;
    }
}
