package ru.selezneva.sweater.controllers;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.selezneva.sweater.security.CustomUserDetails;
import ru.selezneva.sweater.security.UserRole;
import ru.selezneva.sweater.service.UserService;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Controller
public class AdminController {
    private final UserService userService;

    public AdminController(UserService userService){
        this.userService = userService;
    }

    @GetMapping("/admin")
    public String admin(
            Model model,
            @AuthenticationPrincipal CustomUserDetails userDetails
            ) {
        model.addAttribute("users", userService.findAll());
        model.addAttribute("username", userDetails.getUsername());
        model.addAttribute("roles", UserRole.values());
        return "admin";
    }

    @PostMapping("/edit")
    public String edit(
            @RequestParam Long id,
            Model model,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam Map<String, Object> param
    ) {
        Set<UserRole> roles = new HashSet<>();
        for (UserRole userRole: UserRole.values()) {
            if (param.containsKey(userRole.toString())) {
                roles.add(userRole);
            }
        }
        userService.updateRoles(id, roles);
        model.addAttribute("users", userService.findAll());
        model.addAttribute("username", userDetails.getUsername());
        model.addAttribute("roles", UserRole.values());
        return "admin";
    }
}
