package ru.selezneva.sweater.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.selezneva.sweater.dto.UserDto;
import ru.selezneva.sweater.entity.User;
import ru.selezneva.sweater.repos.UserRepo;
import ru.selezneva.sweater.security.UserRole;
import ru.selezneva.sweater.service.UserService;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class UserController {

    final private UserService userService;

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
        Map<UserRole, Boolean> roles = new HashMap<>();
        roles.put(UserRole.USER, true);
        UserDto user = new UserDto().setUserName(username).setActive(true).setPassword(password).setRoles(roles);
        userService.save(user);
        return "redirect:/login";
    }
}
