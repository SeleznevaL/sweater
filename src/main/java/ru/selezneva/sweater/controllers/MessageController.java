package ru.selezneva.sweater.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.selezneva.sweater.domain.Message;
import ru.selezneva.sweater.domain.User;
import ru.selezneva.sweater.dto.MessageDto;
import ru.selezneva.sweater.repos.MessageRepo;
import ru.selezneva.sweater.repos.UserRepo;
import ru.selezneva.sweater.security.CustomUserDetails;

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

    final private MessageRepo messageRepo;
    final private UserRepo userRepo;

    @GetMapping("/main")
    public String main(Model model) {
        User user = currentUser();
        Iterable<Message> all = messageRepo.findAll();
        model.addAttribute("messages", all);
        model.addAttribute("username", user.getUserName());
        return "main";
    }

    @PostMapping("/main")
    public String addMessage(@RequestParam String text, @RequestParam String tag, Model model){
        User user = currentUser();
        Message message = new Message().setTag(tag).setText(text).setTime(Timestamp.from(Instant.now())).setUser(user);
        messageRepo.save(message);
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
        Iterable<Message> all;
        if (filter != null && !filter.isEmpty()) {
            all = messageRepo.findByTag(filter);
            Cookie cookie = new Cookie("filter", filter);
            response.addCookie(cookie);
            model.addAttribute("filter", filter);
        } else {
            all = messageRepo.findAll();
            model.addAttribute("filter", null);
            Cookie cookie = new Cookie("filter", "");
            response.addCookie(cookie);
        }
        List<MessageDto> messages = new ArrayList<>();
        for(Message m : all) {
            messages.add(new MessageDto(m));
        }
        messages.sort((o1, o2) -> o2.getTime().compareTo(o1.getTime()));
        model.addAttribute("messages", messages);
        return "messages";
    }

    @GetMapping("/filter")
    public String update(
            Model model,
            @CookieValue(value = "filter", required = false) Cookie filterCookie,
            HttpServletResponse response
    ) {
        Iterable<Message> all;
        if(filterCookie != null && !filterCookie.getValue().isEmpty()) {
            all = messageRepo.findByTag( filterCookie.getValue());
            model.addAttribute("filter", filterCookie.getValue());
        } else {
            all = messageRepo.findAll();
            model.addAttribute("filter", null);
            Cookie cookie = new Cookie("filter", "");
            response.addCookie(cookie);
        }
        List<MessageDto> messages = new ArrayList<>();
        for(Message m : all) {
            messages.add(new MessageDto(m));
        }
        messages.sort((o1, o2) -> o2.getTime().compareTo(o1.getTime()));
        model.addAttribute("messages", messages);
        return "messages";
    }

    @GetMapping("/messages")
    public String messages(Model model) {
        Iterable<Message> all = messageRepo.findAll();
        List<MessageDto> messages = new ArrayList<>();
        for(Message m : all) {
            messages.add(new MessageDto(m));
        }
        messages.sort((o1, o2) -> o2.getTime().compareTo(o1.getTime()));
        model.addAttribute("messages", messages);
        return "messages";
    }

    private User currentUser() {
        Object user = SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();
        if (user instanceof CustomUserDetails) {
            CustomUserDetails userDetails = (CustomUserDetails) user;
            return userRepo.getOne(userDetails.getId());
        }
        return null;
    }

}
