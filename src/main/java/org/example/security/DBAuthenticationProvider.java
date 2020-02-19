package org.example.security;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DBAuthenticationProvider implements AuthenticationProvider {

    private UserDetailsService userDetailsService;
    private PasswordEncoder passwordEncoder;

    public DBAuthenticationProvider(PasswordEncoder passwordEncoder,
                                    @Qualifier("userDetailsServiceImpl") UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Authentication authenticate(Authentication auth)
            throws AuthenticationException {
        String username = auth.getName();
        String password = auth.getCredentials()
                .toString();

        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        if (userDetails != null) {
            if (passwordEncoder.matches(password, userDetails.getPassword())) {
                return new UsernamePasswordAuthenticationToken
                        (userDetails, password, userDetails.getAuthorities());
            } else {
                throw new BadCredentialsException("Incorrect password");
            }
        } else {
            throw new BadCredentialsException("Incorrect username");
        }
    }

    @Override
    public boolean supports(Class<?> auth) {
        return auth.equals(UsernamePasswordAuthenticationToken.class);
    }
}
