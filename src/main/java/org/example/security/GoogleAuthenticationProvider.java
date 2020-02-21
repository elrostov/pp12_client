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
        if ("GOOGLE".equals(auth.getDetails().toString())) {
            User user = (User) auth.getPrincipal();
            return new UsernamePasswordAuthenticationToken
                    (user, user.getPassword(), user.getAuthorities());
        } else {
            return null;
        }
    }

    @Override
    public boolean supports(Class<?> auth) {
        return auth.equals(UsernamePasswordAuthenticationToken.class);
    }
}