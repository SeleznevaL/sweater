package ru.selezneva.sweater.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.selezneva.sweater.dto.UserDto;
import ru.selezneva.sweater.entity.Message;
import ru.selezneva.sweater.entity.User;
import ru.selezneva.sweater.dto.MessageDto;
import ru.selezneva.sweater.repos.MessageRepo;
import ru.selezneva.sweater.repos.UserRepo;
import ru.selezneva.sweater.security.CustomUserDetails;
import ru.selezneva.sweater.service.MessageService;
import ru.selezneva.sweater.service.UserService;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class MessageController {

    final private MessageService messageService;
    final private UserService userService;

    @GetMapping("/main")
    public String main(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        UserDto user = userService.getById(userDetails.getId());
        List<MessageDto> all = messageService.findAll();
        model.addAttribute("messages", all);
        model.addAttribute("username", user.getUserName());
        return "main";
    }

    @PostMapping("/main")
    public String addMessage(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam String text,
            @RequestParam String tag,
            Model model){
        UserDto user = userService.getById(userDetails.getId());
        MessageDto message = new MessageDto().setTag(tag).setText(text).setUserName(user.getUserName());
        messageService.save(message);
        model.addAttribute("username", user.getUserName());
        return "main";
    }

    @PostMapping("/filter")
    public String filter(
            @RequestParam String filter,
            Model model,
            HttpServletResponse response,
            HttpServletRequest request
    ) {
        List<MessageDto> all;
        if (filter != null && !filter.isEmpty()) {
            all = messageService.findByTag(filter);
            Cookie cookie = new Cookie("filter", filter);
            response.addCookie(cookie);
            model.addAttribute("filter", filter);
        } else {
            all = messageService.findAll();
            model.addAttribute("filter", null);
            Cookie cookie = new Cookie("filter", "");
            response.addCookie(cookie);
        }
        all.sort((o1, o2) -> o2.getTime().compareTo(o1.getTime()));
        model.addAttribute("messages", all);
        return "messages";
    }

    @GetMapping("/filter")
    public String update(
            Model model,
            @CookieValue(value = "filter", required = false) Cookie filterCookie,
            HttpServletResponse response
    ) {
        List<MessageDto> all;
        if(filterCookie != null && !filterCookie.getValue().isEmpty()) {
            all = messageService.findByTag( filterCookie.getValue());
            model.addAttribute("filter", filterCookie.getValue());
        } else {
            all = messageService.findAll();
            model.addAttribute("filter", null);
            Cookie cookie = new Cookie("filter", "");
            response.addCookie(cookie);
        }
        all.sort((o1, o2) -> o2.getTime().compareTo(o1.getTime()));
        model.addAttribute("messages", all);
        return "messages";
    }

    @GetMapping("/messages")
    public String messages(Model model) {
        List<MessageDto> all = messageService.findAll();
        all.sort((o1, o2) -> o2.getTime().compareTo(o1.getTime()));
        model.addAttribute("messages", all);
        return "messages";
    }
}
