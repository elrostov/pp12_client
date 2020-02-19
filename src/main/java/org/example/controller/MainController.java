package org.example.controller;

import org.example.model.Role;
import org.example.model.User;
import org.example.security.DBAuthenticationProvider;
import org.example.security.GoogleAuthenticationProvider;
import org.example.service.AuthServiceGoogle;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Controller
public class MainController {

    private AuthServiceGoogle authServiceGoogle;
    private GoogleAuthenticationProvider googleAuthenticationProvider;
    private DBAuthenticationProvider dbAuthenticationProvider;

    public MainController(AuthServiceGoogle authServiceGoogle, GoogleAuthenticationProvider googleAuthenticationProvider, DBAuthenticationProvider dbAuthenticationProvider) {
        this.authServiceGoogle = authServiceGoogle;
        this.googleAuthenticationProvider = googleAuthenticationProvider;
        this.dbAuthenticationProvider = dbAuthenticationProvider;
    }

    @GetMapping("/admin/users")
    public String adminPage(){
        return "admin";
    }

    @GetMapping("/user")
    public String userPage(){
        return "user";
    }

    @GetMapping("/login")
    public String loginPage(){
        return "loginPage";
    }

    @GetMapping("/googleLogin")
    public String googleLoginPage(){
        String authorizationUrl = authServiceGoogle.getService().getAuthorizationUrl();
        return "redirect:" + authorizationUrl;
    }

    @GetMapping("/googleLogin/process")
    public String googleLoginProcess(String code, Model model) throws InterruptedException, ExecutionException, IOException {
        User user = authServiceGoogle.getGoogleUser(code);
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                new UsernamePasswordAuthenticationToken(user, user.getPassword());
        Authentication auth = googleAuthenticationProvider.authenticate(usernamePasswordAuthenticationToken);
        SecurityContext sc = SecurityContextHolder.getContext();
        sc.setAuthentication(auth);
        model.addAttribute("user", user);
        return "user";
    }

    @PostMapping("/login/process")
    public String loginProcess(String username, String password, Model model){
        UsernamePasswordAuthenticationToken userAuth =
                new UsernamePasswordAuthenticationToken(username, password);
        Authentication authenticated = dbAuthenticationProvider.authenticate(userAuth);
        SecurityContext sc = SecurityContextHolder.getContext();
        sc.setAuthentication(authenticated);
        User user = (User) authenticated.getPrincipal();
        model.addAttribute("user", user);
        return "redirect:/login/success";
    }

    @GetMapping({"/login/success", "/"})
    public String loginSuccess(Authentication authentication){
        User user = (User) authentication.getPrincipal();
        if (user.getRoles().stream()
                .map(Role::getName)
                .collect(Collectors.toList())
                .contains("ADMIN")) {
            return "redirect:/admin/users";
        }
        return "redirect:/user";
    }
}
