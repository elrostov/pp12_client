package org.example.security;

import org.example.model.User;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

@Component
public class GoogleAuthenticationProvider implements AuthenticationProvider {

    @Override
    public Authentication authenticate(Authentication auth)
            throws AuthenticationException {
        User user = (User) auth.getPrincipal();
        String password = auth.getCredentials()
                .toString();
        if ("Google user".equals(password)) {
            return new UsernamePasswordAuthenticationToken
                    (user, password, user.getAuthorities());
        } else {
            return null;
        }
    }

    @Override
    public boolean supports(Class<?> auth) {
        return auth.equals(UsernamePasswordAuthenticationToken.class);
    }
}