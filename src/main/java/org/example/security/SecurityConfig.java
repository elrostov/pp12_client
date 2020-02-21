package org.example.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
@EnableWebSecurity
@ComponentScan("org.example")
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private GoogleAuthenticationProvider googleAuthenticationProvider;
    private DBAuthenticationProvider dbAuthenticationProvider;

    @Autowired
    public SecurityConfig(GoogleAuthenticationProvider googleAuthenticationProvider,
                          DBAuthenticationProvider dbAuthenticationProvider) {
        this.googleAuthenticationProvider = googleAuthenticationProvider;
        this.dbAuthenticationProvider = dbAuthenticationProvider;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .authorizeRequests()
                .antMatchers("/admin/**", "/api/**").hasAuthority("ADMIN")
                .antMatchers("/user").hasAuthority("USER")
                .antMatchers("/googleLogin", "/googleLogin/process").anonymous()
                .antMatchers("/resources/**", "/static/**").permitAll()
                .anyRequest().authenticated()
            .and()
                .formLogin()
                .loginPage("/login")
                .defaultSuccessUrl("/login/success")
                .permitAll()
            .and()
                .logout()
                .permitAll();

        http.csrf().disable(); //Что делает csrf: Когда пользователем запрашивается форма,
        //сервер отдает её, прикрепляя токен, затем, когда от пользователя возвращается
        //заполненная форма, сервер проверяет, тот ли токен пришел с этой формой
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) {
        auth.authenticationProvider(dbAuthenticationProvider);
        auth.authenticationProvider(googleAuthenticationProvider);
    }
}
