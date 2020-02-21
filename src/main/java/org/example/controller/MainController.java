package org.example.controller;

import org.example.model.Role;
import org.example.model.User;
import org.example.service.AuthServiceGoogle;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Controller
public class MainController {

    private AuthServiceGoogle authServiceGoogle;

    public MainController(AuthServiceGoogle authServiceGoogle) {
        this.authServiceGoogle = authServiceGoogle;
    }

    @GetMapping("/admin/users")
    public String adminPage(Authentication authentication,
                            Model model) {
        User user = (User) authentication.getPrincipal();
        model.addAttribute("user", user);
        return "admin";
    }

    @GetMapping("/user")
    public String userPage(Authentication authentication,
                           Model model) {
        User user = (User) authentication.getPrincipal();
        model.addAttribute("user", user);
        return "user";
    }

    @RequestMapping({"/login", "/"})
    public String login() {
        return "loginPage";
    }

    @GetMapping("/googleLogin")
    public String googleLoginPage() {
        String authorizationUrl = authServiceGoogle.getAuthorizationUrl();
        return "redirect:" + authorizationUrl;
    }

    @GetMapping("/googleLogin/process")
    public String googleLoginProcess(String code) throws InterruptedException, ExecutionException, IOException {
        authServiceGoogle.putGoogleUserInSecurityContext(code);
        return "redirect:/login/success";
    }

    @GetMapping({"/login/success", "/"})
    public String loginSuccess(Authentication authentication, Model model) {
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
