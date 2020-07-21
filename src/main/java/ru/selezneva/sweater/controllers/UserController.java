package ru.selezneva.sweater.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.selezneva.sweater.entity.User;
import ru.selezneva.sweater.repos.UserRepo;
import ru.selezneva.sweater.security.UserRole;

import java.util.Collections;

@Controller
@RequiredArgsConstructor
public class UserController {

    final private UserRepo userRepo;

    @GetMapping
    public String greeting() {
        return "greeting";
    }

    @GetMapping("/reg")
    public String regForm() {
        return "reg";
    }

    @PostMapping("/reg")
    public String reg(@RequestParam String username, @RequestParam String password) {
        User user = new User().setUserName(username).setActive(true).setPassword(password).setRoles(Collections.singleton(UserRole.USER));
        userRepo.save(user);
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String loginForm() {
        return "login";
    }
}
