package org.example.security;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
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
        if (!"GOOGLE".equals(auth.getDetails().toString())) {
            String username = auth.getName();
            String password = auth.getCredentials().toString();
            String encodedPassword = passwordEncoder.encode(password);
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            String userDetailsPassword = userDetails.getPassword();
            boolean is = encodedPassword.equals(userDetailsPassword);
            if (passwordEncoder.matches(password, userDetails.getPassword())) {
                UsernamePasswordAuthenticationToken token =
                        new UsernamePasswordAuthenticationToken
                        (userDetails, password, userDetails.getAuthorities());
                token.setDetails("DatabaseUser");
                return token;
            } else {
                throw new BadCredentialsException("Incorrect password");
            }
        }
        return null;
    }

    @Override
    public boolean supports(Class<?> auth) {
        return auth.equals(UsernamePasswordAuthenticationToken.class);
    }
}
